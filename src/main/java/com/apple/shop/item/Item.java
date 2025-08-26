package com.apple.shop.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
@Table(indexes = @Index(columnList = "title", name = "상품명"))

public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    private String title;
    private Integer price;
    private String username;
    private Integer count;

    @Column(name="img_url")
    private String imgUrl; //프리사인드 url 주소 저장할 변수
}
