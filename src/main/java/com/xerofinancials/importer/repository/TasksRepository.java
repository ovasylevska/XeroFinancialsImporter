package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.enums.XeroDataType;
import com.xerofinancials.importer.tasks.AccountDeltaImportTask;
import com.xerofinancials.importer.tasks.AccountInitialImportTask;
import com.xerofinancials.importer.tasks.AccountReconciliationTask;
import com.xerofinancials.importer.tasks.BankTransactionDeltaImportTask;
import com.xerofinancials.importer.tasks.BankTransactionInitialImportTask;
import com.xerofinancials.importer.tasks.BankTransactionReconciliationTask;
import com.xerofinancials.importer.tasks.ImportTask;
import com.xerofinancials.importer.tasks.InvoiceInitialImportTask;
import com.xerofinancials.importer.tasks.PaymentInitialImportTask;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.xerofinancials.importer.repository.TasksRepository.ImportTaskIdentifier.ACCOUNT_DELTA;
import static com.xerofinancials.importer.repository.TasksRepository.ImportTaskIdentifier.ACCOUNT_INITIAL;
import static com.xerofinancials.importer.repository.TasksRepository.ImportTaskIdentifier.ACCOUNT_RECONCILIATION;
import static com.xerofinancials.importer.repository.TasksRepository.ImportTaskIdentifier.BANK_TRANSACTION_DELTA;
import static com.xerofinancials.importer.repository.TasksRepository.ImportTaskIdentifier.BANK_TRANSACTION_INITIAL;
import static com.xerofinancials.importer.repository.TasksRepository.ImportTaskIdentifier.BANK_TRANSACTION_RECONCILIATION;
import static com.xerofinancials.importer.repository.TasksRepository.ImportTaskIdentifier.INVOICE_INITIAL;
import static com.xerofinancials.importer.repository.TasksRepository.ImportTaskIdentifier.PAYMENT_INITIAL;

@Repository
public class TasksRepository {
    private Map<ImportTaskIdentifier, ImportTaskDescription> TASKS = new LinkedHashMap<>();

    public TasksRepository(
            BankTransactionInitialImportTask bankTransactionInitialImportTask,
            BankTransactionDeltaImportTask bankTransactionDeltaImportTask,
            BankTransactionReconciliationTask bankTransactionReconciliationTask,
            AccountInitialImportTask accountInitialImportTask,
            AccountDeltaImportTask accountDeltaImportTask,
            AccountReconciliationTask accountReconciliationTask,
            InvoiceInitialImportTask invoiceInitialImportTask,
            PaymentInitialImportTask paymentInitialImportTask
    ) {
        TASKS.put(BANK_TRANSACTION_INITIAL,
                new ImportTaskDescription(
                        "Bank Transaction Initial Import",
                        bankTransactionInitialImportTask,
                        BANK_TRANSACTION_INITIAL
                )
        );
        TASKS.put(BANK_TRANSACTION_DELTA,
                new ImportTaskDescription(
                        "Bank Transaction Delta Import",
                        bankTransactionDeltaImportTask,
                        BANK_TRANSACTION_DELTA
                )
        );
        TASKS.put(BANK_TRANSACTION_RECONCILIATION,
                new ImportTaskDescription(
                        "Bank Transaction Reconciliation",
                        bankTransactionReconciliationTask,
                        BANK_TRANSACTION_RECONCILIATION
                )
        );

        TASKS.put(ACCOUNT_INITIAL,
                new ImportTaskDescription(
                        "Account Initial Import",
                        accountInitialImportTask,
                        ACCOUNT_INITIAL
                )
        );
        TASKS.put(ACCOUNT_DELTA,
                new ImportTaskDescription(
                        "Account Delta Import",
                        accountDeltaImportTask,
                        ACCOUNT_DELTA
                )
        );
        TASKS.put(ACCOUNT_RECONCILIATION, new ImportTaskDescription(
                        "Account Reconciliation",
                        accountReconciliationTask,
                        ACCOUNT_RECONCILIATION
                )
        );

        TASKS.put(INVOICE_INITIAL,
                new ImportTaskDescription(
                        "Invoice Initial Import",
                        invoiceInitialImportTask,
                        INVOICE_INITIAL
                )
        );

        TASKS.put(PAYMENT_INITIAL,
                new ImportTaskDescription(
                        "Payment Initial Import",
                        paymentInitialImportTask,
                        PAYMENT_INITIAL
                )
        );
    }

    public Collection<ImportTaskDescription> getTaskDescriptions() {
        return TASKS.values();
    }

    public ImportTaskDescription get(ImportTaskIdentifier identifier) {
        return TASKS.get(identifier);
    }

    public boolean isAnyTaskRunning() {
        return TASKS.values()
                .stream()
                .anyMatch(task -> task.getImportTask().getIsRunning());
    }

    public boolean isTaskRunning(ImportTaskIdentifier identifier) {
        return TASKS.values()
                .stream()
                .filter(t -> t.getIdentifier() == identifier)
                .anyMatch(task -> task.getImportTask().getIsRunning());
    }

    public boolean isAnyTaskRunning(XeroDataType dataType) {
        return TASKS.values()
                .stream()
                .filter(t -> t.getImportTask().getDataType() == dataType)
                .anyMatch(task -> task.getImportTask().getIsRunning());
    }

    public enum ImportTaskIdentifier {
        BANK_TRANSACTION_INITIAL,
        BANK_TRANSACTION_DELTA,
        BANK_TRANSACTION_RECONCILIATION,

        ACCOUNT_INITIAL,
        ACCOUNT_DELTA,
        ACCOUNT_RECONCILIATION,

        INVOICE_INITIAL,

        PAYMENT_INITIAL;

        public static ImportTaskIdentifier fromValue(String value) {
            for (final ImportTaskIdentifier importTaskIdentifier : ImportTaskIdentifier.values()) {
                if (importTaskIdentifier.name().equals(value)) {
                    return importTaskIdentifier;
                }
            }
            return null;
        }
    }

    public static class ImportTaskDescription {
        private final String description;
        private final ImportTask importTask;
        private final ImportTaskIdentifier identifier;

        ImportTaskDescription(
                final String description,
                final ImportTask importTask,
                final ImportTaskIdentifier identifier
        ) {
            this.description = description;
            this.importTask = importTask;
            this.identifier = identifier;
        }

        public String getDescription() {
            return description;
        }

        public ImportTask getImportTask() {
            return importTask;
        }

        public ImportTaskIdentifier getIdentifier() {
            return identifier;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final ImportTaskDescription that = (ImportTaskDescription) o;
            return identifier == that.identifier;
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }
    }
}
