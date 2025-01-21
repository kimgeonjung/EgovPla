package pla.dto.validation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicWifiValidationDto {
	@NotNull(message = "알파 값은 필수입니다.")
    @Min(value = 0, message = "알파 값은 최소 0 이상이어야 합니다.")
    @Max(value = 1, message = "알파 값은 최대 1 이하이어야 합니다.")
    private Double alpha;

    @NotNull(message = "N gen 값은 필수입니다.")
    @Min(value = 1, message = "N gen 값은 최소 1 이상이어야 합니다.")
    @Max(value = 30000, message = "N gen 값은 최대 30000 이하이어야 합니다.")
    private Integer ngen;

    @NotNull(message = "새로 만들 와이파이 개수는 필수입니다.")
    @Min(value = 1, message = "최소 1 이상이어야 합니다.")
    @Max(value = 1000, message = "최대 1000 이하이어야 합니다.")
    private Integer newWifi;
}
