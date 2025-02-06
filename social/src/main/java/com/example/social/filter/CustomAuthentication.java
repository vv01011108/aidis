//package com.example.social.filter;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.util.Collection;
//import java.util.Collections;
//
//public class CustomAuthentication implements Authentication {
//
//    private final String username;
//    private boolean authenticated;
//    private Collection<? extends GrantedAuthority> authorities;
//
//    public CustomAuthentication(String username) {
//        this.username = username;
//        this.authenticated = true;
//        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); // 기본 권한 설정
//    }
//
//    @Override
//    public String getName() {
//        return username;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;  // 권한을 반환
//    }
//
//    @Override
//    public Object getCredentials() {
//        return null; // JWT에서는 자격증명이 따로 없으므로 null 반환
//    }
//
//    @Override
//    public Object getDetails() {
//        return null; // 추가적인 세부사항이 필요 없으면 null
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return username; // 사용자 이름 반환
//    }
//
//    @Override
//    public boolean isAuthenticated() {
//        return authenticated; // 인증 여부
//    }
//
//    @Override
//    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
//        this.authenticated = isAuthenticated;
//    }
//}
