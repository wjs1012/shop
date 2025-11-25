package com.apple.shop.sales;


import com.apple.shop.item.Item;
import com.apple.shop.item.ItemRepository;
import com.apple.shop.member.Member;
import com.apple.shop.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final SalesRepository salesRepository;

    @Transactional
    public boolean createOrder(Long itemId, String username) {
        Optional<Member> memberOpt = memberRepository.findByUsername(username);
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        boolean ordered = false;

        if (memberOpt.isPresent() && itemOpt.isPresent()) {
            Member member = memberOpt.get();
            Item item = itemOpt.get();

            if (item.getCount() != null && item.getCount() > 0 ) {

                // 주문 정보
                Sales sales = new Sales();
                sales.setItemName(item.getTitle());
                sales.setPrice(item.getPrice());
                sales.setCount(1);
                item.setCount(item.getCount() - 1);
                sales.setMember(member);

                salesRepository.save(sales);

                ordered = true;
            }
        } return ordered;
    }

    public List<Sales> getAllOrders() {
        return salesRepository.findAllByOrderByCreatedDesc();
    }
}
