package com.xerofinancials.importer.xeroauthorization;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.xero.api.ApiClient;
import com.xero.api.client.IdentityApi;
import com.xero.models.identity.Connection;
import com.xerofinancials.importer.repository.XeroAccountCredentialsRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CallbackHandler {
    private static final String TOKEN_SERVER_URL = "https://identity.xero.com/connect/token";
    private static final String AUTHORIZATION_SERVER_URL = "https://login.xero.com/identity/connect/authorize";
    private static final String XERO_API_URL = "https://api.xero.com";
    private final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final JsonFactory JSON_FACTORY = new JacksonFactory();

    private final TokenStorage tokenStorage;
    private final XeroAccountCredentialsRepository accountCredentialsRepository;

    public CallbackHandler(
            final TokenStorage tokenStorage,
            final XeroAccountCredentialsRepository accountCredentialsRepository
    ) {
        this.tokenStorage = tokenStorage;
        this.accountCredentialsRepository = accountCredentialsRepository;
    }

    public void extractTokenInfo(Map<String,String> requestParams) throws IOException {
        final TokenResponse tokenResponse = getTokenResponse(requestParams);
        final String tenantId = getTenantId(tokenResponse);
        tokenStorage.save(TokenStorage.ACCESS_TOKEN, tokenResponse.getAccessToken());
        tokenStorage.save(TokenStorage.REFRESH_TOKEN, tokenResponse.getRefreshToken());
        tokenStorage.save(TokenStorage.TENANT_ID, tenantId);
    }

    private String getTenantId(final TokenResponse tokenResponse) throws IOException {
        final GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setClientSecrets(
                        accountCredentialsRepository.getClientId(),
                        accountCredentialsRepository.getClientSecret()
                )
                .build();
        credential.setAccessToken(tokenResponse.getAccessToken());
        credential.setRefreshToken(tokenResponse.getRefreshToken());
        credential.setExpiresInSeconds(tokenResponse.getExpiresInSeconds());

        final HttpTransport transport = new NetHttpTransport();
        final HttpRequestFactory requestFactory = transport.createRequestFactory(credential);
        final ApiClient defaultClient = new ApiClient(XERO_API_URL, null, null, null, requestFactory);
        final IdentityApi idApi = new IdentityApi(defaultClient);
        final List<Connection> connections = idApi.getConnections(tokenResponse.getAccessToken());
        return connections.get(0).getTenantId().toString();
    }

    private TokenResponse getTokenResponse(Map<String,String> requestParams) throws IOException {
        final String code = getCodeFromCallback(requestParams);
        final ArrayList<String> scopeList = getScope();

        final AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT,
                JSON_FACTORY,
                new GenericUrl(TOKEN_SERVER_URL),
                new ClientParametersAuthentication(
                        accountCredentialsRepository.getClientId(),
                        accountCredentialsRepository.getClientSecret()
                ),
                accountCredentialsRepository.getClientId(),
                AUTHORIZATION_SERVER_URL
        )
                .setScopes(scopeList)
                .setDataStoreFactory(new MemoryDataStoreFactory())
                .build();

        return flow
                .newTokenRequest(code)
                .setRedirectUri(accountCredentialsRepository.getRedirectURI())
                .execute();
    }

    private String getCodeFromCallback(Map<String,String> requestParams) {
        return requestParams.get("code");
    }

    private ArrayList<String> getScope() {
        final ArrayList<String> scopeList = new ArrayList<>();
        scopeList.add("openid");
        scopeList.add("email");
        scopeList.add("profile");
        scopeList.add("offline_access");
        scopeList.add("accounting.settings");
        scopeList.add("accounting.transactions");
        scopeList.add("accounting.contacts");
        scopeList.add("accounting.journals.read");
        scopeList.add("accounting.reports.read");
        scopeList.add("accounting.attachments");
        return scopeList;
    }
}
