import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './SignupPage.css';

const SignupPage = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [photo, setPhoto] = useState(null);
  const [interest, setInterest] = useState('');
  const [workplace, setWorkplace] = useState('');
  const [phone, setPhone] = useState('');
  const [message, setMessage] = useState(''); // 응답 메시지 표시용
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('name', name);
    formData.append('email', email);
    formData.append('password', password);
    formData.append('photo', photo);
    formData.append('interest', interest);
    formData.append('workplace', workplace);
    formData.append('phoneNumber', phone);

    try {
      const response = await axios.post(
        `${import.meta.env.VITE_API_URL}/api/users/signup`,
        formData,
        {
          withCredentials: true,
          // headers: {
          //   'Content-Type': 'multipart/form-data',
          // },
        }
      );

      setMessage(response.data);
    } catch (error) {
      if (error.response) {
        setMessage(`회원가입 실패: ${error.response.data}`);
      } else {
        setMessage('회원가입 중 오류가 발생했습니다.');
      }
    }
  };

  const goToLoginPage = () => {
    navigate('/login');
  };

  const handleInterestSelect = (selectedInterest) => {
    setInterest(selectedInterest);
  };

  return (
    <div className="container">
      <form onSubmit={handleSubmit}>
        <h2>회원가입</h2>
        <p>{message}</p>

        <div className="form-group">
          <input
            id="name"
            type="text"
            placeholder="이름"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <input
            id="email"
            type="email"
            placeholder="이메일"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <input
            id="password"
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="photo" className="subtitle">프로필 사진</label>
          <input
            id="photo"
            type="file"
            accept="image/*"
            onChange={(e) => setPhoto(e.target.files[0])}
            required
          />
        </div>

        <div className="form-group">
          <label className="subtitle">주요 관심 분야</label>
          <div className="interest-container">
            {[
              'Backend',
              'Frontend',
              'UI/UX',
              'Mobile',
              'Data Science',
              'AI',
              'DevOps',
              'Cloud Computing',
            ].map((field) => (
              <div
                key={field}
                className={`interest-item ${interest === field ? 'selected' : ''}`}
                onClick={() => handleInterestSelect(field)}
              >
                {field}
              </div>
            ))}
          </div>
        </div>

        <div className="form-group">
          <input
            id="workplace"
            type="text"
            placeholder="직장 (학생일 경우 학교)"
            value={workplace}
            onChange={(e) => setWorkplace(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <input
            id="phone"
            type="tel"
            placeholder="휴대폰 번호"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            required
          />
        </div>

        <button type="submit">확인</button>

        <div className="form-buttons">
          {/* 로그인 페이지로 가기 링크 */}
          <a className="toggle-link" onClick={goToLoginPage}>로그인 페이지로 가기</a>
        </div>
      </form>
    </div>
  );
};

export default SignupPage;
