import React, { useState, useEffect, useRef } from 'react';
import './MainPage.css';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';

const MainPage = () => {
  const [user, setUser] = useState(null);
  const [showProfile, setShowProfile] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);  // 초기값을 빈 배열로 설정
  const [noResults, setNoResults] = useState(false);
  const searchResultsRef = useRef(null);
  const navigate = useNavigate();
  const location = useLocation();

  // 사용자 정보 가져오기
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        if (location.state && location.state.updateUser) {
          setUser(location.state.updateUser);
        } else {
          const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/users/me`, {
            withCredentials: true,
          });
          
          setUser(response.data);
        }
      } catch (err) {
        console.error('사용자 정보 가져오기 실패:', err);
        navigate('/login');
      }
    };

    fetchUserInfo();
  }, [navigate, location.state]);

  // 클릭 외부에서 검색창 닫기
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (searchResultsRef.current && !searchResultsRef.current.contains(e.target) && !e.target.closest('.search-input')) {
        setSearchQuery('');
        setSearchResults([]);  // 검색 결과를 빈 배열로 설정
        setNoResults(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // 검색 처리
  const handleSearch = async (e) => {
    e.preventDefault();
    if (searchQuery.trim() === '') {
      setSearchResults([]); // 검색어가 없으면 결과를 빈 배열로 설정
      setNoResults(false);
      return;
    }

    try {
      const response = await axios.get(
        `${import.meta.env.VITE_API_URL}/api/users/search?name=${searchQuery}`,
        {
          withCredentials: true,
        }
      );

      if (Array.isArray(response.data)) {
        if (response.data.length === 0) {
          setSearchResults([]); // 검색 결과가 없으면 빈 배열로 설정
          setNoResults(true);
        } else {
          setSearchResults(response.data); // 검색 결과를 그대로 설정
          setNoResults(false); // 결과가 있으면 noResults를 false로 설정
        }
      } else {
        setSearchResults([]); // 배열이 아니면 빈 배열로 설정
        setNoResults(true);
      }
    } catch (err) {
      console.error('사용자 검색 실패:', err);
      setSearchResults([]); // 에러 발생 시 빈 배열로 설정
      setNoResults(true);
    }
  };

  // 프로필 수정 클릭
  const handleEditClick = () => {
    navigate(`/edit-profile/${user.id}`);
  };

  // 다른 사용자 프로필 클릭
  const handleUserClick = (userId) => {
    navigate(`/user/${userId}`);
  };

  // 친구 요청 보기
  const goToFollowListPage = () => {
    if (user) {
      navigate(`/FollowListPage/${user.id}`);
    }
  };

  // 랭킹 보기 페이지로 이동
  const goToRankingPage = () => {
    navigate(`/ranking/${user.id}`);
  };

  // 로그아웃 처리
  const handleLogout = async () => {
    try {
      await axios.post(
        `${import.meta.env.VITE_API_URL}/api/users/logout`,
        {},
        {
          withCredentials: true,
        }
      );
      navigate('/login');
    } catch (err) {
      console.error('로그아웃 실패:', err);
    }
  };

  return (
    <div className="main-container">
      <h2 className="greeting">안녕하세요, {user ? user.name : '사용자'}님!</h2>

      {/* 검색창 */}
      <div className="search-container">
        <input
          type="text"
          placeholder="이름으로 검색"
          value={searchQuery}
          onChange={(e) => {
            setSearchQuery(e.target.value);
          }}
          className="search-input"
        />
        <button className="search-button" onClick={handleSearch}>검색</button>
      </div>

      {/* 검색 결과 */}
      {Array.isArray(searchResults) && searchResults.length > 0 && (
        <div className="search-results" ref={searchResultsRef}>
          <h3>검색 결과</h3>
          <ul>
            {searchResults.map((result) => (
              <li key={result.id} className="search-result-item" onClick={() => handleUserClick(result.id)}>
                <div className="search-result">
                  <img
                    src={result.photo}
                    alt={`${result.name}의 프로필`}
                    className="search-result-picture"
                  />
                  <p className="search-result-name">{result.name}</p>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {noResults && searchQuery.trim() !== '' && (
        <div className="no-results-message">
          <p>일치하는 사용자가 없습니다.</p>
        </div>
      )}

      {user && (
        <div className="profile-container">
          <div className="profile-header">
            <img
              src={user.photo}
              alt={`${user.name}의 프로필 사진`}
              className="profile-picture"
              onClick={() => setShowProfile(!showProfile)}
            />
            <p className="profile-name">{user.name}</p>
          </div>

          {showProfile && (
            <div className="profile-details">
              <p><strong>이름:</strong> {user.name}</p>
              <p><strong>이메일:</strong> {user.email}</p>
              <p><strong>관심사:</strong> {user.interest}</p>
              <p><strong>직장/학교:</strong> {user.workplace}</p>
              <p><strong>전화번호:</strong> {user.phoneNumber}</p>
            </div>
          )}

          <button className="edit-profile-button" onClick={handleEditClick}>프로필 수정</button>
          <button className="friend-requests-button" onClick={goToFollowListPage}>나를 팔로우한 사람 목록</button>
          <button className="ranking-button" onClick={goToRankingPage}>이웃 랭킹 보기</button>
          <button className="logout-link" onClick={handleLogout}>로그아웃</button>
        </div>
      )}
    </div>
  );
};

export default MainPage;