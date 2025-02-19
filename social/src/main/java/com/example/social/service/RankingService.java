package com.example.social.service;

import com.example.social.dto.RankingDTO;
import com.example.social.entity.Follow;
import com.example.social.repository.RankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final RankingRepository rankingRepository;

    public RankingService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    public List<RankingDTO> getRanking(Long userId) {

        // 팔로우 상태가 ACCEPTED 또는 FOLLOWING인 팔로우 관계만 가져오기
        List<Follow> acceptedFollowsFromSender = rankingRepository.findByStatusAndFollowerId(Follow.Status.ACCEPTED, userId);
        List<Follow> acceptedFollowsFromReceiver = rankingRepository.findByStatusAndFolloweeId(Follow.Status.ACCEPTED, userId);
        List<Follow> followingFollows = rankingRepository.findByStatusAndFollowerId(Follow.Status.FOLLOWING, userId);

        // 랭킹 리스트 생성
        List<RankingDTO> rankings = acceptedFollowsFromSender.stream()
                .map(follow -> calculateRankingDTO(follow, true))  // 내가 팔로우한 사람
                .filter(rankingDTO -> !rankingDTO.getUserId().equals(userId))  // 본인 제외
                .collect(Collectors.toList());

        rankings.addAll(acceptedFollowsFromReceiver.stream()
                .map(follow -> calculateRankingDTO(follow, true))  // 나를 팔로우한 사람
                .filter(rankingDTO -> !rankingDTO.getUserId().equals(userId))  // 본인 제외
                .toList());

        rankings.addAll(followingFollows.stream()
                .map(follow -> calculateRankingDTO(follow, false))  // 일방 팔로우
                .filter(rankingDTO -> !rankingDTO.getUserId().equals(userId))  // 본인 제외
                .toList());

        return rankings;
    }


    // 점수 계산 후 RankingDTO 반환
    private RankingDTO calculateRankingDTO(Follow follow, boolean isMutualFollow) {
        // 점수 계산 로직 (일방 팔로우: +5, 서로 팔로우: +10, 관심 분야 같으면 +3)
        int rankingScore = calculateRankingScore(follow, isMutualFollow);

        // 상대방이 팔로우 대상일 때, 팔로워의 정보를 사용
        return new RankingDTO(
                follow.getFollowee().getId(),
                follow.getFollowee().getName(),
                follow.getFollowee().getPhoto(),
                rankingScore
        );
    }

    // 점수 계산 예시
    private int calculateRankingScore(Follow follow, boolean isMutualFollow) {
        int score = 0;

        // 상호 팔로우일 경우 점수 부여 (+10)
        if (follow.getStatus() == Follow.Status.ACCEPTED) {
            score += 10;
        }

        // 일방 팔로우일 경우 점수 부여 (+5)
        if (follow.getStatus() == Follow.Status.FOLLOWING && !isMutualFollow) {
            score += 5;
        }

        // 관심 분야 일치 점수 추가
        if (follow.getFollowee().getInterest().equals(follow.getFollower().getInterest())) {
            score += 3; // 관심 분야가 같으면 3점 추가
        }

        return score;
    }
}
