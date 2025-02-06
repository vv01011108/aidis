//package com.example.social.filter;
//
//import com.example.social.util.JwtUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private JwtUtil jwtUtil;
//
//    // JwtUtil 주입받는 생성자
//    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
//        String token = request.getHeader("Authorization");
//        System.out.println("헤더의 Authorization 값: " + token); // 디버깅용 로그
//
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7); // "Bearer " 제거
//
//            String username = jwtUtil.extractUsername(token);
//            System.out.println("추출된 사용자 이름: " + username); // 디버깅용 로그
//
//            // 토큰이 유효하고 사용자 이름이 있으면 인증 객체 설정
//            if (username != null && jwtUtil.validateToken(token, username)) {
//                Authentication authentication = new CustomAuthentication(username);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                return authentication; // 인증 처리 완료
//            }
//        }
//
//        return null; // 토큰이 없거나 유효하지 않으면 인증 실패
//    }
//
//}
//
