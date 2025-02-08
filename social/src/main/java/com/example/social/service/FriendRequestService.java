package com.example.social.service;

import com.example.social.dto.FriendRequestDTO;
import com.example.social.entity.Friend;
import com.example.social.entity.User;
import com.example.social.repository.FriendRequestRepository;
import com.example.social.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
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

    // FriendRequestDTO를 사용하여 친구 요청을 보냄
    @Transactional
    public Friend sendFriendRequest(Long senderId, FriendRequestDTO friendRequestDTO) {
        Long receiverId = friendRequestDTO.getReceiverId();

        if (senderId == null) {
            throw new IllegalArgumentException("로그인 상태가 아닙니다.");
        }

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("본인에게 친구 요청을 보낼 수 없습니다.");
        }

        User sender = getUserById(senderId, "발신자를 찾을 수 없습니다.");
        User receiver = getUserById(receiverId, "수신자를 찾을 수 없습니다.");

        // 중복 요청 방지 (PENDING 상태 확인 포함)
        friendRequestRepository.findBySenderAndReceiver(sender, receiver)
                .ifPresent(request -> {
                    if (request.getStatus() == Friend.Status.PENDING) {
                        throw new IllegalArgumentException("이미 대기 중인 친구 요청이 있습니다.");
                    }
                });

        Friend friendRequest = new Friend();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(Friend.Status.PENDING);

        return friendRequestRepository.save(friendRequest);
    }

    public Optional<Friend.Status> getRequestStatus(Long senderId, Long receiverId) {
        // 발신자와 수신자를 찾음
        User sender = getUserById(senderId, "발신자를 찾을 수 없습니다.");
        User receiver = getUserById(receiverId, "수신자를 찾을 수 없습니다.");

        // sender -> receiver 방향을 먼저 확인
        Optional<Friend> requestFromSenderToReceiver = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (requestFromSenderToReceiver.isPresent()) {
            return Optional.of(requestFromSenderToReceiver.get().getStatus());
        }

        // receiver -> sender 방향을 확인
        Optional<Friend> requestFromReceiverToSender = friendRequestRepository.findBySenderAndReceiver(receiver, sender);
        return requestFromReceiverToSender.map(Friend::getStatus);
    }


    public Friend acceptRequest(Long requestId) {
        Friend friendRequest = getFriendRequestById(requestId, "친구 요청을 찾을 수 없습니다.");

        if (friendRequest.getStatus() != Friend.Status.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        friendRequest.setStatus(Friend.Status.ACCEPTED);
        return friendRequestRepository.save(friendRequest);
    }

    public void deleteRequest(Long requestId) {
        Friend friendRequest = getFriendRequestById(requestId, "친구 요청을 찾을 수 없습니다.");
        friendRequestRepository.delete(friendRequest);
    }

    public List<Friend> getPendingRequests(Long userId) {
        User receiver = getUserById(userId, "사용자를 찾을 수 없습니다.");
        return friendRequestRepository.findByReceiverAndStatus(receiver, Friend.Status.PENDING);
    }

    private User getUserById(Long userId, String errorMessage) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }

    private Friend getFriendRequestById(Long requestId, String errorMessage) {
        return friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }
}
