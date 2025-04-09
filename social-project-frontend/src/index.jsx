import React from 'react';
import ReactDOM from 'react-dom/client'; // React 18 사용 시
import './index.css';
import App from './App';

// 'root'라는 id를 가진 DOM 요소를 찾아서 앱을 렌더링합니다
const rootElement = document.getElementById('root');
const root = ReactDOM.createRoot(rootElement);

// 앱 렌더링
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
