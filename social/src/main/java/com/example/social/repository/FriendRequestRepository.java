package com.example.social.repository;

import com.example.social.entity.FriendRequest;
import com.example.social.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);

    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.Status status); // 받은 요청 조회

    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequest.Status status); // 보낸 요청 조회

    List<FriendRequest> findByReceiverOrSenderAndStatus(User receiver, User sender, FriendRequest.Status status); // 양방향 친구 조회
}
