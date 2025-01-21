package pla.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Q&A(질문과 답변) 엔티티.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자
    private String content; // 질문 또는 답변의 내용
    private char state; // 상태 ('Q': 질문, 'A': 답변)

    @OneToOne
    @JoinColumn(name = "qna_id")
    private QnaList qnaId; // Q&A목록
}
