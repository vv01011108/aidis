package com.example.social.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.social.entity.Follow;
import com.example.social.entity.User;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);
    List<Follow> findByFolloweeAndStatus(User followee, Follow.Status status);
    List<Follow> findByFollowerAndStatus(User follower, Follow.Status status);

    // 여러 개 상태 한 번에 조회하는 메서드
    List<Follow> findByFolloweeAndStatusIn(User followee, List<Follow.Status> statuses);

}

