package pla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindPwResponse {
    private String receiveAddress;
    private String mailTitle;
    private String mailContent;
}
