package com.apple.shop.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class ItemPageDTO {
    private Page<Item> items;
    private int startPage;
    private int endPage;
    private int currentPage;
    private int prevGroupPage;
    private int nextGroupPage;
    private int totalPages;
}
