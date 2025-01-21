package pla.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pla.entity.DataBoard;

@Repository
public interface DataBoardRepository extends JpaRepository<DataBoard,Long> {
}
