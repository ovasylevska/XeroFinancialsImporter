package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.beans.ScheduleTime;
import com.xerofinancials.importer.repository.ScheduleRepository;
import com.xerofinancials.importer.repository.TasksRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleRepository scheduleRepository;
    private final TasksRepository tasksRepository;

    public ScheduleController(
            final ScheduleRepository scheduleRepository,
            final TasksRepository tasksRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.tasksRepository = tasksRepository;
    }

    @GetMapping("/all")
    public String get(Model model) {
        final List<ScheduleTime> scheduleTimes = scheduleRepository.all();
        final Collection<TasksRepository.ImportTaskDescription> taskDescriptions = tasksRepository.getTaskDescriptions();
        setTaskDescriptions(scheduleTimes, taskDescriptions);
        model.addAttribute("scheduleTimes", scheduleTimes);
        model.addAttribute("newScheduleTime", new ScheduleTime());
        model.addAttribute("taskIdentifiers", taskDescriptions);
        return "schedule.html";
    }

    @PostMapping
    public String save(ScheduleTime scheduleTime) {
        if (TasksRepository.ImportTaskIdentifier.fromValue(scheduleTime.getTaskIdentifier()) == null) {
            return "redirect:/schedule/all";
        }
        if (!scheduleRepository.all().contains(scheduleTime)) {
            scheduleRepository.save(scheduleTime);
        }
        return "redirect:/schedule/all";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        scheduleRepository.delete(id);
        return "redirect:/schedule/all";
    }

    private void setTaskDescriptions(
            final List<ScheduleTime> scheduleTimes,
            final Collection<TasksRepository.ImportTaskDescription> taskDescriptions
    ) {
        scheduleTimes.forEach(time -> {
            Optional<TasksRepository.ImportTaskDescription> taskDescription = taskDescriptions
                    .stream()
                    .filter(d -> d.getIdentifier().name().equals(time.getTaskIdentifier()))
                    .findFirst();
            if (taskDescription.isPresent()) {
                time.setTaskDescription(taskDescription.get().getDescription());
            }
        });
    }
}
