package pla.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pla.dto.ChangePwRequest;
import pla.dto.FindPwRequest;
import pla.service.MemberService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/confirmId")
    public ResponseEntity<Boolean> confirmId(@RequestBody String loginId) {
        boolean result = memberService.checkLoginId(loginId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/findLoginId")
    public ResponseEntity<String> findLoginId(@RequestParam String name, @RequestParam String email) {
        String loginId = memberService.findLoginIdByNameAndEmail(name, email);
        if (loginId != null) {
            return ResponseEntity.ok(loginId);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 정보를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/findPassword")
    public ResponseEntity<String> findPassword(@RequestBody FindPwRequest request) {
        try {
            String status = memberService.findPw(request);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/changePw")
    public ResponseEntity<String> changePw(@RequestBody ChangePwRequest request, HttpSession session) {
        try{
            String response = memberService.changePw(request);
            session.invalidate();
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
