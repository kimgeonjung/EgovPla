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
    private Long id;

    @Column(name="login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
    private String zipcode;
    @Column(nullable = false)
    private String address;
    @Column(name = "detail_address")
    private String detailAddress;

    private String role = "USER";

    public boolean isAdmin() {
        return "ADMIN".equals(this.role); // role이 "ADMIN"이면 관리자
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
