# 빌드를 위한 Node.js 이미지 사용
FROM node:20 AS build

WORKDIR /app

# package.json과 package-lock.json을 복사
COPY package.json package-lock.json ./

# 의존성 설치
RUN npm install

# Vite 빌드 실행
COPY . .
RUN npm run build

# Nginx를 사용하여 React 앱 배포
FROM nginx:alpine

# 빌드된 파일을 Nginx에 복사
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx가 80 포트에서 실행되도록 설정
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
