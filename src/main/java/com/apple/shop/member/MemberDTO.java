package com.apple.shop.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberDTO {
    public String username;
    public String email;
    public LocalDateTime createdAt;
    public String role;

    MemberDTO(String username, String email, LocalDateTime createdAt, String role){
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.role = role;
    }
}
