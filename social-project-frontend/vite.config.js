import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '127.0.0.1', // 로컬에서 호스팅할 IP 주소
    port: 3000, // 로컬 서버 포트
    proxy: {
      '/api': {
        //target: 'https://3d78-211-216-41-180.ngrok-free.app', // ngrok URL
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
