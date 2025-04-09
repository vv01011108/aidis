package com.example.social.controller;

import com.example.social.dto.FollowDTO;
import com.example.social.dto.UserDTO;
import com.example.social.entity.User;
import com.example.social.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    // 팔로우 api
    @PostMapping
    public ResponseEntity<Map<String, Object>> follow(@RequestBody FollowDTO followDTO) {
        try {
            FollowDTO newFollow = followService.follow(followDTO.getFollowerId(), followDTO.getFolloweeId());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "팔로우 되었습니다.",
                    "data", newFollow
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // 언팔로우 api
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> unfollow(@RequestParam Long userId, @RequestParam Long targetUserId) {
        try {
            followService.unfollow(userId, targetUserId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "언팔로우 되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // 맞팔로우 api
    @PostMapping("/mutual")
    public ResponseEntity<Map<String, Object>> mutualFollow(@RequestBody FollowDTO followDTO) {
        try {
            FollowDTO newFollow = followService.mutualFollow(followDTO.getFollowerId(), followDTO.getFolloweeId());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "맞팔로우 되었습니다.",
                    "data", newFollow
            ));
        } catch (IllegalArgumentException e) {
            System.out.println("Error occurred: " + e.getMessage()); // 예외 메시지 출력
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // 검색 기능에서 팔로우 여부 조회 api
    @GetMapping("/followers/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowers(@PathVariable Long userId) {
        List<User> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", followers
        ));
    }

    // 나를 팔로우하는 목록 조회 api (검색이랑 다른 점: FOLLOWING, ACCEPTED 나눠서 보내줌)
    @GetMapping("/followers-list/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowersWithStatus(@PathVariable Long userId) {
        // 팔로워 목록을 서비스에서 받아옴
        Map<String, List<UserDTO>> followers = followService.getFollowersWithStatus(userId);


        // 응답 데이터와 함께 상태를 반환
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", followers);

        // 200 OK 상태 코드와 함께 응답
        return ResponseEntity.ok(response);
    }


    // 팔로우 상태 확인 api
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getFollowStatus(@RequestParam Long followerId, @RequestParam Long followeeId) {
        try {

            Map<String, Boolean> followStatus = followService.getFollowStatus(followerId, followeeId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", followStatus
            ));
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 자세한 오류 출력
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", "팔로우 상태를 확인하는 중 오류가 발생했습니다."
            ));
        }
    }

    // 팔로잉 목록 조회 - 이건 어디서 쓰는 거지
    @GetMapping("/followees/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowees(@PathVariable Long userId) {
        List<User> followees = followService.getFollowees(userId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", followees
        ));
    }



}
