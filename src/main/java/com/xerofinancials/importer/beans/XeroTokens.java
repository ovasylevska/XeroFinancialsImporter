package com.xerofinancials.importer.beans;

public class XeroTokens {
    private String jwrToken;
    private String accessToken;
    private String refreshToken;
    private String expiresInSeconds;
    private String tenantId;

    public String getJwrToken() {
        return jwrToken;
    }

    public void setJwrToken(final String jwrToken) {
        this.jwrToken = jwrToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(final String expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }
}
