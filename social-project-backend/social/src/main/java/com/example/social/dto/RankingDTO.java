package com.example.social.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RankingDTO {

    private Long userId;
    private String name;
    private String profilePhoto;
    private Integer rankingScore;

    @Override
    public String toString() {
        return "RankingDTO{" +
                "userId=" + userId +
                ", rankingScore=" + rankingScore +
                '}';
    }

}
