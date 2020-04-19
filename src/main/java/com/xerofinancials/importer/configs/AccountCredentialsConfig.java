package com.xerofinancials.importer.configs;

import com.xerofinancials.importer.repository.XeroAccountCredentialsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountCredentialsConfig {

    @Bean
    public XeroAccountCredentialsRepository xeroAccountCredentialsRepository(
            @Value("${xero.clientId}") String clientId,
            @Value("${xero.clientSecret}") String clientSecret,
            @Value("${xero.redirectURI}") String redirectURI
    ) {
        return new XeroAccountCredentialsRepository(clientId, clientSecret, redirectURI);
    }
}
