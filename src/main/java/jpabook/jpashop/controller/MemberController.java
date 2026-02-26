package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm()); // 빈 껍데기라도 만드는 이유는 validation 을 처리하기 때문에.

        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    // javax validation 치면 다양한 validation을 참고 할 수 있다.
    /*
         entity 를 바로받지 않고, Memberform 을 따로만드는 이유는 entity랑 form 화면이랑 정확히 일치하지 않기 때문에,
         form을 따로만들어두는것이 낫다 ( validation 의 이유도 있음)
    */

    public String create(MemberForm form, BindingResult result) { // @Valid 어노테이션을 통해 form 에 선언된 Not Empty valid 사용 가능

        if(result.hasErrors()) {
            return "members/createMemberForm";
        }


        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("members")
    public String list (Model model) {
        List<Member> members = memberService.findByMembers(); //Entity 를 가져오는거보단, Dto를 만들어서 필요한 데이터만 가져오는것이 좋음.
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
