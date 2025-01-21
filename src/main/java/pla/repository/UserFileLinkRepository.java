package pla.repository;

import pla.entity.UserFileLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileLinkRepository extends JpaRepository<UserFileLink, Long> {
    public UserFileLink findByApplyIdAndUid(Long applyId, Long uid);
}
