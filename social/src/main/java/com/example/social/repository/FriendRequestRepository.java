package com.example.social.repository;

import com.example.social.entity.Friend;
import com.example.social.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findBySenderAndReceiver(User sender, User receiver);

    List<Friend> findByReceiverAndStatus(User receiver, Friend.Status status); // 받은 요청 조회

    List<Friend> findBySenderAndStatus(User sender, Friend.Status status); // 보낸 요청 조회

    List<Friend> findByReceiverOrSenderAndStatus(User receiver, User sender, Friend.Status status); // 양방향 친구 조회
}
