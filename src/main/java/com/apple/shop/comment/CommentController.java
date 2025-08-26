package com.apple.shop.comment;
import com.apple.shop.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final CommentService commentService;

    @PostMapping("/comment")
        String postComment(Authentication auth,
                           @RequestParam String content,
                           @RequestParam Long parentId,
                           @RequestParam String parentTitle){
        if (auth == null) {
            return "redirect:/login?needLogin";
        }
        String username = auth.getName();
        commentService.saveComment(username, content, parentId, parentTitle);

        return "redirect:/detail/" + parentId;
    }
    @PostMapping("/comment/delete")
    public String deleteComment(@RequestParam Long id, @RequestParam Long parentId) {
        commentRepository.deleteById(id);
        return "redirect:/detail/" + parentId;
    }

}
