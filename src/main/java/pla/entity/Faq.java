package pla.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 자주 묻는 질문(FAQ) 엔티티.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String question; // 질문

    @Column(nullable = false)
    private String answer; // 답변

//    @Column(nullable = false)
//    private char deletedYn; // 삭제 여부 ('Y': 삭제, 'N': 미삭제)

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
