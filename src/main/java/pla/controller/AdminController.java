package pla.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequiredArgsConstructor
public class AdminController {
    private final NoticeService noticeService;
    private final QnaListRepository qnAListRepository;
    private final MemberRepository memberRepository;
    private final QnaRepository qnARepository;
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

    @GetMapping("/admin")
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

    @GetMapping("/admin/admin_notice")
    public String noticeList(Model model,
                             @PageableDefault(page = 0, size = 10) Pageable pageable) {
        List<Notice> notices = noticeService.getAllNotices();
        Page<Notice> page = PagingUtils.createPage(notices, pageable);
        model.addAttribute("notices", notices.size());
        model.addAttribute("now", getCurrentTime());
        model.addAttribute("list", page);
        return "admin/admin_notice";
    }

    @GetMapping("/admin/admin_noticeWrite")
    public String noticeWrite() {
        return "admin/admin_noticeWrite";
    }

    @PostMapping("/admin/createNotice")
    public String createNotice(@RequestParam String title,
                               @RequestParam String content,
                               @RequestParam("files") MultipartFile[] files) throws IOException {
        noticeService.createNotice(title, content,files); // 서비스 호출하여 공지사항 생성
        return "redirect:/admin/admin_notice";
    }

    @GetMapping("/admin/noticeDetail")
    public String adminNoticeDetail(@RequestParam Long id, Model model, HttpSession session) throws Exception {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        if (authInfo != null) {
            model.addAttribute("authInfo", authInfo); // 템플릿에서 접근 가능하도록 모델에 추가
        }
        model.addAttribute("now", getCurrentTime());
        model.addAttribute("notice", noticeService.getNoticeDetail(id,false));
        return "admin/admin_noticeDetail";  // 관리자 페이지
    }

    @PostMapping("/admin/updateNotice")
    public String updateNotice(Notice notice) {
        noticeService.updateNotice(notice);
        return "redirect:/admin/admin_notice";
    }

    @PostMapping("/admin/deleteNotice")
    public String deleteNotice(@RequestParam Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/admin/admin_notice";
    }

    // -------------------------------------------
    // Q&A 관리
    // -------------------------------------------

    @GetMapping("/admin/admin_qna")
    public String qnaList(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable, HttpSession session) {
    	AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
    	model.addAttribute("now", getCurrentTime());
        model.addAttribute("qna", qnARepository.findAll());
        model.addAttribute("qnaList", qnaService.getInquiries(authInfo, pageable));
        model.addAttribute("member", memberRepository.findAll());
        return "admin/admin_qna";
    }

    @GetMapping("/admin/admin_qnaDetail")
    public String qnaDetail(@RequestParam("id") Long id, Model model) {
        QnaList qnAList = qnaService.getInquiryDetail(id);
        Member member = memberRepository.findById(qnAList.getUid()).get();
        model.addAttribute("member", member);
        model.addAttribute("qna", qnAList);
        model.addAttribute("lists", qnaService.getInquiryReplies(id));
        model.addAttribute("now", getCurrentTime());
        return "admin/admin_qnaDetail";
    }

    @PostMapping("/admin/inquiryInsert")
    public String qnaInsert(@ModelAttribute QnaList list,
                            @RequestParam String content,
                            HttpSession session) {
        qnaService.createInquiry(list, content, session);
        return "redirect:/admin/admin_qnaDetail";
    }

    @PostMapping("/admin/answerInsert")
    public String answerInsert(@RequestParam String content,
                               @RequestParam String role,
                               @RequestParam Long id) {
        qnaService.addAnswer(content, role, id);
        return "redirect:/admin/admin_qnaDetail?id=" + id;
    }

    @PostMapping("/admin/end/{id}")
    public String end(@PathVariable Long id, Model model) {
        QnaList qnAList = qnAListRepository.findById(id).get();
        qnAList.setEndYn('Y');
        qnAListRepository.save(qnAList);
        MessageDto message = new MessageDto("문의를 종료하였습니다.", "/admin/admin_qna", RequestMethod.GET, null);
        return showMessageAndRedirect(message, model);
    }

    // -------------------------------------------
    // FAQ 관리
    // -------------------------------------------

    @GetMapping("/admin/admin_faq")
    public String faq(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        model.addAttribute("faqs", faqService.getAllFAQs(pageable));
        return "admin/admin_faq";
    }

    @GetMapping("/admin/admin_faqDetail")
    public String faqDetail(@RequestParam Long id, Model model) {
        model.addAttribute("now", getCurrentTime());
        model.addAttribute("faq", faqService.faqDetail(id));
        return "admin/admin_faqDetail";
    }

