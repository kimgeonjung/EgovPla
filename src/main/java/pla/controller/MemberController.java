package pla.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pla.dto.AuthInfo;
import pla.dto.LoginDto;
import pla.dto.MemberRequest;
import pla.entity.Member;
import pla.repository.MemberRepository;
import pla.service.MemberService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @PostMapping("/register")
    public String register(MemberRequest request, Model model, RedirectAttributes redirectAttributes) {
        String result = memberService.registerMember(request);
        if ("이미 존재하는 이메일입니다.".equals(result)) {
            model.addAttribute("error", "이미 존재하는 이메일입니다.");
            return "user/register";
        }
        redirectAttributes.addFlashAttribute("message", "회원가입이 성공적으로 완료되었습니다!");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login"; // 로그인 폼 페이지
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto dto, Model model, HttpSession session) {
        boolean isLogin = memberService.loginMember(dto);
        if (!isLogin) {
            model.addAttribute("error", "ID 또는 비밀번호가 틀립니다.");
            return "user/login";
        }
        Member member = memberRepository.findByLoginId(dto.getLoginId()).orElse(null); // 이메일로 회원 조회
        if (member != null && member.isAdmin()) { // 관리자인지 확인
            AuthInfo authInfo = new AuthInfo(member.getId(), member.getLoginId(),member.getEmail(),member.getName(),
                    member.getTel(),member.getZipcode(),member.getAddress(),member.getDetailAddress(), member.getRole()); // 관리자 정보를 AuthInfo에 입력
            session.setAttribute("authInfo", authInfo); // 세션에 AuthInfo 저장

            return "redirect:/admin"; // 관리자는 관리자 페이지로 리디렉션
        }
        AuthInfo authInfo = new AuthInfo(member.getId(), member.getLoginId(),member.getEmail(),member.getName(),
                member.getTel(),member.getZipcode(),member.getAddress(),member.getDetailAddress(),member.getRole()); // 회원 정보를 AuthInfo에 입력
        session.setAttribute("authInfo", authInfo); // 세션에 AuthInfo 저장



        return "redirect:/"; // 일반 사용자는 홈 페이지로 리디렉션
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // 세션 무효화 (로그아웃 처리)
        return "redirect:/";  // 로그인 페이지로 리디렉션
    }

    @GetMapping("/findId")
    public String findIdPage() {
        return "user/findId";  // idfor.html로 이동
    }

    @GetMapping("/findPw")
    public String findPwPage() {
        return "user/findPw";  // passwordfor.html로 이동
    }

}
