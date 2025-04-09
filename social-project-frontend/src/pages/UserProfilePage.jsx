import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './UserProfilePage.css';
import { useParams } from 'react-router-dom';

const UserProfilePage = () => {
  const { userId } = useParams();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [followStatus, setFollowStatus] = useState(false); // 내가 상대를 팔로우 상태
  const [isFollowedByUser, setIsFollowedByUser] = useState(false); // 상대가 나를 팔로우 상태
  const [senderId, setSenderId] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);
  const [isProcessing, setIsProcessing] = useState(false);

  // 초기 데이터 로드
  useEffect(() => {
    const fetchData = async () => {
      try {
        const [loggedInResponse, userProfileResponse] = await Promise.all([
          axios.get(`${import.meta.env.VITE_API_URL}/api/users/me`, { withCredentials: true }),
          axios.get(`${import.meta.env.VITE_API_URL}/api/users/${userId}`, { withCredentials: true })
        ]);

        setSenderId(loggedInResponse.data.id);
        setUser(userProfileResponse.data);

        // 팔로우 상태 확인
        const followResponse = await axios.get(`${import.meta.env.VITE_API_URL}/api/follow/status`, {
          params: { followerId: loggedInResponse.data.id, followeeId: userId },
          withCredentials: true
        });

        setFollowStatus(followResponse.data.data.isFollowing); // 내가 상대를 팔로우 중인지 여부
        setIsFollowedByUser(followResponse.data.data.isFollowedByUser); // 상대가 나를 팔로우 중인지 여부
        
      } catch (err) {
        console.error('사용자 데이터를 가져오는 중 오류가 발생했습니다:', err);
        setErrorMessage('사용자 데이터를 가져오는 중 문제가 발생했습니다. 다시 시도해주세요.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [userId]);

  // 팔로우 상태를 다시 받아오는 함수
  const fetchFollowStatus = async () => {
    try {
      const followResponse = await axios.get(`${import.meta.env.VITE_API_URL}/api/follow/status`, {
        params: { followerId: senderId, followeeId: Number(userId) },
        withCredentials: true
      });

      setFollowStatus(followResponse.data.data.isFollowing); // 내가 상대를 팔로우 중인지 여부
      setIsFollowedByUser(followResponse.data.data.isFollowedByUser); // 상대가 나를 팔로우 중인지 여부
      
    } catch (err) {
      console.error('팔로우 상태를 가져오는 중 오류가 발생했습니다:', err.response);
    }
  };

  // 팔로우 처리
  const handleFollow = async () => {
    if (isProcessing) return;
    setIsProcessing(true);
    setErrorMessage(null);

    try {
      const response = await axios.post(
        `${import.meta.env.VITE_API_URL}/api/follow`,
        { followerId: senderId, followeeId: Number(userId) },
        { withCredentials: true }
      );

      if (response.data.status === 'success') {
        // 팔로우 상태 갱신 후 다시 상태를 가져오기
        await fetchFollowStatus();
      }

    } catch (err) {
      console.error('팔로우 중 오류가 발생했습니다:', err);
      setErrorMessage('팔로우 중 문제가 발생했습니다. 다시 시도해주세요.');
    } finally {
      setIsProcessing(false);
    }
  };

  // 언팔로우 처리
  const handleUnfollow = async (targetUserId) => {
    if (isProcessing) return;
    setIsProcessing(true);
    setErrorMessage(null);

    try {
      await axios.delete(
        `${import.meta.env.VITE_API_URL}/api/follow`,
        {
          params: { userId: senderId, targetUserId: userId },
          withCredentials: true
        }
      );
      console.log("타켓 아이디", targetUserId);
      console.log("현재 사용자 아이디", senderId);
      

      // 언팔로우 상태 갱신 후 다시 상태를 가져오기
      await fetchFollowStatus();
    } catch (err) {
      console.error('언팔로우 중 오류가 발생했습니다:', err);

      setErrorMessage('언팔로우 중 문제가 발생했습니다. 다시 시도해주세요.');
    } finally {
      setIsProcessing(false);
    }
  };

  // 맞팔로우 처리
  const handleMutualFollow = async () => {
    if (isProcessing) return;
    setIsProcessing(true);
    setErrorMessage(null);

    try {
      const response = await axios.post(
        `${import.meta.env.VITE_API_URL}/api/follow/mutual`,
        { followerId: userId, followeeId: senderId },
        { withCredentials: true }
      );

      if (response.data.status === 'success') {
        // 맞팔로우 후 상태 갱신
        await fetchFollowStatus();
      }
    } catch (err) {
      console.error('맞팔로우 중 오류가 발생했습니다:', err);
      setErrorMessage('맞팔로우 중 문제가 발생했습니다. 다시 시도해주세요.');
    } finally {
      setIsProcessing(false);
    }
  };

  if (loading) {
    return <p>로딩 중...</p>;
  }

  if (!user) {
    return <p>사용자를 찾을 수 없습니다.</p>;
  }

  return (
    <div className="user-profile-container">
      <h2>{user.name}님의 프로필</h2>
      <img src={user.photo} alt={`${user.name}의 프로필 사진`} className="profile-picture" />
      <p><strong>이름:</strong> {user.name}</p>
      <p><strong>이메일:</strong> {user.email}</p>
      <p><strong>관심 분야:</strong> {user.interest}</p>
      <p><strong>직장/학교:</strong> {user.workplace}</p>
      <p><strong>전화번호:</strong> {user.phoneNumber}</p>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {/* 팔로우/언팔로우/맞팔로우 버튼 */}
      {followStatus && isFollowedByUser ? (
        <button className="mutual-follow-button" disabled>
          서로 팔로우 중입니다
        </button>
      ) : isFollowedByUser ? (
        <button onClick={handleMutualFollow} className="follow-button" disabled={isProcessing}>
          맞팔로우 하기
        </button>
      ) : followStatus ? (
        <button onClick={() => handleUnfollow(userId)} className="unfollow-button" disabled={isProcessing}>
          언팔로우
        </button>
      ) : (
        <button onClick={handleFollow} className="follow-button" disabled={isProcessing}>
          팔로우
        </button>
      )}

    </div>
  );
};

export default UserProfilePage;
