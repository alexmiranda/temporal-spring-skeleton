package io.github.alexmiranda.samples.temporal_poc.controllers;

import io.github.alexmiranda.samples.temporal_poc.domain.OnboardingCaseRepository;
import io.github.alexmiranda.samples.temporal_poc.domain.TaskRepository;
import io.github.alexmiranda.samples.temporal_poc.messages.OnboardingRequestIn;
import io.github.alexmiranda.samples.temporal_poc.messages.TaskCompleted;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/ui/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository taskRepository;
    private final OnboardingCaseRepository caseRepository;
    private final TaskMapper mapper;
    private final ApplicationEventPublisher publisher;

    @GetMapping(path = "/tasklist")
    public String list(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "tasklist";
    }

    @GetMapping(path = "/{tasktype}")
    public String details(Model model, @PathVariable String tasktype, @RequestParam long id) {
        var task = taskRepository.findById(id).get();
        var entity = caseRepository.findById(task.getCaseId()).get();
        var request = mapper.toOnboardingRequestIn(entity);
        model.addAttribute("task", task);
        model.addAttribute("request", request);
        model.addAttribute("countryList", countryList());
        model.addAttribute("cardTypes", cardTypes());
        return tasktype;
    }

    @PostMapping(path = "/{tasktype}", params = "save")
    @Transactional
    public String save(Model model, @PathVariable String tasktype, @RequestParam long id, @ModelAttribute("request") OnboardingRequestIn in) {
        var task = taskRepository.findById(id).get();
        var entity = caseRepository.findById(task.getCaseId()).get();
        mapper.update(entity, in);
        entity = caseRepository.save(entity);
        var request = mapper.toOnboardingRequestIn(entity);
        model.addAttribute("task", task);
        model.addAttribute("request", request);
        model.addAttribute("countryList", countryList());
        model.addAttribute("cardTypes", cardTypes());
        return tasktype;
    }

    @PostMapping(path = "/{tasktype}", params = "action")
    @Transactional
    public String submit(Model model, @PathVariable String tasktype, @RequestParam long id, @ModelAttribute("request") OnboardingRequestIn in) {
        var task = taskRepository.findById(id).get();
        var entity = caseRepository.findById(task.getCaseId()).get();
        mapper.update(entity, in);
        caseRepository.save(entity);
        task.markCompleted();
        taskRepository.save(task);
        boolean approved = Objects.equals(in.getAction(), "approve");
        publisher.publishEvent(new TaskCompleted(task.getId(), entity.getWorkflowId().toString(), task.getTaskType(), approved, entity.isScreeningRequired()));
        return "redirect:/ui/tasks/tasklist";
    }

    private static Collection<String> countryList() {
        return List.of("The Netherlands", "Germany", "Spain");
    }

    private static Collection<String> cardTypes() {
        return List.of("Master", "Visa");
    }
}
