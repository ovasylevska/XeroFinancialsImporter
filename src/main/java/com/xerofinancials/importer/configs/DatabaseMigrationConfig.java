package com.xerofinancials.importer.configs;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DatabaseMigrationConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

    @Bean
    public Flyway initDatabases(
            @Qualifier("financialsDataSource") DataSource financialsDataSource,
            @Value("${database.xero_financials.flywaymigration}") String financialsAction,
            @Qualifier("importerDataSource") DataSource importerDataSource,
            @Value("${database.xero_importer.flywaymigration}") String importerAction
    ) {
        if (isEqual(financialsDataSource, importerDataSource) && financialsAction.equals(importerAction)) {
            return initAllDatabases(financialsDataSource, financialsAction);
        } else {
            initFinancialsDatabase(financialsDataSource, financialsAction);
            return initImportersDatabase(importerDataSource, importerAction);
        }
    }

    private boolean isEqual(DataSource financialsDatasource, DataSource importerDatasource) {
        try {
            String financialsUrl = financialsDatasource.getConnection().getMetaData().getURL();
            String importerUrl = importerDatasource.getConnection().getMetaData().getURL();
            return financialsUrl.equals(importerUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Flyway initFinancialsDatabase(DataSource datasource, String action) {
        final Flyway flyway = Flyway.configure()
                .dataSource(datasource)
                .locations(
                        new Location("database/bank_transaction"),
                        new Location("database/account")
                )
                .schemas("bank_transactions", "accounts")
                .baselineOnMigrate(true)
                .load();
        logger.info("Starting database migration for 'financials' (account and bank transactions) data source...");
        executeFlywayAction(flyway, action);
        return flyway;
    }

    private Flyway initImportersDatabase(DataSource datasource, String action) {
        final Flyway flyway = Flyway.configure()
                .dataSource(datasource)
                .locations(new Location("database/importer"))
                .schemas("importer")
                .baselineOnMigrate(true)
                .load();
        logger.info("Starting database migration for 'importer' data source...");
        executeFlywayAction(flyway, action);
        return flyway;
    }

    private Flyway initAllDatabases(DataSource dataSource, String action) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(
                        new Location("database/importer"),
                        new Location("database/bank_transaction"),
                        new Location("database/account")
                )
                .schemas("importer", "bank_transactions", "accounts")
                .baselineOnMigrate(true)
                .load();
        logger.info("Starting database migration for 'financials' and 'importer' data sources...");
        executeFlywayAction(flyway, action);
        return flyway;
    }

    private void executeFlywayAction(Flyway flyway, String action) {
        if ("migrate".equals(action)) {
            flyway.migrate();
            return;
        }
        if ("clean".equals(action)) {
            flyway.clean();
            return;
        }
        logger.info("Unsupported flyway migration '" + action + "'");
    }
}
