package pla.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import pla.dto.AuthInfo;
import pla.dto.MessageDto;
import pla.service.MemberService;
import pla.service.ProfileService;

@Controller
@RequiredArgsConstructor
public class MyDataController {
    private final ProfileService profileService;
    private final MemberService memberService;

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        model.addAttribute("my", authInfo);
        return "user/profile"; // 프로필 페이지 반환
    }

    @PostMapping("/profile/updateProfile")
    public String updateProfile(@ModelAttribute AuthInfo authInfo, HttpSession session, RedirectAttributes redirectAttributes) {
        profileService.updateProfile(authInfo);
        session.setAttribute("authInfo", authInfo);
        authInfo.setRole("USER");
        redirectAttributes.addFlashAttribute("message", "프로필 정보가 변경되었습니다.");
        return "redirect:/profile";
    }

    @GetMapping("/profile/checkLoginId")
    @ResponseBody
    public String checkLoginId(String loginId) {
        boolean isAvailable = memberService.checkLoginId(loginId);
        if (isAvailable) {
            return "사용 가능한 아이디입니다.";
        } else {
            return "이미 사용 중인 아이디입니다.";
        }
    }

}
