package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // SpringBean으로 등록 (@Component) 어노테이션으로 컴포넌트 스캔

public class MemberRepositoryOld {

    //Spring Data Jpa가 PesistanceContext 대신  어노테이션 Autowired 로도 쓰게 지원. @PersistenceContext // 스프링이 EntityManager를 만들어서 Injection(주입) 하게 만들어줌.
    @PersistenceContext
    private EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id)  {
        Member member = em.find(Member.class, id);

        return member;
    }

    public List<Member> findAll() {
        // sql과 차이점은 테이블을 조회하는것이 아닌, entity를 조회한다. (JPQL)
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
