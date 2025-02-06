package com.example.social.controller;

import com.example.social.dto.FriendRequestDTO;
import com.example.social.entity.Friend;
import com.example.social.entity.User;
import com.example.social.service.FriendRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/friend-requests")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendFriendRequest(@RequestBody FriendRequestDTO friendRequestDTO, HttpSession session) {

        // 세션에서 senderId 추출
        User user = (User) session.getAttribute("user");
        Long senderId = user != null ? user.getId() : null;
        Long receiverId = friendRequestDTO.getReceiverId();

        // 로그인 체크
        if (senderId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "error",
                    "message", "로그인이 필요합니다."
            ));
        }

        // 본인에게 요청을 보내는 경우 처리
        if (senderId.equals(receiverId)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "본인에게 친구 요청을 보낼 수 없습니다."
            ));
        }

        try {
            Friend friendRequest = friendRequestService.sendFriendRequest(senderId, friendRequestDTO);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "친구 요청이 성공적으로 생성되었습니다.",
                    "data", friendRequest
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // 친구 요청 확인
    @GetMapping("/pending/{userId}")
    public ResponseEntity<Map<String, Object>> getPendingRequests(@PathVariable Long userId) {
        try {
            List<Friend> pendingRequests = friendRequestService.getPendingRequests(userId);
            List<Map<String, Object>> responseData = new ArrayList<>();

            for (Friend request : pendingRequests) {
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("id", request.getId());
                requestData.put("status", request.getStatus());
                requestData.put("sender", Map.of(
                        "id", request.getSender().getId(),
                        "name", request.getSender().getName(),
                        "email", request.getSender().getEmail()
                ));
                requestData.put("receiver", Map.of(
                        "id", request.getReceiver().getId(),
                        "name", request.getReceiver().getName(),
                        "email", request.getReceiver().getEmail()
                ));
                responseData.add(requestData);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", responseData
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // 친구 요청 수락
    @PutMapping("/{requestId}/accept")
    public ResponseEntity<Map<String, Object>> acceptRequest(@PathVariable Long requestId) {
        try {
            Friend acceptedRequest = friendRequestService.acceptRequest(requestId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "친구 요청이 수락되었습니다.",
                    "data", acceptedRequest
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // 친구 요청 삭제
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Map<String, Object>> deleteRequest(@PathVariable Long requestId) {
        try {
            friendRequestService.deleteRequest(requestId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "친구 요청이 삭제되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // 친구 요청 상태 조회
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getFriendRequestStatus(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        try {
            Optional<Friend.Status> status = friendRequestService.getRequestStatus(senderId, receiverId);
            if (status.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "status", "error",
                        "message", "해당 요청이 존재하지 않습니다."
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", status.get()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}