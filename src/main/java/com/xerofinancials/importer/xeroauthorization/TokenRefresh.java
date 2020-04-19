package com.xerofinancials.importer.xeroauthorization;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.xerofinancials.importer.repository.XeroAccountCredentialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TokenRefresh {
    private static final Logger logger = LoggerFactory.getLogger(TokenRefresh.class);

    private static final String TOKEN_SERVER_URL = "https://identity.xero.com/connect/token";
    private final TokenStorage tokenStorage;
    private final XeroAccountCredentialsRepository accountCredentialsRepository;

    public TokenRefresh(
            final TokenStorage tokenStorage,
            final XeroAccountCredentialsRepository accountCredentialsRepository
    ) {
        this.tokenStorage = tokenStorage;
        this.accountCredentialsRepository = accountCredentialsRepository;
    }

    public String getAndRefreshIfNeeded(String accessToken, String refreshToken) throws IOException {
        if (accessToken == null || refreshToken == null) {
            return null;
        }

        try {
            final DecodedJWT jwt = JWT.decode(accessToken);
            if (jwt.getExpiresAt().getTime() > System.currentTimeMillis()) {
                return accessToken;
            }
        } catch (JWTDecodeException exception) {
            logger.error("Error: " + exception.getMessage());
        }

        try {
            final TokenResponse tokenResponse = new RefreshTokenRequest(
                    new NetHttpTransport(),
                    new JacksonFactory(),
                    new GenericUrl(TOKEN_SERVER_URL),
                    refreshToken
            )
                    .setClientAuthentication(new BasicAuthentication(
                            accountCredentialsRepository.getClientId(),
                            accountCredentialsRepository.getClientSecret()
                    ))
                    .execute();

            tokenStorage.save(TokenStorage.JWT_TOKEN, tokenResponse.toPrettyString());
            tokenStorage.save(TokenStorage.ACCESS_TOKEN, tokenResponse.getAccessToken());
            tokenStorage.save(TokenStorage.REFRESH_TOKEN, tokenResponse.getRefreshToken());
            tokenStorage.save(TokenStorage.EXPIRES_IN_SECONDS, tokenResponse.getExpiresInSeconds().toString());
            tokenStorage.dump();

            logger.info("Access token was refreshed");
            return tokenResponse.getAccessToken();
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                logTokenResponseException(e);
            } else {
                logger.error("Exception while refreshing access token", e);
            }
            //todo: throw new custom exception
            throw new RuntimeException("Failed to refresh access token", e);
        }
    }

    private void logTokenResponseException(TokenResponseException e) {
        logger.error("Xero Token Response Exception: " + e.getDetails().getError());

        if (e.getDetails().getErrorDescription() != null) {
            logger.error(e.getDetails().getErrorDescription());
        }
        if (e.getDetails().getErrorUri() != null) {
            logger.error(e.getDetails().getErrorUri());
        }
    }
}
