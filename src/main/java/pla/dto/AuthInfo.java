package pla.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Setter
public class AuthInfo {

	private Long id;
	private String loginId;
	private String email;
	private String name;
	private String tel;
	private String zipcode;
	private String address;
	private String detailAddress;
	private String role;
}
