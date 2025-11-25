package com.apple.shop.item;

import com.apple.shop.comment.Comment;
import com.apple.shop.comment.CommentRepository;
import com.apple.shop.member.Member;
import com.apple.shop.member.MemberRepository;
import com.apple.shop.member.MemberService;
import com.apple.shop.sales.Sales;
import com.apple.shop.sales.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final S3Service s3Service;
    private final CommentRepository commentRepository;

    @GetMapping("/list")
    String list(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login?needLogin";
        }
        List<Item> result = itemRepository.findAll();
        model.addAttribute("items", result);
        return "redirect:/list/page/1";
    }

    @GetMapping("/list/page/{page}")
    String listPage(@PathVariable Integer page, Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login?needLogin";
        }
        ItemPageDTO response = itemService.getPagedItems(page, 5);  // 페이지 크기 5 고정

        model.addAttribute("items", response.getItems());
        model.addAttribute("startPage", response.getStartPage());
        model.addAttribute("endPage", response.getEndPage());
        model.addAttribute("currentPage", response.getCurrentPage());
        model.addAttribute("prevGroupPage", response.getPrevGroupPage());
        model.addAttribute("nextGroupPage", response.getNextGroupPage());
        model.addAttribute("totalPages", response.getTotalPages());
        return "list";
    }

    @GetMapping("/write")
    @PreAuthorize("hasRole('ADMIN')")
        String write(Authentication auth){
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login?needLogin";
        }
        return "write";
    }
    @PostMapping("/add")
        String addPost(String title,Integer price, @RequestParam(value = "imgUrl", required = false) String imgUrl,
                       Model model, Authentication auth) {

        Item tempItem = new Item();
        tempItem.setTitle(title);
        tempItem.setPrice(price);
        tempItem.setImgUrl(imgUrl);

        if (title == null || title.isBlank()){
            model.addAttribute("error1", "상품 명을 입력해야합니다.");
            model.addAttribute("data", tempItem);
            return "write";
        }
        if (price == null || price < 1 || price > 10000000){
            model.addAttribute("error2", "1원 이상, 1천만원 미만의 가격을 입력해야합니다.");
            model.addAttribute("data", tempItem);
            return "write";
        }
        // 이미지 확장자 제한 (jpg, jpeg, gif, png, webp 허용)
        if (imgUrl != null && !imgUrl.isBlank()) {
            String lower = imgUrl.toLowerCase();
            if (!(lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif")
                    || lower.endsWith(".png") || lower.endsWith(".webp"))) {
                model.addAttribute("error3", "이미지 파일은 JPG, JPEG, GIF, PNG, WEBP 형식만 가능합니다.");
                model.addAttribute("data", tempItem);
                return "write";
            }
        }
        String username = auth.getName();
        itemService.saveItem(title, price, username, imgUrl);
        return "redirect:/list";
    }

    @GetMapping("/edit/{id}")
    String editPost(@PathVariable Integer id, Model model,Authentication auth) {
        Optional<Item> result = itemRepository.findById(id.longValue());
        if (result.isPresent()) {

            String username = auth.getName();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && !result.get().getUsername().equals(username)) {
                model.addAttribute("errorMessage", "접근 권한이 없습니다.");
                return "redirect:/";
            }
            model.addAttribute("data", result.get());
            return "edit";
        } else {
            return "redirect:/";
        }
    }
    @PostMapping("/edit")
    String editPost(String title, Integer price, long id, @RequestParam(value = "imgUrl", required = false) String imgUrl,
                    @RequestParam(value = "count", required = false) Integer count,
                    Model model, Authentication auth) {

        Item tempItem = new Item();
        tempItem.setTitle(title);
        tempItem.setPrice(price);
        tempItem.setImgUrl(imgUrl);
        tempItem.setCount(count);

        if (title == null || title.isBlank()){
            model.addAttribute("error1", "상품 명을 입력해야합니다.");
            model.addAttribute("data", tempItem);
            return "edit";
        }
        if (price == null || price < 1 || price > 10000000){
            model.addAttribute("error2", "1원 이상, 1천만원 미만의 가격을 입력해야합니다.");
            model.addAttribute("data", tempItem);
            return "edit";
        }
        // 이미지 확장자 제한 (jpg, jpeg, gif, png, webp 허용)
        if (imgUrl != null && !imgUrl.isBlank()) {
            String lower = imgUrl.toLowerCase();
            if (!(lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif")
                    || lower.endsWith(".png") || lower.endsWith(".webp"))) {
                model.addAttribute("error3", "이미지 파일은 JPG, JPEG, GIF, PNG, WEBP 형식만 가능합니다.");
                model.addAttribute("data", tempItem);
                return "edit";
            }
        }
        String username = auth.getName();
      itemService.editItem(title, price, id, username, imgUrl, count);
        return "redirect:/detail/" + id;
    }

    @GetMapping("/detail/{id}")
    String detail(@PathVariable Integer id, Model model, Authentication auth){
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login?needLogin";
        }
        List<Comment> comments = commentRepository.findTop10ByParentIdOrderByCreatedAtDesc(id.longValue());
            model.addAttribute("comments", comments);

        Optional<Item> result = itemRepository.findById(id.longValue());
        if(result.isPresent()){
            model.addAttribute("data", result.get());
            System.out.println(result.get());
        }

        return "detail";
    }

    @DeleteMapping("/delete")
        ResponseEntity<String> deletePost(@RequestParam Long id,Authentication auth) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !item.getUsername().equals(username)) {
            return ResponseEntity.status(403).body("삭제 권한이 없습니다.");
        }
            itemRepository.deleteById(id);
            return ResponseEntity.status(200).body("삭제 완료");
        }

    @GetMapping("/presigned-url")
    @ResponseBody
    String getURL(@RequestParam String filename){
        return s3Service.createPresignedUrl(filename);
    }

    @GetMapping("/search")
    String searchPost(@RequestParam String searchText, @RequestParam(defaultValue = "1") Integer page, Model model){

        int pageSize = 5;
        ItemPageDTO result = itemService.searchPagedItems(searchText, page, pageSize);
        //List<Item> result = itemRepository.findByTitleContains(searchText);
        model.addAttribute("items", result.getItems());
        model.addAttribute("startPage", result.getStartPage());
        model.addAttribute("endPage", result.getEndPage());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("prevGroupPage", result.getPrevGroupPage());
        model.addAttribute("nextGroupPage", result.getNextGroupPage());
        model.addAttribute("totalPages", result.getTotalPages());
        //model.addAttribute("items", result);

        model.addAttribute("searchText", searchText);

        System.out.println(result);
        return "list";
    }






    }

