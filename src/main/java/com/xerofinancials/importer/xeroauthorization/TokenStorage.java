package com.xerofinancials.importer.xeroauthorization;

import com.xerofinancials.importer.beans.XeroTokens;
import com.xerofinancials.importer.repository.XeroTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TokenStorage {
    public static final String JWT_TOKEN = "jwt_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String TENANT_ID = "xero_tenant_id";
    public static final String EXPIRES_IN_SECONDS = "expires_in_seconds";
    private final XeroTokenRepository tokenRepository;

    public TokenStorage(final XeroTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public boolean isAuthentificated() {
        final Optional<XeroTokens> xeroTokens = tokenRepository.get();
        if (!xeroTokens.isPresent()) {
            return false;
        }
        if (isEmpty(xeroTokens.get().getAccessToken())) {
            return false;
        }
        if (isEmpty(xeroTokens.get().getTenantId())) {
            return false;
        }
        return true;
    }

    public String get(String key) {
        final Optional<XeroTokens> xeroTokens = tokenRepository.get();
        if (!xeroTokens.isPresent()) {
            return null;
        }
        if (key.equals(JWT_TOKEN)) {
            return xeroTokens.get().getJwrToken();
        }
        if (key.equals(ACCESS_TOKEN)) {
            return xeroTokens.get().getAccessToken();
        }
        if (key.equals(REFRESH_TOKEN)) {
            return xeroTokens.get().getRefreshToken();
        }
        if (key.equals(TENANT_ID)) {
            return xeroTokens.get().getTenantId();
        }
        if (key.equals(EXPIRES_IN_SECONDS)) {
            return xeroTokens.get().getExpiresInSeconds();
        }
        return null;
    }

    public Optional<XeroTokens> get() {
        return tokenRepository.get();
    }

    public void save(String key, String value) {
        final XeroTokens xeroTokens = tokenRepository.get().orElse(new XeroTokens());
        if (key.equals(JWT_TOKEN)) {
            xeroTokens.setJwrToken(value);
        }
        if (key.equals(ACCESS_TOKEN)) {
            xeroTokens.setAccessToken(value);
        }
        if (key.equals(REFRESH_TOKEN)) {
            xeroTokens.setRefreshToken(value);
        }
        if (key.equals(TENANT_ID)) {
            xeroTokens.setTenantId(value);
        }
        if (key.equals(EXPIRES_IN_SECONDS)) {
            xeroTokens.setExpiresInSeconds(value);
        }
        tokenRepository.update(xeroTokens);
    }

    public void save(XeroTokens xeroTokens) {
        tokenRepository.update(xeroTokens);
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

}