    @GetMapping("/admin/admin_faqWrite")
    public String faqWrite() {
        return "admin/admin_faqWrite";
    }

    @PostMapping("/admin/createFAQ")
    public String createFAQ(@RequestParam String title,
                            @RequestParam String question,
                            @RequestParam String answer) {
        faqService.faqCreate(title, question, answer);
        return "redirect:/admin/admin_faq";
    }

    @PostMapping("/admin/admin_faqUpdate")
    public String updateFAQ(@RequestParam Long id,
                            @RequestParam String title,
                            @RequestParam String question,
                            @RequestParam String answer) {
        faqService.faqUpdate(id, title, question, answer);
        return "redirect:/admin/admin_faq";
    }

    @PostMapping("/admin/admin_faqDelete")
    public String faqDelete(@RequestParam Long id, Model model) {
        faqService.faqDelete(id);
        MessageDto message = new MessageDto("삭제하였습니다", "/admin/admin_faq", RequestMethod.GET, null);
        return showMessageAndRedirect(message, model);
    }

    // -------------------------------------------
    // 입지분석 신청 관리
    // -------------------------------------------

    @GetMapping("/admin/admin_apply")
    public String apply(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable,HttpSession session) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        Page<Apply> applies = applyService.getApplies(authInfo, pageable, "none"); // 관리자는 request 상관없음
        model.addAttribute("count", applies.stream().count());
        model.addAttribute("applies", applies);
        model.addAttribute("now", getCurrentTime());
        return "admin/admin_apply";
    }

    @GetMapping("/admin/admin_apply/detail")
    public String applyDetail(@RequestParam Long id, Model model) {
        model.addAttribute("apply", applyService.selectApplyDetail(id));
        model.addAttribute("now", getCurrentTime());
        return "admin/admin_applyDetail";
    }

    @PostMapping("/admin/admin_apply_result")
    public String applyResult(Apply apply){
        Apply updateApply = applyRepository.selectApplyDetail(apply.getId());
        updateApply.setCompletedYn('Y');
        updateApply.setLink(apply.getLink());
        updateApply.setLocation(apply.getLocation());
        updateApply.setType(apply.getType());
        applyService.updateApply(updateApply);
        return "redirect:/admin/admin_apply";
    }
    
    // -------------------------------------------
    // 메타데이터 관리
    // -------------------------------------------
    
    @GetMapping("/admin/admin_dataList")
    public String getDataList(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        List<DataBoard> dataBoard = dataBoardService.getAllDataList();
        Page<DataBoard> dataBoardPage = PagingUtils.createPage(dataBoard, pageable);

        model.addAttribute("dataList", dataBoardPage);
        model.addAttribute("totalDataList", dataBoard.size());
        model.addAttribute("now", getCurrentTime());
        return "admin/admin_dataList";
    }
    
    @GetMapping("/admin/admin_dataListDetail")
    public String getDataListDetail(@RequestParam Long id,
                                    @RequestParam(defaultValue = "10") int limit,
                                    @RequestParam(defaultValue = "0") int offset,Model model) throws Exception {

        DataBoard dataBoard = dataBoardService.getDataListDetail(id, true, limit, offset);
        model.addAttribute("offset", offset);
        model.addAttribute("limit", limit);
        model.addAttribute("dataList", dataBoard);
        model.addAttribute("now", getCurrentTime()); // 현재 시간
        return "admin/admin_dataListDetail";
    }
    
    @GetMapping("/admin/admin_dataListWrite")
    public String getDataListWrite(){
        return "admin/admin_dataListWrite";
    }

    @PostMapping("/admin/admin_dataListWrite")
    public String createData(@ModelAttribute DataBoardDto dataBoardDto) throws IOException {

        // 데이터 저장 처리
        dataBoardService.createDataBoard(dataBoardDto);

        return "redirect:/admin/admin_dataList"; // 데이터 목록 페이지로 리다이렉트
    }

    @PostMapping("/admin/updateDataList")
    public String updateDataBoard(DataBoardDto dto) throws IOException {
        dataBoardService.updateDataBoard(dto);
        return "redirect:/admin/admin_dataList";  // 수정 후 목록으로 리디렉션
    }

    @PostMapping("/admin/deleteDataList")
    public String deleteDataList(@RequestParam Long id)throws IOException{
        dataBoardService.deleteDataBoard(id);
        return "redirect:/admin/admin_dataList";
    }
    
}
