package com.apple.shop.sales;

import com.apple.shop.item.Item;
import com.apple.shop.item.ItemRepository;
import com.apple.shop.member.Member;
import com.apple.shop.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SalesController {
    private final SalesService salesService;
    private final SalesRepository salesRepository;



    @PostMapping("/sales")
    String orderItem(@RequestParam("itemId") long itemId, Authentication auth) {
        String username = auth.getName();

            salesService.createOrder(itemId, username);

            return "redirect:/detail/" + itemId + "?ordered=true";
        }
//    @GetMapping("/order/all")
//    String getOrderAll(){
//        List<Sales> result = salesRepository.customFindAll();
//        List<SalesDto> dtoList = new ArrayList<>();
//        for (Sales sale : result) {
//            dtoList.add(new SalesDto(
//                    sale.getItemName(),
//                    sale.getPrice(),
//                    sale.getMember().getUsername(
//            ));
//        }
//        System.out.println(dtoList);
//
//
//        return "index";
//    } 콘솔로 조회용 임시

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orderList")
    public String orderList(Model model){
        List<Sales> orderList = salesService.getAllOrders();
        model.addAttribute("orderList", orderList);
        return "orderList";
    }




}
