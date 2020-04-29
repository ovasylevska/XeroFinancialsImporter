package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.beans.ScheduleTime;
import com.xerofinancials.importer.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    //todo: add schedule for diff tasks (account and bank transactions)
    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);
    private final ScheduleRepository scheduleRepository;

    public ScheduleController(final ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping("/all")
    public String get(Model model) {
        final List<ScheduleTime> scheduleTimes = scheduleRepository.all();
        model.addAttribute("scheduleTimes", scheduleTimes);
        model.addAttribute("newScheduleTime", new ScheduleTime());
        return "schedule.html";
    }

    @PostMapping
    public String save(ScheduleTime scheduleTime) {
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
}
