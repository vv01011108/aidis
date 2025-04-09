import React, { useState, useEffect } from 'react';
import './EditProfilePage.css';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

const EditProfilePage = () => {
  const { userId } = useParams();
  const [user, setUser] = useState(null);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [interest, setInterest] = useState('');
  const [workplace, setWorkplace] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [photo, setPhoto] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/users/${userId}`, {
          withCredentials: true,
        });
        const userData = response.data;
        setUser(userData);
        setName(userData.name);
        setEmail(userData.email);
        setInterest(userData.interest);
        setWorkplace(userData.workplace);
        setPhoneNumber(userData.phoneNumber);
        setPhoto(userData.photo);
      } catch (err) {
        console.error('사용자 정보 가져오기 실패:', err);
        navigate('/login');
      }
    };

    if (userId) {
      fetchUserInfo();
    }
  }, [userId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');

    const formData = new FormData();
    formData.append('name', name);
    formData.append('email', email);
    formData.append('interest', interest);
    formData.append('workplace', workplace);
    formData.append('phoneNumber', phoneNumber);
    if (photo) formData.append('photo', photo);

    try {
      await axios.put(`${import.meta.env.VITE_API_URL}/api/users/${userId}/edit`, formData, {
        withCredentials: true,
        // headers: { 'Content-Type': 'multipart/form-data' }
      });
      alert('프로필이 성공적으로 업데이트되었습니다.');
      const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/users/${userId}`);
      const userData = response.data;
      setUser(userData);
      navigate('/main', { state: { updateUser: userData } });
    } catch (error) {
      console.error('프로필 업데이트 실패:', error);
      setErrorMessage('프로필 업데이트에 실패했습니다. 다시 시도해주세요.');
    }
  };

  const handleInterestSelect = (field) => {
    setInterest(field);
  };

  return (
    <div className="edit-profile-container">
      <h2>프로필 수정</h2>

      {user && (
        <form onSubmit={handleSubmit}>
          <div>
            <label htmlFor="name">이름</label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="email">이메일</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
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


          <div>
            <label htmlFor="workplace">학교/직장</label>
            <input
              type="text"
              id="workplace"
              value={workplace}
              onChange={(e) => setWorkplace(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="phoneNumber">전화번호</label>
            <input
              type="text"
              id="phoneNumber"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="photo">사진</label>
            <input
              type="file"
              id="photo"
              onChange={(e) => setPhoto(e.target.files[0])}
            />
          </div>

          <button type="submit">수정 완료</button>
        </form>
      )}
    </div>
  );
};

export default EditProfilePage;
