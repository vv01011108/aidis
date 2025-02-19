package com.example.social.service;

import com.example.social.dto.FollowDTO;
import com.example.social.dto.UserDTO;
import com.example.social.entity.Follow;
import com.example.social.entity.User;
import com.example.social.repository.FollowRepository;
import com.example.social.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    // 팔로우 기능
    @Transactional
    public FollowDTO follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("자신을 팔로우할 수 없습니다.");
        }

        User follower = getUserById(followerId);
        User followee = getUserById(followeeId);

        // 이미 팔로우 중인 경우 - 이거 없어도 될 것 같은데
        followRepository.findByFollowerAndFollowee(follower, followee)
                .ifPresent(existingFollow -> {
                    if (existingFollow.getStatus() == Follow.Status.FOLLOWING) {
                        // 일방 팔로우 중이면 맞팔로우 상태로 변경
                        existingFollow.setStatus(Follow.Status.ACCEPTED);
                        followRepository.save(existingFollow); // 상태 변경
                    } else if (existingFollow.getStatus() == Follow.Status.ACCEPTED) {
                        throw new IllegalArgumentException("이미 맞팔로우 중입니다.");
                    }
                });

        // 새 팔로우 생성
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowee(followee);
        follow.setStatus(Follow.Status.FOLLOWING);

        follow = followRepository.save(follow);

        return new FollowDTO(follow.getFollower().getId(), follow.getFollowee().getId(), Follow.Status.FOLLOWING);
    }

    // 언팔로우 기능
    @Transactional
    public void unfollow(Long userId, Long targetUserId) {
        System.out.println("로그인 사용자 " + userId);

        // 로그인한 사용자와 타겟 사용자를 가져옴
        User currentUser = getUserById(userId);
        User targetUser = getUserById(targetUserId);

        // 팔로우 관계를 찾음 (양방향으로 확인)
        Follow follow = followRepository.findByFollowerAndFollowee(currentUser, targetUser)
                .orElseGet(() -> followRepository.findByFollowerAndFollowee(targetUser, currentUser)
                        .orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 존재하지 않습니다.")));

        // 현재 로그인한 사용자가 follower인지 followee인지를 확인
        boolean isCurrentUserFollower = follow.getFollower().getId().equals(userId);
        boolean isCurrentUserFollowee = follow.getFollowee().getId().equals(userId);

        // 팔로우 상태가 ACCEPTED인 경우에만 언팔로우 가능
        if (follow.getStatus() == Follow.Status.ACCEPTED) {
            if (isCurrentUserFollower) {
                // 상황 1: 로그인한 사용자가 follower인 경우 (내가 언팔로우하는 경우)
                System.out.println("User " + userId + " (follower) is unfollowing " + targetUserId);

                // 상태를 FOLLOWING으로 변경
                follow.setStatus(Follow.Status.FOLLOWING);

                // 상대의 follower와 followee를 바꿔줌
                follow.setFollower(targetUser);
                follow.setFollowee(currentUser);

                followRepository.save(follow);

            } else if (isCurrentUserFollowee) {
                // 상황 2: 로그인한 사용자가 followee인 경우 (나를 팔로우한 사람을 삭제하는 경우)
                System.out.println("User " + userId + " (followee) is removing follower " + targetUserId);

                // 상태를 FOLLOWING으로 변경
                follow.setStatus(Follow.Status.FOLLOWING);

                followRepository.save(follow);
            }
        } else {
            // 팔로우 상태가 ACCEPTED가 아닌 경우에는 단순히 팔로우 관계를 삭제
            System.out.println("User " + userId + " (unilateral) is unfollowing " + targetUserId);

            // 팔로우 관계를 삭제
            followRepository.delete(follow);
        }
    }


    // 맞팔로우 기능
    @Transactional
    public FollowDTO mutualFollow(Long followerId, Long followeeId) {

        // 사용자 존재 확인
        Optional<User> follower = userRepository.findById(followerId);
        Optional<User> followee = userRepository.findById(followeeId);

        if (follower.isPresent() && followee.isPresent()) {

            // 이미 팔로우 중인지 확인
            Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowee(follower.get(), followee.get());
            if (existingFollow.isPresent()) {
                Follow follow = existingFollow.get();

                // 상대가 나를 팔로우 중이라면 상태 변경
                if (follow.getStatus() == Follow.Status.FOLLOWING) {
                    follow.setStatus(Follow.Status.ACCEPTED);
                    followRepository.save(follow);

                    return new FollowDTO(follow.getFollower().getId(), follow.getFollowee().getId(), Follow.Status.ACCEPTED);
                } else if (follow.getStatus() == Follow.Status.ACCEPTED) {
                    throw new IllegalArgumentException("이미 맞팔로우 중입니다.");
                }
            } else {
                System.out.println("Follower: " + follower);
                System.out.println("Followee: " + followee);

                throw new IllegalArgumentException("팔로우 관계가 존재하지 않습니다.");
            }
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        return new FollowDTO(follower.get().getId(), followee.get().getId(), Follow.Status.NONE);
    }


    // 검색 기능에서 팔로우 여부 확인
    public List<User> getFollowers(Long userId) {
        User user = getUserById(userId);

        List<User> followers = followRepository.findByFolloweeAndStatusIn(user,
                        Arrays.asList(Follow.Status.FOLLOWING, Follow.Status.ACCEPTED))
                .stream()
                .map(Follow::getFollower)
                .toList();

        // ACCEPTED 상태일 경우 반대 방향도 포함
        List<User> acceptedFollowers = followRepository.findByFollowerAndStatus(user, Follow.Status.ACCEPTED)
                .stream()
                .map(Follow::getFollowee)
                .toList();

        return Stream.concat(followers.stream(), acceptedFollowers.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // 나를 팔로우하는 사용자 조회 목록
    public Map<String, List<UserDTO>> getFollowersWithStatus(Long userId) {
        User user = getUserById(userId);
        List<Follow.Status> statuses = Arrays.asList(Follow.Status.FOLLOWING, Follow.Status.ACCEPTED);

        // ✅ 나를 followee로 하는 관계 (즉, 나를 팔로우한 사람들)
        List<Follow> followeeList = followRepository.findByFolloweeAndStatusIn(user, statuses);

        // ✅ 나를 follower로 하는 관계 (즉, 내가 팔로우한 사람들)
        List<Follow> followerList = followRepository.findByFollowerAndStatus(user, Follow.Status.ACCEPTED);

        // FOLLOWING과 ACCEPTED 상태로 분리
        List<UserDTO> followingList = new ArrayList<>();
        List<UserDTO> acceptedList = new ArrayList<>();

        // 내가 FOLLOWEE인 경우 (즉, 나를 팔로우한 사람들)
        for (Follow follow : followeeList) {
            User follower = follow.getFollower();

            if (follow.getStatus() == Follow.Status.FOLLOWING) {
                followingList.add(mapToUserDTO(follower));
            }

            if (follow.getStatus() == Follow.Status.ACCEPTED) {
                acceptedList.add(mapToUserDTO(follower));
            }
        }

        // 내가 FOLLOWER인 경우 (즉, 내가 팔로우한 사람들)
        for (Follow follow : followerList) {
            User followee = follow.getFollowee(); // 내가 팔로우한 사람

            if (follow.getStatus() == Follow.Status.ACCEPTED) {
                acceptedList.add(mapToUserDTO(followee));
            }
        }

        // 최종 결과
        Map<String, List<UserDTO>> result = new HashMap<>();
        result.put("FOLLOWING", followingList);
        result.put("ACCEPTED", acceptedList);

        System.out.println(result);
        return result;
    }


    // User -> UserDTO 변환
    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    // 상태 확인
    public Map<String, Boolean> getFollowStatus(Long followerId, Long followeeId) {
        Optional<User> follower = userRepository.findById(followerId);
        Optional<User> followee = userRepository.findById(followeeId);

        if (follower.isPresent() && followee.isPresent()) {
            // 팔로우 상태 조회
            Optional<Follow> follow = followRepository.findByFollowerAndFollowee(follower.get(), followee.get());
            Optional<Follow> reverseFollow = followRepository.findByFollowerAndFollowee(followee.get(), follower.get());

            // 양방향 팔로우인 경우 (ACCEPTED 상태)
            if (follow.isPresent() || reverseFollow.isPresent()) {
                // 양방향 팔로우인 경우 (ACCEPTED 상태)
                if ((follow.isPresent() && follow.get().getStatus() == Follow.Status.ACCEPTED) ||
                        (reverseFollow.isPresent() && reverseFollow.get().getStatus() == Follow.Status.ACCEPTED)) {
                    System.out.println("상태: ACCEPTED (맞팔로우)");
                    return Map.of(
                            "isFollowing", true,
                            "isFollowedByUser", true
                    );
                }
            }

            // 내가 팔로우한 상태인 경우 (FOLLOWING)
            if (follow.isPresent() && follow.get().getStatus() == Follow.Status.FOLLOWING) {
                System.out.println("상태: FOLLOWING (내가 팔로우)");
                return Map.of(
                        "isFollowing", true,
                        "isFollowedByUser", false
                );
            }

            // 상대가 팔로우한 상태인 경우 (FOLLOWING)
            if (reverseFollow.isPresent() && reverseFollow.get().getStatus() == Follow.Status.FOLLOWING) {
                System.out.println("상태: FOLLOWING (상대가 팔로우)");
                return Map.of(
                        "isFollowing", false,
                        "isFollowedByUser", true
                );
            }

            // 팔로우 상태가 없으면
            System.out.println("상태: 서로 팔로우하지 않음");
            return Map.of(
                    "isFollowing", false,
                    "isFollowedByUser", false
            );
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
    }



    // 내가 팔로우하는 사람 확인 기능 - 어디서 쓰는지 확인해야 됨
    public List<User> getFollowees(Long userId) {
        User user = getUserById(userId);
        return followRepository.findByFollowerAndStatus(user, Follow.Status.FOLLOWING)
                .stream()
                .map(Follow::getFollowee)
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

}
