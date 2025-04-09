function showSnackbar(message) {
    const snackbar = document.getElementById('snackbar');
    snackbar.textContent = message;
    snackbar.className = "show";
    setTimeout(() => {
        snackbar.className = snackbar.className.replace("show", "");
    }, 3000); // 3초 후에 사라지도록 설정
}

// 로그인 처리
document.getElementById('loginFormInner').addEventListener('submit', function(event) {
    event.preventDefault();
    const email = document.getElementById('loginUseremail').value;
    const password = document.getElementById('loginPassword').value;

    fetch('/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => { throw new Error(data.message || '로그인 실패'); });
            }
            return response.json();
        })
        .then(data => {
            localStorage.setItem('username', JSON.stringify({ name: data.name })); // 사용자 정보를 로컬 스토리지에 저장
            showSnackbar('로그인 성공!'); // 성공 메시지 표시
            window.location.href = '/book_management.html'; // 도서 관리 페이지 URL로 이동
        })
        .catch(error => {
            showSnackbar(error.message); // 에러 메시지 표시
        });
});

// 회원가입 처리
document.getElementById('registerFormInner').addEventListener('submit', function(event) {
    event.preventDefault();
    const email = document.getElementById('registerUseremail').value;
    const password = document.getElementById('registerPassword').value;
    const name = document.getElementById('registerUserName').value;

    fetch('/api/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password, name })
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => { throw new Error(data.message || '회원가입 실패'); });
            }
            return response.json();
        })
        .then(data => {
            showSnackbar('회원가입 성공! 로그인하세요.'); // 성공 메시지 표시
            document.getElementById('showLogin').click(); // 로그인 화면으로 이동
        })
        .catch(error => {
            showSnackbar(error.message); // 에러 메시지 표시
        });
});

// 로그인/회원가입 토글
document.getElementById('showLogin').addEventListener('click', function() {
    document.getElementById('loginForm').classList.add('active');
    document.getElementById('registerForm').classList.remove('active');
    this.classList.add('active');
    document.getElementById('showRegister').classList.remove('active');
});

document.getElementById('showRegister').addEventListener('click', function() {
    document.getElementById('registerForm').classList.add('active');
    document.getElementById('loginForm').classList.remove('active');
    this.classList.add('active');
    document.getElementById('showLogin').classList.remove('active');
});
