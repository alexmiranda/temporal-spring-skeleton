package io.github.alexmiranda.samples.temporal_poc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {
    @GetMapping
    public String home() {
        return "redirect:/ui/tasks/tasklist";
    }
}
