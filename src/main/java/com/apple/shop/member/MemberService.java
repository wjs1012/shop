package com.apple.shop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 검증 가입 처리
    @Transactional
    public String signup(String username, String password, String passwordConfirm, String email) {
        // 비밀번호 확인
        if (!password.equals(passwordConfirm)) {
            return "비밀번호가 일치하지 않습니다!";
        }
        // 아이디 중복 체크
        if (memberRepository.findByUsername(username).isPresent()) {
            return "이미 가입된 아이디입니다!";
        }
        // 정상 가입
        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setEmail(email);
        member.setRole("USER");
        memberRepository.save(member);
        return null;
    }
    public MemberDTO getMyPageDto(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));
        return new MemberDTO(
                member.getUsername(),
                member.getEmail(),
                member.getCreatedAt(),
                member.getRole()
        );
    }

}
