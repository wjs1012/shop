package com.apple.shop.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public void saveItem(String title, Integer price, String username, String imgUrl){
        Item item = new Item();
        item.setTitle(title);
        item.setPrice(price);
        item.setUsername(username);
        item.setImgUrl(imgUrl);
        item.setCount(10);
        itemRepository.save(item);

    }
    public void editItem(String title, Integer price, long id, String username, String imgUrl, Integer count){
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
        item.setId(id);
        item.setTitle(title);
        item.setPrice(price);
        item.setUsername(username);
        item.setImgUrl(imgUrl);
        if (count != null) {
            item.setCount(count);
        }
        itemRepository.save(item);
    }

    public ItemPageDTO getPagedItems(int pageNumber, int pageSize) {
        Page<Item> result = itemRepository.findPageBy(PageRequest.of(pageNumber - 1, pageSize));

        int totalPages = result.getTotalPages(); //html에서 페이지 이동 버튼에 쓸 변수들
        if (totalPages == 0) {
            totalPages = 1; // 최소 1페이지로 고정
        }
        int currentPage = result.getNumber() + 1; // Page는 0부터 시작하니 +1 해줌

        int pageGroupSize = 5; // 버튼에 페이지 표기 숫자
        int currentGroup = Math.max(1, (int) Math.ceil((double) currentPage / pageGroupSize)); // 현재 페이지가 어느 배수 그룹인지 파악용 나누기 5하고 소수점 올림내림

        int startPage = (currentGroup - 1) * pageGroupSize + 1; //5의배수 그룹의 시작페이지 몇번인지 구하기
        int endPage = Math.min(startPage + pageGroupSize - 1, totalPages); //끝페이지 있는 그룹에서 5의배수로 안 떨어지면 나머진 안보이게 하기

        // 다음 그룹의 첫 페이지가 totalPages보다 작거나 같을 때만 이동 가능
        int prevGroupPage = Math.max(startPage - 1, 1); // 1보다 아래로는 못감
        int nextGroupPage = Math.min(endPage + 1, totalPages);

        return new ItemPageDTO(result, startPage, endPage, currentPage, prevGroupPage, nextGroupPage, totalPages);
    }
    public ItemPageDTO searchPagedItems(String searchText, int pageNumber, int pageSize) {
        Page<Item> result = itemRepository.findByTitleContains(searchText, PageRequest.of(pageNumber - 1, pageSize));

        int totalPages = result.getTotalPages();
        if (totalPages == 0) {
            totalPages = 1; // 최소 1페이지 고정
        }
        int currentPage = result.getNumber() + 1;

        int pageGroupSize = 5;
        int currentGroup = Math.max(1, (int) Math.ceil((double) currentPage / pageGroupSize));

        int startPage = (currentGroup - 1) * pageGroupSize + 1;
        int endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

        int prevGroupPage = Math.max(startPage - 1, 1);
        int nextGroupPage = Math.min(endPage + 1, totalPages);

        return new ItemPageDTO(result, startPage, endPage, currentPage, prevGroupPage, nextGroupPage, totalPages);
    }
}
