package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.repository.TasksRepository;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
@RequestMapping("/task")
public class ImportTaskController {
    private static final Logger logger = LoggerFactory.getLogger(ImportTaskController.class);
    private final TokenStorage tokenStorage;
    private final TasksRepository tasksRepository;

    public ImportTaskController(
            final TokenStorage tokenStorage,
            final TasksRepository tasksRepository
    ) {
        this.tokenStorage = tokenStorage;
        this.tasksRepository = tasksRepository;
    }

    @GetMapping("all")
    public String all(Model model) {
        if (!tokenStorage.isAuthentificated()) {
            return "redirect:/xero/authorization";
        }
        final Collection<TasksRepository.ImportTaskDescription> taskDescriptions = tasksRepository.getTaskDescriptions();
        model.addAttribute("taskIdentifiers", taskDescriptions);
        return "tasks.html";
    }

    @GetMapping("run/{taskIdentifier}")
    public String runTask(@PathVariable("taskIdentifier") String taskIdentifier) {
        final TasksRepository.ImportTaskIdentifier importTaskIdentifier = TasksRepository.ImportTaskIdentifier.fromValue(taskIdentifier);
        if (importTaskIdentifier == null) {
            logger.info("Invalid task identifier '{}'. Skipping.", taskIdentifier);
            return "redirect:/task/all";
        }
        if (tasksRepository.isAnyTaskRunning()) {
            logger.info("Import Task already running. Skipping.");
            return "redirect:/task/all";
        }
        final TasksRepository.ImportTaskDescription importTaskDescription = tasksRepository.get(importTaskIdentifier);
        new Thread(() -> importTaskDescription.getImportTask().run()).start();
        return "redirect:/task/all";
    }

}
