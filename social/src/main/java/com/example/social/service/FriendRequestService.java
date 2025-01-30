package com.example.social.service;

import com.example.social.entity.FriendRequest;
import com.example.social.entity.User;
import com.example.social.repository.FriendRequestRepository;
import com.example.social.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public FriendRequestService(FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("본인에게 친구 요청을 보낼 수 없습니다.");
        }

        User sender = getUserById(senderId, "발신자를 찾을 수 없습니다.");
        User receiver = getUserById(receiverId, "수신자를 찾을 수 없습니다.");

        // 중복 요청 방지 (PENDING 상태 확인 포함)
        friendRequestRepository.findBySenderAndReceiver(sender, receiver)
                .ifPresent(request -> {
                    if (request.getStatus() == FriendRequest.Status.PENDING) {
                        throw new IllegalArgumentException("이미 대기 중인 친구 요청이 있습니다.");
                    }
                });

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequest.Status.PENDING);

        return friendRequestRepository.save(friendRequest);
    }

    public Optional<FriendRequest.Status> getRequestStatus(Long senderId, Long receiverId) {
        User sender = getUserById(senderId, "발신자를 찾을 수 없습니다.");
        User receiver = getUserById(receiverId, "수신자를 찾을 수 없습니다.");

        return friendRequestRepository.findBySenderAndReceiver(sender, receiver)
                .map(FriendRequest::getStatus);
    }

    public FriendRequest acceptRequest(Long requestId) {
        FriendRequest friendRequest = getFriendRequestById(requestId, "친구 요청을 찾을 수 없습니다.");

        if (friendRequest.getStatus() != FriendRequest.Status.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
        return friendRequestRepository.save(friendRequest);
    }

    public void deleteRequest(Long requestId) {
        FriendRequest friendRequest = getFriendRequestById(requestId, "친구 요청을 찾을 수 없습니다.");
        friendRequestRepository.delete(friendRequest);
    }

    public List<FriendRequest> getPendingRequests(Long userId) {
        User receiver = getUserById(userId, "사용자를 찾을 수 없습니다.");
        return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequest.Status.PENDING);
    }

    private User getUserById(Long userId, String errorMessage) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }

    private FriendRequest getFriendRequestById(Long requestId, String errorMessage) {
        return friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }
}
