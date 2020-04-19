package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.tasks.TestImportTask;
import com.xerofinancials.importer.xeroapi.XeroApiWrapper;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/task")
public class ImportTaskController {
    private static final Logger logger = LoggerFactory.getLogger(ImportTaskController.class);
    private final TokenStorage tokenStorage;
    private final XeroApiWrapper apiWrapper;

    public ImportTaskController(
            final TokenStorage tokenStorage,
            final XeroApiWrapper apiWrapper
    ) {
        this.tokenStorage = tokenStorage;
        this.apiWrapper = apiWrapper;
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
    public String testImportTask() throws IOException {
        logger.info("Will be launched task...");
        final TestImportTask task = new TestImportTask(apiWrapper, tokenStorage);
        task.execute();
        return "redirect:/task/all";
    }

    @GetMapping("initial")
    public String initialImportTask() {
        return "redirect:/task/all";
    }

    @GetMapping("delta")
    public String deltaImportTask() {
        return "redirect:/task/all";
    }

    @GetMapping("reconciliation")
    public String reconciliationImportTask() {
        return "redirect:/task/all";
    }

}
