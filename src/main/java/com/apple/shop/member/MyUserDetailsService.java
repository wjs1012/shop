package com.apple.shop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       var result = memberRepository.findByUsername(username);
        if (result.isEmpty()){
            throw new UsernameNotFoundException("존재하지 않는 아이디 입니다.");
        }
        var user = result.get();

        String role = user.getRole(); //USER 또는 ADMIN
        if (role == null) role = "USER"; // NULL을 USER로 디폴트
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return new User(user.getUsername(), user.getPassword(), authorities);

       }




}
