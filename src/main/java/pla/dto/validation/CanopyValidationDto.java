package pla.dto.validation;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CanopyValidationDto {
	
	@NotNull(message = "새로 만들 그늘막 개수는 필수입니다.")
    @Min(value = 1, message = "그늘막 개수는 최소 1 이상이어야 합니다.")
    @Max(value = 100, message = "그늘막 개수는 최대 100 이하이어야 합니다.")
	private Integer newLibraries;

    @NotNull(message = "알파 값은 필수입니다.")
    @Min(value = 0, message = "알파 값은 최소 0 이상이어야 합니다.")
    @Max(value = 1, message = "알파 값은 최대 1 이하이어야 합니다.")
    private Double alpha;

    @NotNull(message = "커버 가능한 크기는 필수입니다.")
    @Min(value = 0, message = "커버 가능한 크기는 최소 0.1 이상이어야 합니다.")
    @Max(value = 10, message = "커버 가능한 크기는 최대 10.0 이하이어야 합니다.")
    private Double coverageRadius;
}
