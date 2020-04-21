package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.tasks.BankTransactionDeltaImportTask;
import com.xerofinancials.importer.tasks.BankTransactionInitialImportTask;
import com.xerofinancials.importer.tasks.BankTransactionReconciliationTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/status", produces = "application/json")
public class ImportTaskStatusController {
    private final BankTransactionInitialImportTask initialImportTask;
    private final BankTransactionReconciliationTask reconciliationTask;
    private final BankTransactionDeltaImportTask deltaImportTask;

    public ImportTaskStatusController(
            final BankTransactionInitialImportTask initialImportTask,
            final BankTransactionReconciliationTask reconciliationTask,
            final BankTransactionDeltaImportTask deltaImportTask
    ) {
        this.initialImportTask = initialImportTask;
        this.reconciliationTask = reconciliationTask;
        this.deltaImportTask = deltaImportTask;
    }

    @GetMapping(value = "/isTaskRunning")
    public String getCurrentServerTime() {
        return Boolean.toString(isAnyTaskRunning());
    }

    //todo: move to some kind of task repository
    private boolean isAnyTaskRunning() {
        if (initialImportTask.getIsRunning()) {
            return true;
        }
        if (reconciliationTask.getIsRunning()) {
            return true;
        }
        if (deltaImportTask.getIsRunning()) {
            return true;
        }
        return false;
    }
}
