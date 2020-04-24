package com.xerofinancials.importer.xeroauthorization;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.testing.http.FixedClock;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.xerofinancials.importer.repository.XeroAccountCredentialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

@Component
public class Authorization {
    private static final Logger logger = LoggerFactory.getLogger(Authorization.class);

    private static final String TOKEN_SERVER_URL = "https://identity.xero.com/connect/token";
    private static final String AUTHORIZATION_SERVER_URL = "https://login.xero.com/identity/connect/authorize";

    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final String secretState = "secret" + new Random().nextInt(999_999);

    private final XeroAccountCredentialsRepository accountCredentialsRepository;

    public Authorization(final XeroAccountCredentialsRepository accountCredentialsRepository) {
        this.accountCredentialsRepository = accountCredentialsRepository;
    }

    public String getAuthorizationRedirect() throws IOException {
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
                .setClock(new FixedClock())
                .build();

        return flow.newAuthorizationUrl()
                .setClientId(accountCredentialsRepository.getClientId())
                .setScopes(scopeList)
                .setState(secretState)
                .setRedirectUri(accountCredentialsRepository.getRedirectURI())
                .build();
    }

    private ArrayList<String> getScope() {
        final ArrayList<String> scopeList = new ArrayList<String>();
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
