package pla.service;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pla.dto.ChangePwRequest;
import pla.dto.FindPwRequest;
import pla.dto.FindPwResponse;
import pla.dto.LoginDto;
import pla.dto.MemberRequest;
import pla.entity.Member;
import pla.exception.BadCredentialsException;
import pla.mail.MailProperties;
import pla.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdminAccount() {
        // 관리자 계정이 이미 존재하는지 확인
        Optional<Member> existingAdmin = memberRepository.findByName("관리자");

        if (existingAdmin.isEmpty()) {
            Member admin = new Member();
            admin.setLoginId("admin");
            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // 관리자 비밀번호
            admin.setName("관리자");
            admin.setTel("000-0000-0000");
            admin.setZipcode("123");
            admin.setAddress("123");
            admin.setDetailAddress("123");
            admin.setRole("ADMIN"); // 관리자 역할

            // 관리자 정보 저장
            memberRepository.save(admin);
        }
    }
    
    @PostConstruct
    public void createUserAccount() {
        // 사용자 계정이 이미 존재하는지 확인
        Optional<Member> existingUser = memberRepository.findByName("유저");

        if (existingUser.isEmpty()) {
            Member user = new Member();
            user.setLoginId("123");
            user.setEmail("user@user.com");
            user.setPassword(passwordEncoder.encode("123")); // 사용자 비밀번호
            user.setName("유저");
            user.setTel("000-0000-0000");
            user.setZipcode("123");
            user.setAddress("123");
            user.setDetailAddress("123");
            user.setRole("USER"); // 사용자 역할

            // 사용자 정보 저장
            memberRepository.save(user);
        }
    }
    
    public boolean checkLoginId(String loginId){
        return memberRepository.findByLoginId(loginId).isEmpty();
//      비어있으면 true, 아니면 false
    }

    public boolean checkEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
    
    public String registerMember(MemberRequest request) {
        Member member = new Member();
        member.setLoginId(request.getLoginId());
        member.setEmail(request.getEmail());
        member.setPassword(passwordEncoder.encode(request.getPassword())); // 비밀번호 암호화
        member.setName(request.getName());
        member.setTel(request.getTel());
        member.setZipcode(request.getZipcode());
        member.setAddress(request.getAddress());
        member.setDetailAddress(request.getDetailAddress());
        member.setRole("USER");

        memberRepository.save(member);
        return "회원가입 성공";
    }

    public boolean loginMember(LoginDto dto) {
        Member member = memberRepository.findByLoginId(dto.getLoginId()).orElse(null);
        if (member != null && passwordEncoder.matches(dto.getPassword(), member.getPassword())){ // 비밀번호 암호화 필요
            return true;
        }
        return false;
    }

    public String findLoginIdByNameAndEmail(String name, String email) {
        return memberRepository.findLoginIdByNameAndEmail(name, email).orElse(null);
    }

    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정 정보입니다."));
    }

    private final MailProperties mailProperties;
    private final MailSender mailSender;

    // 임시 비밀번호 발급
    public String findPw(FindPwRequest request) {
        Member member = findByLoginId(request.getLoginId());

        if(!member.getEmail().equals(request.getEmail())){
            throw new BadCredentialsException("이메일이 맞지 않습니다");
        }
        char[] charSet = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        StringBuilder tempPw = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int idx = (int) (charSet.length * Math.random());
            tempPw.append(charSet[idx]);
        }

        String newPw = tempPw.toString();
        String body = "안녕하세요. "+member.getName()+"님. 임시 비밀번호를 발급해드립니다.\n"+
                "회원님의 임시 비밀번호는 "+newPw+" 입니다.\n" +
                "임시 비밀번호로 로그인 후 비밀번호를 변경해주세요.";

        FindPwResponse response = FindPwResponse.builder()
                .receiveAddress(request.getEmail())
                .mailTitle("공공복합시설 입지분석 플랫폼 임시 비밀번호 발급")
                .mailContent(body)
                .build();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(mailProperties.getSender());
        message.setTo(response.getReceiveAddress());
        message.setSubject(response.getMailTitle());
        message.setText(response.getMailContent());

        mailSender.send(message);
        member.updatePassword(passwordEncoder.encode(newPw));
        memberRepository.save(member);

        return "임시 비밀번호 발급.";
    }

    public String changePw(ChangePwRequest request) {
        Member member = findByLoginId(request.getLoginId());
        if(!passwordEncoder.matches(request.getOldPw(), member.getPassword())){
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(passwordEncoder.encode(request.getNewPw()));
        memberRepository.save(member);

        return "비밀번호가 성공적으로 변경되었습니다.";
    }
}
