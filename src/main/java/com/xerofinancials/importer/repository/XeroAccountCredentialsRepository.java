package com.xerofinancials.importer.repository;

public class XeroAccountCredentialsRepository {
    //todo: move to config or database
    private final String clientId;
    private final String clientSecret;
    private final String redirectURI;

    public XeroAccountCredentialsRepository(
            final String clientId,
            final String clientSecret,
            final String redirectURI
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectURI = redirectURI;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectURI() {
        return redirectURI;
    }
}
