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
            return initImporterDatabase(importerDataSource, importerAction);
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
        initAccountSchema(datasource, action);
        initBankTransactionSchema(datasource, action);
        return initInvoiceSchema(datasource, action);
    }

    private Flyway initBankTransactionSchema(DataSource datasource, String action) {
        final Flyway flyway = Flyway.configure()
                .dataSource(datasource)
                .locations(new Location("database/bank_transaction"))
                .schemas("bank_transactions")
                .baselineOnMigrate(true)
                .load();
        logger.info("Starting database migration for 'financials/bank transaction' data source...");
        executeFlywayAction(flyway, action);
        return flyway;
    }

    private Flyway initAccountSchema(DataSource datasource, String action) {
        final Flyway flyway = Flyway.configure()
                .dataSource(datasource)
                .locations(new Location("database/account"))
                .schemas("accounts")
                .baselineOnMigrate(true)
                .load();
        logger.info("Starting database migration for 'financials/account' data source...");
        executeFlywayAction(flyway, action);
        return flyway;
    }

    private Flyway initInvoiceSchema(DataSource datasource, String action) {
        final Flyway flyway = Flyway.configure()
                .dataSource(datasource)
                .locations(new Location("database/invoice"))
                .schemas("invoices")
                .baselineOnMigrate(true)
                .load();
        logger.info("Starting database migration for 'financials/invoice' data source...");
        executeFlywayAction(flyway, action);
        return flyway;
    }

    private Flyway initImporterDatabase(DataSource datasource, String action) {
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
        initAccountSchema(dataSource, action);
        initBankTransactionSchema(dataSource, action);
        initInvoiceSchema(dataSource, action);
        return initImporterDatabase(dataSource, action);
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
        if ("baseline".equals(action)) {
            flyway.baseline();
            return;
        }
        logger.info("Unsupported flyway migration '" + action + "'");
    }
}
