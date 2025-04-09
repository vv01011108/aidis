package com.example.social.dto;

import com.example.social.entity.Follow;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowDTO {
    private Long followerId;  // 팔로워 ID
    private Long followeeId;  // 팔로잉 대상 ID
    private Follow.Status status;    // 팔로우 상태 (FOLLOWING, ACCEPTED)

    // 기본 생성자
    public FollowDTO() {}

    // 생성자 (팔로워와 팔로우 대상, 상태를 받을 수 있게)
    public FollowDTO(Long followerId, Long followeeId, Follow.Status status) {
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.status = status;
    }
}
