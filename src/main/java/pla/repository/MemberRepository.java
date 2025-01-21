package pla.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pla.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByEmail(String email);

    @Query("select m.loginId from Member m where m.name = :name and m.email = :email")
    Optional<String> findLoginIdByNameAndEmail(String name, String email);

	Optional<Member> findByName(String string);
}
