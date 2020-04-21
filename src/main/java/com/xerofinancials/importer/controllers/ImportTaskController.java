package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.tasks.BankTransactionDeltaImportTask;
import com.xerofinancials.importer.tasks.BankTransactionInitialImportTask;
import com.xerofinancials.importer.tasks.BankTransactionReconciliationTask;
import com.xerofinancials.importer.tasks.TestImportTask;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/task")
public class ImportTaskController {
    private static final Logger logger = LoggerFactory.getLogger(ImportTaskController.class);
    private final TokenStorage tokenStorage;
    private final XeroApiWrapper apiWrapper;
    private final BankTransactionInitialImportTask initialImportTask;
    private final BankTransactionReconciliationTask reconciliationTask;
    private final BankTransactionDeltaImportTask deltaImportTask;

    public ImportTaskController(
            final TokenStorage tokenStorage,
            final XeroApiWrapper apiWrapper,
            final BankTransactionInitialImportTask initialImportTask,
            final BankTransactionReconciliationTask reconciliationTask,
            final BankTransactionDeltaImportTask deltaImportTask
    ) {
        this.tokenStorage = tokenStorage;
        this.apiWrapper = apiWrapper;
        this.initialImportTask = initialImportTask;
        this.reconciliationTask = reconciliationTask;
        this.deltaImportTask = deltaImportTask;
    }

    @GetMapping("all")
    public String all() {
        if (!tokenStorage.isAuthentificated()) {
            return "redirect:/xero/authorization";
        }
        return "tasks.html";
    }

    //todo: delete
    @GetMapping("test")
    public String testImportTask() {
        logger.info("Will be launched task...");
        final TestImportTask task = new TestImportTask(apiWrapper, tokenStorage);
        task.run();
        return "redirect:/task/all";
    }

    @GetMapping("initial")
    public String initialImportTask() {
        if (!isAnyTaskRunning()) {
            new Thread(initialImportTask::run).start();
        } else {
            logger.info("Import Task already running. Skipping.");
        }
        return "redirect:/task/all";
    }

    @GetMapping("delta")
    public String deltaImportTask() {
        if (!isAnyTaskRunning()) {
            new Thread(deltaImportTask::run).start();
        } else {
            logger.info("Import Task already running. Skipping.");
        }
        return "redirect:/task/all";
    }

    @GetMapping("reconciliation")
    public String reconciliationImportTask() {
        if (!isAnyTaskRunning()) {
            new Thread(reconciliationTask::run).start();
        } else {
            logger.info("Import Task already running. Skipping.");
        }
        return "redirect:/task/all";
    }

    private boolean isAnyTaskRunning() {
        if (initialImportTask.getIsRunning()) {
            return true;
        }
        if (reconciliationTask.getIsRunning()) {
            return true;
        }
        return false;
    }

}
