package com.apple.shop.comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class CommentService {
    private final CommentRepository commentRepository;

    public void saveComment(String username, String content, Long parentId, String parentTitle){
        var data = new Comment();
        data.setUsername(username);
        data.setContent(content);
        data.setParentId(parentId);
        data.setParentTitle(parentTitle);
        commentRepository.save(data);
    }
}
