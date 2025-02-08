package com.example.social.service;

import com.example.social.dto.RankingDTO;
import com.example.social.entity.Friend;
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

        // 친구 상태가 ACCEPTED인 친구 관계만 가져오기 (sender와 receiver 두 가지 기준으로 가져오기)
        List<Friend> acceptedFriendsFromSender = rankingRepository.findByStatusAndSenderId(Friend.Status.ACCEPTED, userId);
        List<Friend> acceptedFriendsFromReceiver = rankingRepository.findByStatusAndReceiverId(Friend.Status.ACCEPTED, userId);

        // 친구 관계를 바탕으로 랭킹 점수 계산 후 RankingDTO로 변환
        return acceptedFriendsFromSender.stream()
                .map(friend -> calculateRankingDTO(friend))
                .collect(Collectors.toList());
    }

    // 점수 계산 후 RankingDTO 반환
    private RankingDTO calculateRankingDTO(Friend friend) {
        // 점수 계산 로직 (친구 수 + 관심 분야 일치)
        int rankingScore = calculateRankingScore(friend);

        // 상대방이 sender일 때, receiver의 정보를 사용
        return new RankingDTO(
                friend.getReceiver().getId(),
                friend.getReceiver().getName(),
                friend.getReceiver().getPhoto(),
                rankingScore
        );
    }

    // 점수 계산 예시
    private int calculateRankingScore(Friend friend) {
        int score = 0;

        // 친구 관계가 ACCEPTED 상태일 경우 점수 부여
        if (friend.getStatus() == Friend.Status.ACCEPTED) {
            score += 5; // 예시 점수 부여
        }

        // 여기서 관심 분야 일치 점수 추가 등 다양한 로직을 구현할 수 있습니다.
        // 예시로 관심 분야 일치 점수 부여
        if (friend.getReceiver().getInterest().equals(friend.getSender().getInterest())) {
            score += 3; // 관심 분야가 같으면 3점 추가
        }

        return score;
    }
}
