package com.apple.shop.comment;

import com.apple.shop.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //Page<Comment> findPageBy(Pageable page);
    List<Comment> findTop10ByParentIdOrderByCreatedAtDesc(Long parentId);
}
