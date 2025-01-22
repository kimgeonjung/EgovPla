package pla.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유식별자

    @Column(name="login_id", nullable = false, unique = true)
    private String loginId; // 로그인아이디

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false)
    private String tel; // 전화번호

    @Column(nullable = false)
    private String zipcode; // 우편번호
    @Column(nullable = false)
    private String address; // 주소
    @Column(name = "detail_address")
    private String detailAddress; // 상세주소

    private String role = "USER"; // 권한

    public boolean isAdmin() {
        return "ADMIN".equals(this.role); // role이 "ADMIN"이면 관리자
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
