package com.example.social.repository;

import com.example.social.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankingRepository extends JpaRepository<Friend, Long> {

    // 친구 상태가 ACCEPTED인 관계만 가져오기
    List<Friend> findByStatusAndSenderId(Friend.Status status, Long senderId);
    List<Friend> findByStatusAndReceiverId(Friend.Status status, Long receiverId);
}
