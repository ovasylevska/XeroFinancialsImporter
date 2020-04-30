package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.repository.TasksRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/status", produces = "application/json")
public class ImportTaskStatusController {
    private final TasksRepository tasksRepository;

    public ImportTaskStatusController(final TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    @GetMapping(value = "/isTaskRunning")
    public String getCurrentServerTime() {
        return Boolean.toString(tasksRepository.isAnyTaskRunning());
    }

}
