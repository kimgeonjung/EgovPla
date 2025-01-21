package pla.dto;

import lombok.Data;

@Data
public class MemberRequest {
    private String email;
    private String loginId;
    private String password;
    private String name;
    private String tel;
    private String zipcode;
    private String address;
    private String detailAddress;
}
