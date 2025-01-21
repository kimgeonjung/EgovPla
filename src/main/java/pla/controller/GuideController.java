package pla.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuideController {

    @GetMapping("/platform")
    public String platform(Model model) {
        return "guide/platform";
    }

    @GetMapping("/guide")
    public String guide(Model model) {
        return "guide/guide";
    }

    @GetMapping("/cases")
    public String cases(Model model) {
        return "guide/cases";
    }
}
