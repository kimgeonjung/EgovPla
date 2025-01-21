package pla.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import pla.dto.AuthInfo;
import pla.entity.Notice;
import pla.service.NoticeService;
import pla.utils.PagingUtils;

/**
 * 공지사항 관련 요청을 처리하는 컨트롤러.
 */
@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 페이지를 반환합니다.
    @GetMapping("/noticeList")
    public String getNoticeList(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        List<Notice> notices = noticeService.getAllNotices();
        Page<Notice> noticePage = PagingUtils.createPage(notices, pageable);

        model.addAttribute("notices", noticePage); // 공지사항 리스트를 페이징 처리 후 모델에 추가
        model.addAttribute("totalNotices", notices.size()); // 총 공지사항 개수
        model.addAttribute("now", getCurrentTime()); // 현재 시간
        return "notice/list";
    }

    // 공지사항 상세 페이지를 반환합니다.
    @GetMapping("/noticeDetail")
    public String getNoticeDetail(@RequestParam Long id, Model model, HttpSession session) throws Exception {
        Notice notice = noticeService.getNoticeDetail(id, true); // 조회수 증가
        AuthInfo authInfo = getAuthInfo(session);

        if (authInfo != null) {
            model.addAttribute("authInfo", authInfo);
        }
        model.addAttribute("notice", notice); // 공지사항 정보
        model.addAttribute("now", getCurrentTime()); // 현재 시간
        return "notice/detail";
    }

    // 세션에서 AuthInfo 객체를 가져옵니다.
    private AuthInfo getAuthInfo(HttpSession session) {
        return (AuthInfo) session.getAttribute("authInfo");
    }

    // 현재 시간을 반환합니다.
    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
