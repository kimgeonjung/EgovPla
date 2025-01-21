package pla.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private static final String senderEmail = "";

    private final Map<String, VerificationCode> verificationCode = new HashMap<>();

    public String createNumber(){
        Random rand = new Random();
        StringBuilder key = new StringBuilder();

        for(int i=0; i<8; i++){
            int index = rand.nextInt(3);

            switch(index){
                case 0: key.append((char)(rand.nextInt(26)+97));
                break;
                case 1: key.append((char)(rand.nextInt(26)+65));
                break;
                case 2: key.append(rand.nextInt(10));
                break;
            }
        }
        return key.toString();
    }

    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String number = createNumber();

        MimeMessage message = createMail(sendEmail, number);
        log.info("이메일: {}", sendEmail);
        log.info("인증번호: {}", number);
        try{
        	mailSender.send(message);
        }catch (MailException e){
        	log.error("메일 발송 중 오류: {}", e.getMessage(), e);
            throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다.");
        }

        verificationCode.put(sendEmail, new VerificationCode(number, System.currentTimeMillis()));
        log.info("코드 해시맵 {}", verificationCode.get(sendEmail).toString());

        return number;
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode storedCode = verificationCode.get(email);
        log.info("저장된 코드 {}", storedCode);
        if(storedCode != null){
            long currentTime = System.currentTimeMillis();
            if(currentTime - storedCode.getTimestamp() < 300000){
                return storedCode.getCode().equals(code);
            }
        }
        return false;
    }

}
