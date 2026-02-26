package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.*;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    // 파라미터가 많으면 Dto를 아예 만들어서 관리해라. 가장 중요한건 변경할 데이터를 명확하게 하여 전달하는것이 중요하다.
    public void updateItem(Long itemId, String name,  int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId); // 영속성 컨텍스트
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);

        // repository save 안해도됨. 변경감지로인해
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long id){
        return itemRepository.findOne(id);
    }
}
