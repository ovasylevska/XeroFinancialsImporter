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
            Flyway flyway = Flyway.configure()
                    .dataSource(financialsDataSource)
                    .locations(new Location("database/xero_financials"), new Location("database/xero_importer"))
                    .schemas("bank_transactions")
                    .baselineOnMigrate(true)
                    .load();
            logger.info("Starting database migration for 'financials' and 'importer' data sources...");
            executeFlywayAction(flyway, financialsAction);
            return flyway;
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
                .locations(new Location("database/xero_financials"))
                .schemas("bank_transactions")
                .baselineOnMigrate(true)
                .load();
        logger.info("Starting database migration for 'xero_financials' database...");
        executeFlywayAction(flyway, action);
        return flyway;
    }

    private Flyway initImportersDatabase(DataSource datasource, String action) {
        final Flyway flyway = Flyway.configure()
                .dataSource(datasource)
                .locations(new Location("database/xero_importer"))
                .schemas("bank_transactions")
                .baselineOnMigrate(true)
                .load();
        logger.info("Starting database migration for 'xero_importer' database...");
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
