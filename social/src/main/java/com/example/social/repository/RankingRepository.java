package com.example.social.repository;

import com.example.social.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankingRepository extends JpaRepository<Follow, Long> {

    // 팔로우 상태가 ACCEPTED인 팔로우 관계만 가져오기 (팔로워 기준)
    List<Follow> findByStatusAndFollowerId(Follow.Status status, Long followerId);

    // 팔로우 상태가 ACCEPTED인 팔로우 관계만 가져오기 (팔로잉 대상 기준)
    List<Follow> findByStatusAndFolloweeId(Follow.Status status, Long followeeId);

    // 상호 팔로우 상태를 가지는 팔로우 관계만 가져오기
    List<Follow> findByFollowerIdAndFolloweeIdAndStatus(Long followerId, Long followeeId, Follow.Status status);
}
