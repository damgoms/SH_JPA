package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
            this.orderId = orderId;
            this.name = name; // Lazy가 초기화 됨. -> 영속성 컨텍스트가  memberId를 가지고 영속성 컨텍스트를 찾아옴 근데 없으면 db 쿼리를 날림.
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.address = address; // lazy가 초기화 됨.
        }
}
