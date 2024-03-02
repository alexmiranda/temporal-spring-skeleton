package io.github.alexmiranda.samples.temporal_poc.controllers;

import io.github.alexmiranda.samples.temporal_poc.domain.OnboardingCaseRepository;
import io.github.alexmiranda.samples.temporal_poc.domain.TaskRepository;
import io.github.alexmiranda.samples.temporal_poc.messages.EnrichAndVerifyRequestIn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/ui/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository taskRepository;
    private final OnboardingCaseRepository caseRepository;
    private final TaskMapper mapper;

    @GetMapping(path = "/tasklist")
    public String list(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "tasklist";
    }

    @GetMapping(path = "/{tasktype}")
    public String details(Model model, @PathVariable String tasktype, @RequestParam long id) {
        var task = taskRepository.findById(id).get();
        var entity = caseRepository.findById(task.getId()).get();
        var request = mapper.toEnrichAndVerifyRequestIn(entity);
        model.addAttribute("task", task);
        model.addAttribute("request", request);
        model.addAttribute("countryList", countryList());
        return tasktype;
    }

    @PostMapping(path = "/{tasktype}")
    public String update(Model model, @PathVariable String tasktype, @RequestParam long id, @ModelAttribute("request") EnrichAndVerifyRequestIn in) {
        var task = taskRepository.findById(id).get();
        var entity = caseRepository.findById(task.getCaseId()).get();
        mapper.update(entity, in);
        entity = caseRepository.save(entity);
        var request = mapper.toEnrichAndVerifyRequestIn(entity);
        model.addAttribute("task", task);
        model.addAttribute("request", request);
        model.addAttribute("countryList", countryList());
        return tasktype;
    }

    private static Collection<String> countryList() {
        return List.of("The Netherlands", "Germany", "Spain");
    }
}