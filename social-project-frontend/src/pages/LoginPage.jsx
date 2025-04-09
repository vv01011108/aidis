import React, { useState } from 'react';
import '../pages/LoginPage.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // 페이지 이동을 위한 useNavigate 훅

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate(); // 페이지 이동을 위한 useNavigate 훅

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const response = await axios.post(
        `${import.meta.env.VITE_API_URL}/api/users/login`, 
        {
          email,
          password,
        },
        {
          withCredentials: true,
        }
      );
  
      // // 서버가 반환한 Authorization 헤더에서 토큰 추출
      // const token = response.headers['authorization'].replace('Bearer ', '');
      
      // // 로컬 스토리지에 JWT 토큰 저장
      // localStorage.setItem('token', token);
  
      // 로그인 후 메인 페이지로 리디렉션
      console.log('로그인 성공여부:', response.data);
      navigate('/main');
    } catch (err) {
      setError('로그인 실패. 이메일 또는 비밀번호를 확인해주세요.');
    }
  };
  
  

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleSubmit}>
        <h2>로그인</h2>
  
        <input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
  
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
  
        {error && <div className="error-message">{error}</div>}
  
        <button type="submit">로그인</button>
  
        <a href="/signup" className="toggle-link">회원가입</a>
      </form>
    </div>
  );
  
};

export default LoginPage;
