package com.xerofinancials.importer.xeroapi;

import com.xero.api.ApiClient;
import com.xero.api.client.AccountingApi;
import com.xerofinancials.importer.xeroauthorization.TokenRefresh;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class XeroApiWrapper {
    private static final Logger logger = LoggerFactory.getLogger(XeroApiWrapper.class);

    private final TokenStorage tokenStorage;
    private final TokenRefresh tokenRefresh;
    private final ApiClient defaultClient = new ApiClient();
    private final AccountingApi accountingApi = AccountingApi.getInstance(defaultClient);
    private String accessToken;
    private String tenantId;

    public XeroApiWrapper(final TokenStorage tokenStorage, final TokenRefresh tokenRefresh) {
        this.tokenStorage = tokenStorage;
        this.tokenRefresh = tokenRefresh;
    }

    public <T> T executeApiCall(XeroApiCall<T> call) throws IOException {
        try {
            refreshTokenIfNeeded();
        } catch (IOException e) {
            logger.error("Exception while executing Xero api call : " + e.getMessage());
        }
        return call.execute(accountingApi, this.accessToken, this.tenantId);
    }

    private void refreshTokenIfNeeded() throws IOException {
        final String savedAccessToken = tokenStorage.get(TokenStorage.ACCESS_TOKEN);
        final String savedRefreshToken = tokenStorage.get(TokenStorage.REFRESH_TOKEN);
        this.accessToken = tokenRefresh.getAndRefreshIfNeeded(savedAccessToken, savedRefreshToken);
        this.tenantId = tokenStorage.get(TokenStorage.TENANT_ID);
    }
}
