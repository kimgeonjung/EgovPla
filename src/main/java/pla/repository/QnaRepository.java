package pla.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pla.entity.Qna;
import pla.entity.QnaList;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    List<Qna> findAllByQnaIdOrderByIdAsc(QnaList qnaId);
}
