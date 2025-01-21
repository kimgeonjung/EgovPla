package pla.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Q&A 목록 엔티티.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnaList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유식별자
    private String title; // Q&A 제목
    private LocalDateTime created_at; // 생성일시
    private char endYn; // 종료 여부 ('Y': 종료, 'N': 진행 중)
    private char state; // 상태 ('Q': 질문, 'A': 답변)
    private Long uid; // 질문자 ID
}
