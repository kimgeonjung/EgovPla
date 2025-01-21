package pla.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class VerificationCode {
    private String code;
    private long timestamp;
}
