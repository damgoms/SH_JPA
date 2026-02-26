package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // spring jpa rule에 의해
    // select m from Member m where m.name = ? 이라고 알아서 짜버림. 대신 Name 이라고 해야함.
    List<Member> findByName(String name);
}
