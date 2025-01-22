package pla.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pla.entity.Apply;


@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
    @Query("SELECT a FROM Apply a WHERE a.id = :id")
    Apply selectApplyDetail(Long id); // ID로 특정 게시물 정보를 반환
    // 전체 신청서 목록 (관리자 전용)
    List<Apply> findAll(Sort sort);
    // 특정 사용자 신청서 목록
    List<Apply> findAllByUidAndRequestOrderByCreatedAtDesc(Long uid, String request);
    Apply findByUid(Long uid);
    public List<Apply> findAllByCompletedYnOrderByIdAsc(char completedYn);
    List<Apply> findAllByUidAndRequest(Long uid, String request);
}
