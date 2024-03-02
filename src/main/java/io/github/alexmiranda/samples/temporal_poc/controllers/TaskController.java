package io.github.alexmiranda.samples.temporal_poc.controllers;

import io.github.alexmiranda.samples.temporal_poc.domain.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository repository;

    @GetMapping(path = "/tasklist")
    public String list(Model model) {
        model.addAttribute("tasks", repository.findAll());
        return "tasklist";
    }
}
