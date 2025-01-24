package pla.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pla.dto.AuthInfo;
import pla.entity.Faq;
import pla.entity.Notice;
import pla.service.FaqService;
import pla.service.NoticeService;

/**
 * 메인 화면 컨트롤러.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final NoticeService noticeService;
    private final FaqService faqService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        // 로그인 상태 확인
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        model.addAttribute("isLoggedIn", authInfo != null);

        // 공지사항, FAQ 데이터 조회
        List<Notice> notices = noticeService.getAllNotices();
        List<Faq> faqs = faqService.getUserFAQs();

        // 각각 화면에 최대 5개 표시
        model.addAttribute("notices", notices.stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("faqs", faqs.stream().limit(5).collect(Collectors.toList()));

        return "main/home";
    }
}
