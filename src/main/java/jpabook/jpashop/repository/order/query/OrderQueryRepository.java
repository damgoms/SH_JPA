package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o-> {
             List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
             o.setOrderItems(orderItems);
        });

        return result;
    }


    public List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                           "from Order o " +
                          " join o.member m " +
                          " join o.delivery d " , OrderQueryDto.class)
                     .getResultList();
    }


    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders(); // 주문을 다가져옴 - 1번째쿼리실행.

        // userA, userB 의 ID를 전부 가짐.
        List<Long> orderIds = result.stream()
                        .map(o -> o.getOrderId())//OrderQueryDto -> orderId (return type이 orderId(Long)로 바뀜)
                        .collect(Collectors.toList());

        // 2번째 쿼리실행
        List<OrderItemQueryDto> orderItems = em.createQuery(
                " select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                       " from OrderItem oi " +
                       " join oi.item i" +
                       " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();


        Map<Long, List<OrderItemQueryDto>> orderItemMap =  orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto->OrderItemQueryDto.getOrderId())); // Map 으로 전환해주는데 key가 orderId, value 가 OrderItemQueryDto

        result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        // userA, userB 의 id를 in으로.
        return em.createQuery(
                        " select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                                " from OrderItem oi " +
                                " join oi.item i" +
                                " where oi.order.id in :orderId" ,OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                " select new " +
                        " jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice,oi.count)" +
                        " from Order o " +
                        " join o.member m " +
                        " join o.delivery d " +
                        " join o.orderItems oi " +
                        " join oi.item i "
        , OrderFlatDto.class)
        .getResultList();
    }
}
