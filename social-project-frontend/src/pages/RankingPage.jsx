import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import axios from 'axios';
import './RankingPage.css';  // 수정된 스타일시트 임포트

const RankingPage = () => {
    const { userId } = useParams();
    const [friends, setFriends] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [expandedUser, setExpandedUser] = useState(null);
    const [userDetails, setUserDetails] = useState(null);

    useEffect(() => {
        const fetchRanking = async () => {
            try {
                const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/ranking/${userId}`, {
                    withCredentials: true,
                });

                const sortedFriends = response.data.sort((a, b) => b.rankingScore - a.rankingScore);
                setFriends(sortedFriends);
        
            } catch (err) {
                setError('이웃 랭킹을 불러오는 중 문제가 발생했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchRanking();
    }, [userId]);

    const fetchUserDetails = async (friendId) => {
        try {
            const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/users/${friendId}`, {
                withCredentials: true,
            });
            setUserDetails(response.data);
        } catch (err) {
            console.error('상세 정보를 불러오는 중 오류 발생:', err);
        }
    };

    const toggleDetails = (friendId) => {
        setExpandedUser(expandedUser === friendId ? null : friendId);
        if (expandedUser !== friendId) {
            fetchUserDetails(friendId);
        }
    };

    if (loading) return <p>로딩 중...</p>;
    if (error) return <p className="error">{error}</p>;

    return (
        <div className="ranking-container">
            <h2 className="ranking-title">이웃 랭킹</h2>
            <ul className="neighbor-list">
                {friends.map((friend, index) => (
                    <li key={friend.userId} className="neighbor-item">
                        <div className="profile" onClick={() => toggleDetails(friend.userId)}>
                            <img src={friend.profilePhoto} alt={`${friend.name}의 프로필`} className="profile-pic" />
                            <span>{index + 1}. {friend.name}</span>
                        </div>
                        {expandedUser === friend.userId && (
                            <div className="expanded-info">
                                <p><strong>이름:</strong> {friend.name}</p>
                                <p><strong>이메일:</strong> {friend.email}</p>
                                <p><strong>관심 분야:</strong> {friend.interest}</p>
                                <p><strong>직장/학교:</strong> {friend.workplace}</p>
                                <p><strong>전화번호:</strong> {friend.phoneNumber}</p>
                            </div>
                        )}
                    </li>
                ))}
            </ul>

            <Link to="/main" className="back-button">메인 페이지로 돌아가기</Link>
        </div>
    );
};

export default RankingPage;
