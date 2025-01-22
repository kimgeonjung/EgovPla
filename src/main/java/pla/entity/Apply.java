package pla.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상세 분석 신청서 엔티티.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String author; // 작성자

    @Column(nullable = false)
    private String content; // 내용

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일시
    
    private Long uid; //사용자 아이디
    private char completedYn; // 분석 완료 여부 ('Y': 완료, 'N': 미완료)

    private String link; //링크
    private String location; // 지역
    private String type; // ai 종류
    private String request; // 요청 타입 ("normal" : 기본 입지 분석, "detail" : 상세 입지 분석)
    
    @ElementCollection
    @CollectionTable(name = "apply_files", joinColumns = @JoinColumn(name = "apply_id"))
    private List<FileDetail> files = new ArrayList<>(); // 첨부 파일 리스트

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
