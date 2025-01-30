//package com.example.social.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//import java.util.Date;
//
//public class JwtTokenUtil {
//
//    // JWT Token 발급
//    public static String createToken(String loginId, String key, long expireTimeMs) {
//        // Claim = Jwt Token에 들어갈 정보
//        // Claim에 loginId를 넣어 줌으로써 나중에 loginId를 꺼낼 수 있음
//        Claims claims = Jwts.claims();  // 형변환 필요 없음
//        claims.put("loginId", loginId);  // Claims에 loginId 추가
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//    }
//
//    // Claims에서 loginId 꺼내기
//    public static String getLoginId(String token, String secretKey) {
//        return extractClaims(token, secretKey).get("loginId").toString();  // Claims에서 loginId 추출
//    }
//
//    // 발급된 Token이 만료 시간이 지났는지 체크
//    public static boolean isExpired(String token, String secretKey) {
//        Date expiredDate = extractClaims(token, secretKey).getExpiration();  // 만료일자 추출
//        return expiredDate.before(new Date());  // 만료일자가 현재 시간보다 이전인지 체크
//    }
//
//    // SecretKey를 사용해 Token Parsing
//    private static Claims extractClaims(String token, String secretKey) {
//        return Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(token)  // Token을 파싱하여 Claims 반환
//                .getBody();
//    }
//}
