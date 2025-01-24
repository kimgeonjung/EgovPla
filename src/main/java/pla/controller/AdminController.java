package pla.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pla.dto.AuthInfo;
import pla.dto.DataBoardDto;
import pla.dto.MessageDto;
import pla.entity.Apply;
import pla.entity.DataBoard;
import pla.entity.Member;
import pla.entity.Notice;
import pla.entity.QnaList;
import pla.repository.ApplyRepository;
import pla.repository.MemberRepository;
import pla.repository.QnaListRepository;
import pla.repository.QnaRepository;
import pla.service.ApplyService;
import pla.service.DataBoardService;
import pla.service.FaqService;
import pla.service.NoticeService;
import pla.service.QnaService;
import pla.utils.PagingUtils;

/**
 * 관리자 페이지 요청을 처리하는 컨트롤러.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final NoticeService noticeService;
    private final QnaListRepository qnAListRepository;
    private final MemberRepository memberRepository;
    private final QnaService qnaService;
    private final FaqService faqService;
    private final ApplyRepository applyRepository;
    private final ApplyService applyService;
    private final DataBoardService dataBoardService;

    // 메시지 출력 및 리다이렉트를 처리하는 공통 메서드.
    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params);
        return "common/messageRedirect";
    }
    // 현재 시간을 반환하는 공통 메서드.
    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    // -------------------------------------------
    // 관리자 페이지 메인
    // -------------------------------------------

    @GetMapping("")
    public String admin(Model model) {
        List<QnaList> qnAList = qnaService.needAnswer();
        List<Apply> applyList = applyService.needAnswer();
        model.addAttribute("qnALists", qnAList);
        model.addAttribute("now", getCurrentTime());
        model.addAttribute("member", memberRepository.findAll());
        model.addAttribute("applyLists", applyList);
        return "admin/adminPage";
    }

    // -------------------------------------------
    // 공지사항 관리
    // -------------------------------------------

    @GetMapping("/notice")
    public String noticeList(Model model,
                             @PageableDefault(page = 0, size = 10) Pageable pageable) {
        List<Notice> notices = noticeService.getAllNotices();
        Page<Notice> page = PagingUtils.createPage(notices, pageable);
        model.addAttribute("notices", notices.size());
        model.addAttribute("now", getCurrentTime());
        model.addAttribute("list", page);
        return "admin/admin_notice";
    }

    @GetMapping("/notice/write")
    public String noticeWrite() {
        return "admin/admin_noticeWrite";
    }

    @PostMapping("/notice/create")
    public String createNotice(@RequestParam String title,
                               @RequestParam String content,
                               @RequestParam("files") MultipartFile[] files) throws IOException {
        noticeService.createNotice(title, content,files); // 서비스 호출하여 공지사항 생성
        return "redirect:/admin/notice";
    }

    @GetMapping("/notice/detail")
    public String adminNoticeDetail(@RequestParam Long id, Model model, HttpSession session) throws Exception {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        if (authInfo != null) {
            model.addAttribute("authInfo", authInfo); // 템플릿에서 접근 가능하도록 모델에 추가
        }
        model.addAttribute("now", getCurrentTime());
        model.addAttribute("notice", noticeService.getNoticeDetail(id,false));
        return "admin/admin_noticeDetail";  // 관리자 페이지
    }

    @PostMapping("/notice/update")
    public String updateNotice(Notice notice) {
        noticeService.updateNotice(notice);
        return "redirect:/admin/notice";
    }

    @PostMapping("/notice/delete")
    public String deleteNotice(@RequestParam Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/admin/notice";
    }

    // -------------------------------------------
    // Q&A 관리
    // -------------------------------------------

    @GetMapping("/qna")
    public String qnaList(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable, HttpSession session) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;  // ISO 8601 표준 포맷 적용
        model.addAttribute("now", now.format(formatter));
        model.addAttribute("qna", qnAListRepository.findAll());
        model.addAttribute("qnaList", qnaService.getInquiries(authInfo, pageable));
        model.addAttribute("member", memberRepository.findAll());
        return "admin/admin_qna";
    }

    @GetMapping("/qna/detail")
    public String qnaDetail(@RequestParam("id") Long id, Model model) {
        QnaList qnAList = qnaService.getInquiryDetail(id);
        Member member = memberRepository.findById(qnAList.getUid()).get();
        model.addAttribute("member", member);
        model.addAttribute("qna", qnAList);
        model.addAttribute("lists", qnaService.getInquiryReplies(id));
        model.addAttribute("now", getCurrentTime());
        return "admin/admin_qnaDetail";
    }

    @PostMapping("/qna/answer")
    public String answerInsert(@RequestParam String content,
                               @RequestParam String role,
                               @RequestParam Long id) {
        qnaService.addAnswer(content, role, id);
        return "redirect:/admin/qna/detail?id=" + id;
    }

    // -------------------------------------------
    // FAQ 관리
    // -------------------------------------------

    @GetMapping("/faq")
    public String faq(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        model.addAttribute("faqs", faqService.getAllFAQs(pageable));
        return "admin/admin_faq";
    }

    @GetMapping("/faq/detail")
    public String faqDetail(@RequestParam Long id, Model model) {
        model.addAttribute("now", getCurrentTime());
        model.addAttribute("faq", faqService.faqDetail(id));
        return "admin/admin_faqDetail";
    }

    @GetMapping("/faq/write")
    public String faqWrite() {
        return "admin/admin_faqWrite";
    }

    @PostMapping("/faq/create")
    public String createFAQ(@RequestParam String title,
                            @RequestParam String question,
                            @RequestParam String answer) {
        faqService.faqCreate(title, question, answer);
        return "redirect:/admin/faq";
    }

    @PostMapping("/faq/update")
    public String updateFAQ(@RequestParam Long id,
                            @RequestParam String title,
                            @RequestParam String question,
                            @RequestParam String answer) {
        faqService.faqUpdate(id, title, question, answer);
        return "redirect:/admin/faq";
    }

    @PostMapping("/faq/delete")
    public String faqDelete(@RequestParam Long id, Model model) {
        faqService.faqDelete(id);
        MessageDto message = new MessageDto("삭제하였습니다", "/admin/faq", RequestMethod.GET, null);
        return showMessageAndRedirect(message, model);
    }

    // -------------------------------------------
    // 입지분석 신청 관리
    // -------------------------------------------

    @GetMapping("/apply")
    public String apply(Model model,HttpSession session) {
    	AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        List<Apply> applies = applyRepository.findAll();
        model.addAttribute("count", applies.size());
        model.addAttribute("applies", applies);
        model.addAttribute("now", getCurrentTime());
        return "admin/admin_apply";
    }

    @GetMapping("/apply/detail")
    public String applyDetail(@RequestParam Long id, Model model) {
        model.addAttribute("apply", applyService.selectApplyDetail(id));
        model.addAttribute("now", getCurrentTime());
        return "admin/admin_applyDetail";
    }

    @PostMapping("/apply/result")
    public String applyResult(Apply apply){
        Apply updateApply = applyRepository.selectApplyDetail(apply.getId());
        updateApply.setCompletedYn('Y');
        updateApply.setLink(apply.getLink());
        updateApply.setLocation(apply.getLocation());
        updateApply.setType(apply.getType());
        applyService.updateApply(updateApply);
        return "redirect:/admin/apply";
    }
    
    // -------------------------------------------
    // 메타데이터 관리
    // -------------------------------------------
    
    @GetMapping("/dataList")
    public String getDataList(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        List<DataBoard> dataBoard = dataBoardService.getAllDataList();
        Page<DataBoard> dataBoardPage = PagingUtils.createPage(dataBoard, pageable);

        model.addAttribute("dataList", dataBoardPage);
        model.addAttribute("totalDataList", dataBoard.size());
        model.addAttribute("now", getCurrentTime());
        return "admin/admin_dataList";
    }
    
    @GetMapping("/dataList/detail")
    public String getDataListDetail(@RequestParam Long id,
                                    @RequestParam(defaultValue = "10") int limit,
                                    @RequestParam(defaultValue = "0") int offset,Model model) throws Exception {

        DataBoard dataBoard = dataBoardService.getDataListDetail(id, true, limit, offset);
        model.addAttribute("dataList", dataBoard);
        model.addAttribute("now", getCurrentTime()); // 현재 시간
        return "admin/admin_dataListDetail";
    }
    
    @GetMapping("/dataList/write")
    public String getDataListWrite(){
        return "admin/admin_dataListWrite";
    }

    @PostMapping("/dataList/create")
    public String createData(@ModelAttribute DataBoardDto dataBoardDto) throws IOException {

        // 데이터 저장 처리
        dataBoardService.createDataBoard(dataBoardDto);

        return "redirect:/admin/dataList"; // 데이터 목록 페이지로 리다이렉트
    }

    @PostMapping("/dataList/update")
    public String updateDataBoard(DataBoardDto dto) throws IOException {
        dataBoardService.updateDataBoard(dto);
        return "redirect:/admin/dataList";  // 수정 후 목록으로 리디렉션
    }

    @PostMapping("/dataList/delete")
    public String deleteDataList(@RequestParam Long id)throws IOException{
        dataBoardService.deleteDataBoard(id);
        return "redirect:/admin/dataList";
    }
    
}
