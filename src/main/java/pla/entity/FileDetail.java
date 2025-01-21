package pla.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
@ToString
public class FileDetail {
    private String filePath;  // 파일 경로
    private String fileName;  // 원본 파일 이름
}
