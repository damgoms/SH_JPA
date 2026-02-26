package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // JPA는 기본적으로 트랜잭션 안에서 데이터 변경이 일어나야 하기 때문에 이 어노테이션이 필수로 필요함.
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional
    public Long join(Member member) {

        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();

    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION

        /*
        * 이렇게 VALIDATION 을 넣어도 WAS가 여러개 떠있을경우 해당 로직을 동시에 통과하게 되면 무용지물이 될 수 있다.
        * 따라서 멀티쓰레드에 대한 대비를 하기 위해 데이터베이스에 유니크 인덱스를 걸어서 한번더 방어 해주는것이 좋다.
        * */
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원 입니다.");
        }
    }


    //회원 전체 조회
    public List<Member> findByMembers() {
        return memberRepository.findAll();
    }


    //한건 조회
    public Member findById(Long id) {
        return memberRepository.findById(id).get();
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id).get();
        member.setName(name);
    }
}
