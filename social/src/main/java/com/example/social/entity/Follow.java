package com.example.social.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower; // 팔로워

    @ManyToOne
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee; // 팔로잉 대상

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // 팔로우 상태 (FOLLOWING, ACCEPTED)

    public enum Status {
        FOLLOWING, // 일방 팔로우
        ACCEPTED,   // 맞팔로우
        NONE;
    }
}

