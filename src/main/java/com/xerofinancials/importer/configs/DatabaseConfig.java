package com.xerofinancials.importer.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public JdbcTemplate financialsJdbcTemplate(@Qualifier("financialsDataSource") DataSource datasource) {
        return new JdbcTemplate(datasource);
    }

    @Bean
    public DataSource financialsDataSource(
            @Value("${database.xero_financials.driverClassName}") String driver,
            @Value("${database.xero_financials.url}") String url,
            @Value("${database.xero_financials.username}") String username,
            @Value("${database.xero_financials.password}") String password
    ) {
        return getDataSource(driver, url, username, password);
    }

    @Bean
    public JdbcTemplate importerJdbcTemplate(@Qualifier("importerDataSource") DataSource datasource) {
        return new JdbcTemplate(datasource);
    }

    @Bean
    public DataSource importerDataSource(
            @Value("${database.xero_importer.driverClassName}") String driver,
            @Value("${database.xero_importer.url}") String url,
            @Value("${database.xero_importer.username}") String username,
            @Value("${database.xero_importer.password}") String password
    ) {
        return getDataSource(driver, url, username, password);
    }

    private DataSource getDataSource(String driver, String url, String username, String password) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
