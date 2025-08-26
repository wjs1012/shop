package com.apple.shop.sales;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class SalesDto {
    private String itemName;
    private Integer price;
    private String userName;

    @Override
    public String toString() {
        return "SalesDto{" +
                "itemName='" + itemName + '\'' +
                ", price=" + price +
                ", userName='" + userName + '\'' +
                '}';
    }
}
