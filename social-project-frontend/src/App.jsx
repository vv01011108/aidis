import React from 'react'; 
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import MainPage from './pages/MainPage';
import EditProfilePage from './pages/EditProfilePage';
import UserProfilePage from './pages/UserProfilePage'; 
import FollowListPage from './pages/FollowListPage'; 
import RankingPage from './pages/RankingPage'; // 랭킹 페이지 추가

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/edit-profile/:userId" element={<EditProfilePage />} />
        <Route path="/user/:userId" element={<UserProfilePage />} />
        <Route path="/FollowListPage/:userId" element={<FollowListPage />} />
        <Route path="/ranking/:userId" element={<RankingPage />} /> {/* 랭킹 페이지 추가 */}
      </Routes>
    </Router>
  );
};

export default App;
