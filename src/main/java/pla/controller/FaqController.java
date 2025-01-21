package pla.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import pla.entity.Faq;
import pla.service.FaqService;

/**
 * FAQ 관련 요청을 처리하는 컨트롤러.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/faq")
public class FaqController {

    private final FaqService faqService;

    // FAQ 목록 페이지를 반환.
    @GetMapping("")
    public String faq(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<Faq> faqs = faqService.getActiveFAQs(pageable);
        model.addAttribute("faqs", faqs);
        return "faq/faqList";
    }

    // FAQ 상세 페이지를 반환.
    @GetMapping("/faqDetail")
    public String faqDetail(@RequestParam("id") Long id, Model model) {
        Faq faq = faqService.faqDetail(id);
        model.addAttribute("faq", faq);
        model.addAttribute("now", LocalDateTime.now());
        return "faq/faqDetail";
    }
}