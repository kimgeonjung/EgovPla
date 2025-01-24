package pla.mail;

import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pla.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailRestController {

    private final MailService mailService;
    private final MemberService memberService;
    
    @PostMapping("/uniqueCheck")
    public ResponseEntity<Boolean> uniqueCheck(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        boolean result = memberService.checkEmail(email);
        return ResponseEntity.ok(result);
    }

    // 인증번호를 담은 메일을 보냄
    @PostMapping("/emailCheck")
    public ResponseEntity<String> emailCheck(@RequestBody Mail mail) throws MessagingException {
        try{
            String response = mailService.sendSimpleMessage(mail.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (MessagingException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이메일 발송 중 오류가 발생했습니다.");
        }
    }

    // 인증번호 유효여부 확인
    @PostMapping("/codeCheck")
    public ResponseEntity<String> codeCheck(@RequestBody Mail mail) {
        try{
            boolean isValid = mailService.verifyCode(mail.getEmail(), mail.getCode());

            if (isValid) {
                return ResponseEntity.status(HttpStatus.OK).body("인증 성공!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("인증 실패. 잘못된 인증번호입니다.\n"+"이메일:"+ mail.getEmail()+"\n코드:"+ mail.getCode());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
}
