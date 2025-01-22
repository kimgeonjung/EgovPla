package pla.dto.validation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibraryValidationDto {
	
	@NotNull(message = "새로 만들 도서관 개수는 필수입니다.")
    @Min(value = 1, message = "도서관 개수는 최소 1 이상이어야 합니다.")
    @Max(value = 100, message = "도서관 개수는 최대 100 이하이어야 합니다.")
	private Integer newLibraries;
	
	@NotNull(message = "도서관 가중치는 필수입니다.")
    @Min(value = 0, message = "도서관 가중치는 최소 0 이상이어야 합니다.")
    @Max(value = 1, message = "도서관 가중치는 최대 1 이하이어야 합니다.")
    private Double libraryWeight;

    @NotNull(message = "버스 정류장 가중치는 필수입니다.")
    @Min(value = 0, message = "버스 정류장 가중치는 최소 0 이상이어야 합니다.")
    @Max(value = 1, message = "버스 정류장 가중치는 최대 1 이하이어야 합니다.")
    private Double busWeight;

    @NotNull(message = "표출할 상위 개수는 필수입니다.")
    @Min(value = 1, message = "최소 1 이상이어야 합니다.")
    @Max(value = 1000, message = "최대 1000 이하이어야 합니다.")
    private Integer topNLibraries;
}
