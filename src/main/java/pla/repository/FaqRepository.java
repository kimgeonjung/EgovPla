package pla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pla.entity.Faq;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
//    List<Faq> findAllByDeletedYn(char deleted);
}
