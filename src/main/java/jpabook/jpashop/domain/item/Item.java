package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;
import jpabook.jpashop.exception.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속관계에서 부모테이블에 테이블전략 기입.
@DiscriminatorColumn(name="dtype")
public abstract class Item {

    @GeneratedValue
    @Id
    @Column(name="item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//

    /**
     * stock 증가
     **
     */
    public void addStock(int stockQuantity) {
        this.stockQuantity += stockQuantity;
    }


    /*
    * stock 감소
    * */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity -= quantity;

        // 재고 수량보다 더 많은 재고만큼 감소시키려고 시도할때.
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }

        this.stockQuantity = restStock;
    }
}
