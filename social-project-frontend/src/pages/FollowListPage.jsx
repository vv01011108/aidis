import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './FollowListPage.css';
import { useParams } from 'react-router-dom';

const FollowListPage = () => {
  const { userId } = useParams(); // URL 파라미터에서 userId를 받음
  const [followers, setFollowers] = useState({
    FOLLOWING: [], // 팔로우 중인 사용자
    ACCEPTED: [],  // 맞팔로우 중인 사용자
  });
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [isProcessing, setIsProcessing] = useState(false); // 처리 중 상태

  // 나를 팔로우한 사람 목록을 받아오는 useEffect
  useEffect(() => {
    if (userId) {
      const fetchFollowers = async () => {
        try {
          const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/follow/followers-list/${userId}`, {
            withCredentials: true,
          });

          if (response.data) {
            console.log(response.data);
            // 팔로우와 맞팔로우 상태로 구분하여 업데이트
            setFollowers({
              FOLLOWING: response.data.data.FOLLOWING || [],
              ACCEPTED: response.data.data.ACCEPTED || [],
            });
          } else {
            console.error('팔로워 데이터가 없습니다.');
          }
        } catch (err) {
          console.error('팔로워 목록 가져오기 실패:', err);
        } finally {
          setLoading(false);
        }
      };

      fetchFollowers();
    }
  }, [userId]);

  // 맞팔로우 처리 함수
  const handleMutualFollow = async (followerId) => {
    if (isProcessing) return;
    setIsProcessing(true);

    try {
      const response = await axios.post(
        `${import.meta.env.VITE_API_URL}/api/follow/mutual`,
        { followerId, followeeId: userId },
        { withCredentials: true }
      );

      if (response.data.status === 'success') {
        // 상태 업데이트
        setFollowers((prevFollowers) => {
          const updatedFollowers = { ...prevFollowers };
          updatedFollowers.FOLLOWING = updatedFollowers.FOLLOWING.filter(
            (follower) => follower.id !== followerId
          );
          updatedFollowers.ACCEPTED = [...updatedFollowers.ACCEPTED, { id: followerId, name: response.data.name }];
          
          console.log(updatedFollowers);
          return updatedFollowers;
        });
      }
    } catch (err) {
      console.error('맞팔로우 중 오류가 발생했습니다:', err);
    } finally {
      setIsProcessing(false);
    }
  };

  // 언팔로우 처리
  const handleUnfollow = async (targetUserId) => {
    console.log(targetUserId);
    if (isProcessing) return;
    setIsProcessing(true);

    try {
      await axios.delete(
        `${import.meta.env.VITE_API_URL}/api/follow`,
        {
          params: { userId, targetUserId  },
          withCredentials: true
        }
      );

      // 상태 업데이트
      setFollowers((prevFollowers) => {
        const updatedFollowers = { ...prevFollowers };

        // 로그인한 사용자가 follower일 때
        updatedFollowers.ACCEPTED = updatedFollowers.ACCEPTED.filter(
          (follower) => follower.id !== userId
        );

        // 로그인한 사용자가 followee일 때
        updatedFollowers.FOLLOWING = [...updatedFollowers.FOLLOWING, { id: userId }];
        return updatedFollowers;
      });

    } catch (err) {
      console.error('언팔로우 중 오류가 발생했습니다:', err.response || err);
    } finally {
      setIsProcessing(false);
    }
  };

  if (loading) {
    return <p className="loading">로딩 중...</p>;
  }

  if (!followers.FOLLOWING.length && !followers.ACCEPTED.length) {
    return <p className="no-followers">팔로워가 없습니다.</p>;
  }

  return (
    <div className="followers-list">
      <h3>팔로워 목록</h3>

      {/* FOLLOWING 상태의 팔로워들 */}
      {followers.FOLLOWING.length > 0 && (
        <div className="following-users">
          <h4>팔로우 중인 사용자</h4>
          {followers.FOLLOWING.map((follower) => (
            <div key={follower.id} className="follower-item">
              <p>{follower.name}</p>
              <button onClick={() => handleMutualFollow(follower.id)} disabled={isProcessing}>
                맞팔로우 하기
              </button>
            </div>
          ))}
        </div>
      )}

      {/* ACCEPTED 상태의 팔로워들 */}
      {followers.ACCEPTED.length > 0 && (
        <div className="accepted-users">
          <h4>맞팔로우 중인 사용자</h4>
          {followers.ACCEPTED.map((follower) => (
            <div key={follower.id} className="follower-item">
              <p>{follower.name}</p>
              <button onClick={() => handleUnfollow(follower.id)} disabled={isProcessing}>
                언팔로우 하기
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default FollowListPage;
