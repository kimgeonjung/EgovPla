package pla.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pla.dto.AuthInfo;
import pla.entity.Member;
import pla.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;

    public void updateProfile(AuthInfo authInfo) {
        // 데이터베이스에서 사용자를 조회한 후, 데이터를 업데이트합니다.
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 프로필 정보 업데이트
        member.setLoginId(authInfo.getLoginId());
        member.setEmail(authInfo.getEmail());
        member.setName(authInfo.getName());
        member.setTel(authInfo.getTel());
        member.setZipcode(authInfo.getZipcode());
        member.setAddress(authInfo.getAddress());
        member.setDetailAddress(authInfo.getDetailAddress());


        // 변경된 정보를 데이터베이스에 저장
        memberRepository.save(member);
    }

}
