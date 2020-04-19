package com.xerofinancials.importer.xeroauthorization;

import com.xerofinancials.importer.beans.XeroTokens;
import com.xerofinancials.importer.repository.XeroTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class TokenStorage {
    public static final String JWT_TOKEN = "jwt_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String TENANT_ID = "xero_tenant_id";
    public static final String EXPIRES_IN_SECONDS = "expires_in_seconds";
    private final XeroTokenRepository tokenRepository;

    private final Map<String, String> TOKEN_STORAGE = new HashMap<>();

    public TokenStorage(final XeroTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;

        Optional<XeroTokens> xeroTokens = tokenRepository.get();
        if (xeroTokens.isPresent()) {
            TOKEN_STORAGE.put(JWT_TOKEN, xeroTokens.get().getJwrToken());
            TOKEN_STORAGE.put(ACCESS_TOKEN, xeroTokens.get().getAccessToken());
            TOKEN_STORAGE.put(REFRESH_TOKEN, xeroTokens.get().getRefreshToken());
            TOKEN_STORAGE.put(TENANT_ID, xeroTokens.get().getTenantId());
            TOKEN_STORAGE.put(EXPIRES_IN_SECONDS, xeroTokens.get().getExpiresInSeconds());
        }
    }

    public boolean isAuthentificated() {
        if (TOKEN_STORAGE.get(ACCESS_TOKEN) == null) {
            return false;
        }
        if (TOKEN_STORAGE.get(TENANT_ID) == null) {
            return false;
        }
        return true;
    }

    public String get(String key) {
        return TOKEN_STORAGE.get(key);
    }

    void save(String key, String value) {
        TOKEN_STORAGE.put(key, value);
    }

    void dump() {
        XeroTokens xeroTokens = new XeroTokens();
        xeroTokens.setJwrToken(TOKEN_STORAGE.get(JWT_TOKEN));
        xeroTokens.setAccessToken(TOKEN_STORAGE.get(ACCESS_TOKEN));
        xeroTokens.setRefreshToken(TOKEN_STORAGE.get(REFRESH_TOKEN));
        xeroTokens.setTenantId(TOKEN_STORAGE.get(TENANT_ID));
        xeroTokens.setExpiresInSeconds(TOKEN_STORAGE.get(EXPIRES_IN_SECONDS));
        tokenRepository.update(xeroTokens);
    }
}
