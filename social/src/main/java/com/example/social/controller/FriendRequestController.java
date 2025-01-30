package com.example.social.controller;

import com.example.social.entity.FriendRequest;
import com.example.social.service.FriendRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/friend-requests")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendFriendRequest(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {

        if (senderId.equals(receiverId)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "본인에게 친구 요청을 보낼 수 없습니다."
            ));
        }

        try {
            FriendRequest friendRequest = friendRequestService.sendFriendRequest(senderId, receiverId);
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

    @GetMapping("/pending/{userId}")
    public ResponseEntity<Map<String, Object>> getPendingRequests(@PathVariable Long userId) {
        try {
            List<FriendRequest> pendingRequests = friendRequestService.getPendingRequests(userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", pendingRequests
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<Map<String, Object>> acceptRequest(@PathVariable Long requestId) {
        try {
            FriendRequest acceptedRequest = friendRequestService.acceptRequest(requestId);
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

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getFriendRequestStatus(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        try {
            Optional<FriendRequest.Status> status = friendRequestService.getRequestStatus(senderId, receiverId);
            if (status.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
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
