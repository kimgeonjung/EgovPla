package pla.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pla.entity.QnaList;

@Repository
public interface QnaListRepository extends JpaRepository<QnaList, Long> {
    public List<QnaList> findAllByUidOrderByIdDesc(Long uid);
    public List<QnaList> findAllByOrderByIdDesc();
    public List<QnaList> findAllByStateOrderByIdAsc(char state);
}
