package pla.dto.validation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KioskValidationDto {
    @NotNull(message = "처리 방식은 필수입니다.")
    private String mode; // 병렬 or 순차 처리

    @NotNull(message = "표출할 상위 개수는 필수입니다.")
    @Min(value = 1, message = "최소 1 이상이어야 합니다.")
    @Max(value = 1000, message = "최대 1000 이하이어야 합니다.")
    private Integer topNKiosk;

    @NotNull(message = "알파 값은 필수입니다.")
    @Min(value = 0, message = "알파 값은 최소 0 이상이어야 합니다.")
    @Max(value = 1, message = "알파 값은 최대 1 이하이어야 합니다.")
    private Double alpha;
}
