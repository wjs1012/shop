package com.apple.shop.member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor

public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final RecaptchaService recaptchaService;

    @GetMapping("/signup")
    String signup(Authentication auth){
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/list";
        }
        return "signup";
    }
    @PostMapping("/signup")
    String addsignup(HttpServletRequest request, Model model) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordConfirm");
        String email = request.getParameter("email");
        String recaptchaToken = request.getParameter("g-recaptcha-response");

        if (!recaptchaService.verifyToken(recaptchaToken)) {
            model.addAttribute("error", "reCAPTCHA 인증에 실패했습니다. 다시 시도해 주세요.");
            return "signup";
        }

        String error = memberService.signup(username, password, passwordConfirm, email);

        if (error != null) {
            model.addAttribute("error", error);
            return "signup";
        }
        return "redirect:/login";
        }

    @GetMapping("/login")
    String login() {
        return "login.html";
    }

    @GetMapping("/mypage")
    public String myPage(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login?needLogin";
        }
        String username = auth.getName();
        MemberDTO dto = memberService.getMyPageDto(username);
        model.addAttribute("member", dto);
        model.addAttribute("role", dto);
        return "mypage";
    }


}
