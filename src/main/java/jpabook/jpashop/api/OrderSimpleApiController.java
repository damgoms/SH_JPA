package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XtoOne
 * Order
 * Order --> Member  (Many To one)
 * Order -> Delivery  (One to One)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /*
    * V1 : Entity 를 다이렉트로 반환하는거임.  Entity 가 노출 됨 권장하지않는다.
    * 그냥 이런게 있구나 하는정도..
    * */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // order.getMember() 까지는 proxy 지만, getName 은 실제로 값이 있기 때문에 Lazy 가 강제 초기화 됨.
            order.getDelivery().getAddress();
        }
        return all;
    }

    /**
     * V2 : 반환 타입을 Entity -> Dto로 변경한거임.
     * Entity를 노출하지 않아서 좋지만, 아직 fetch join 을 사용하지 않았기 때문에
     * 각 연관관계에 의해서 Lazy Loading (1+N) 현상이 발생하여 성능 이슈가 발생함.
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orverV2() {
        // order 2개 조회쿼리
        // n + 1 문제 -> 1 + 회원 N +  배송 N = 아래 예제로는 5번 쿼리가 실행 됨.
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                                    .map(o-> new SimpleOrderDto(o))
                                    //.map(SimpleOrderDto::new)
                                    .collect(Collectors.toList());
        return result;
    }

    /**
     * V3 : 반환타입 -> Dto
     * Repository 에서 데이터 조회시, 각 테이블을 fetch join 으로 한번에 조회하기 때문에
     * Lazy Loading 이발생하지 않으며, 한번의 쿼리만 수행된다.
     * 그러나, 모든 테이블의 칼럼을 조회 하는 형태를 띈다.
     */
        // 장점 : 데이터 변경 가능, fetch join 만으로 쉽게 단점 : select 절에서 모두 조회함.
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orverV3() {
        //fetch 조인으로 이미 조회가 되었기 때문에 지연로딩의 대상이 되지 않고, n+1 문제가 해결된다.
        //프록시 객체가 아닌, 실제 데이터를 조회하여 값을 다 채우기 때문에 더 이상 지연로딩의 대상이 되지 않는 것이다.
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * V4 : 반환타입 -> Dto
     * Repository에서 API 스펙에 맞게끔만 조회를 하며, JPQL에서 쿼리 조회시에
     * new 키워드와 함께 패키지 단위로 모두 기재한 후 return 해야 한다.
     * 쿼리의 성능은 최적화 되었다고 할 수 있으나 미비하다.. (테이블의 칼럼이 너~무 많은 경우엔 대안이 될 수 있다..)
     * API스펙이 변경되면 얘도 수정이 필요하다.. 실무에서 더욱 복잡한 쿼리라면? 좀 쓰기 꺼려질거 같기도 함..
     */
    // select 절에서 필요한 데이터만 조회 할 수 있음.  단점 : 로직 재활용 불가, 코드 복잡..  장점 : v3보다는 조금 최적화 됨. (장점 중 단점 : 생각보다 미비함)
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orverV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }


    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // Lazy가 초기화 됨. -> 영속성 컨텍스트가  memberId를 가지고 영속성 컨텍스트를 찾아옴 근데 없으면 db 쿼리를 날림.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // lazy가 초기화 됨.
        }
    }
}
