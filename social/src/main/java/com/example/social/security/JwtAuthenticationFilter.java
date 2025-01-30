//package com.example.social.security;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@WebFilter
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final String secretKey = "yourSecretKey";  // 실제 배포 환경에서는 이 값을 안전하게 관리
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = getTokenFromRequest(request);
//
//        if (token != null && !JwtTokenUtil.isExpired(token, secretKey)) {
//            String loginId = JwtTokenUtil.getLoginId(token, secretKey);
//
//            // UsernamePasswordAuthenticationToken을 이용해 인증 객체를 생성하고 SecurityContext에 저장
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    loginId, null, null
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);  // 계속해서 필터 체인 진행
//    }
//
//    private String getTokenFromRequest(HttpServletRequest request) {
//        String header = request.getHeader("Authorization");
//
//        // Authorization header에서 "Bearer " 접두어를 제외한 token 부분만 추출
//        if (header != null && header.startsWith("Bearer ")) {
//            return header.substring(7);
//        }
//
//        return null;
//    }
//}
