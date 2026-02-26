package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    // public 으로 할 경우 사람들이 호출할 수 있기때문에, JPA에서는 protected 까지는 허용 해줌.
    protected Address() {} // JPA 구현 라이브러리의 특성상 리플렉션이나 프록시를 사용하려면 기본 생성자가 필요하기 때문에 없으면 에러가 난다.

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
