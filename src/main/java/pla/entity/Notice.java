package pla.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 공지사항 엔티티.
 * 공지 제목, 내용, 첨부 파일 및 조회수 정보를 관리합니다.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    @Column(nullable = false)
    private String title; // 공지 제목

    @Column(nullable = false, columnDefinition = "TEXT") // 긴 텍스트 저장
    private String content; // 공지 내용

    @Column(nullable = false)
    private Integer hits; // 조회수

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 생성일시

    @ElementCollection
    @CollectionTable(name = "notice_files", joinColumns = @JoinColumn(name = "notice_id"))
    private List<FileDetail> files = new ArrayList<>(); // 첨부 파일 리스트


    // 생성일시 자동 설정
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();  // 생성시 현재 시간 설정
        }
    }
}
