package com.example.social.controller;

import com.example.social.dto.RankingDTO;
import com.example.social.service.RankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    private RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    // 이웃 랭킹 가져오기
    @GetMapping("/{userId}")
    public List<RankingDTO> getRanking(@PathVariable Long userId) {
        return rankingService.getRanking(userId);
    }
}
