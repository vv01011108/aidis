package com.example.social.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestDTO {
    private Long senderId;
    private Long receiverId;

    // 기본 생성자
    public FriendRequestDTO() {}

    // 게터와 세터
    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}
