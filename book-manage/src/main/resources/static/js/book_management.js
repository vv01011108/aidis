document.addEventListener('DOMContentLoaded', () => {
    const bookForm = document.getElementById('bookForm');
    const bookList = document.getElementById('bookList').querySelector('tbody');
    const username = document.getElementById('username');
    const searchInput = document.getElementById('searchInput');
    const resetButton = document.getElementById('resetButton');

    const userInfo = JSON.parse(localStorage.getItem('username'));
    if (userInfo) {
        username.textContent = userInfo.name;
    } else {
        window.location.href = '/login.html';
        return;
    }

    // 공통 Fetch 요청 함수
    const fetchRequest = (url, options) => {
        return fetch(url, options)
            .then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = '/login.html';
                    }
                    throw new Error('서버 응답 오류');
                }
                return response.json();
            })
            .catch(error => {
                console.error('Error:', error);
                return null;
            });
    };

    // 도서 목록 조회
    const fetchBooks = () => {
        fetchRequest('/api/books', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${userInfo.token}`
            }
        }).then(books => {
            if (books) {
                renderBookList(books);
                saveBooksToLocalStorage(books);
            }
        });
    };

    // 도서 목록 렌더링 함수
    const renderBookList = (books) => {
        bookList.innerHTML = books.length
            ? books.map(book => createBookRow(book)).join('')
            : '<tr><td colspan="5">등록된 도서가 없습니다.</td></tr>';
    };

    // 도서 목록에 도서를 추가하는 함수 (HTML 생성)
    const createBookRow = (book) => {
        return `
            <tr data-id="${book.id}">
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${book.publisher}</td>
                <td>${book.genre}</td>
                <td>
                    <button class="edit">수정</button>
                    <button class="delete">삭제</button>
                </td>
            </tr>
        `;
    };

    // 도서 등록 및 수정 처리
    bookForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const bookData = {
            title: document.getElementById('bookTitle').value,
            author: document.getElementById('bookAuthor').value,
            publisher: document.getElementById('bookPublisher').value,
            genre: document.getElementById('bookGenre').value
        };
        const method = bookForm.dataset.editing ? 'PUT' : 'POST';
        const url = bookForm.dataset.editing ? `/api/books/${bookForm.dataset.editing}` : '/api/books/add';

        fetchRequest(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${userInfo.token}`
            },
            body: JSON.stringify(bookData)
        }).then(() => {
            fetchBooks();
            bookForm.reset();
            delete bookForm.dataset.editing; // 수정 상태 초기화
        });
    });

    // 도서 수정 및 삭제 기능
    bookList.addEventListener('click', (event) => {
        const bookId = event.target.closest('tr')?.dataset.id;
        if (event.target.classList.contains('edit')) {
            loadBookToForm(bookId);
        } else if (event.target.classList.contains('delete')) {
            deleteBook(bookId);
        }
    });

    // 수정할 도서 데이터를 폼에 로드
    const loadBookToForm = (bookId) => {
        const row = document.querySelector(`tr[data-id='${bookId}']`);
        if (row) {
            const [title, author, publisher, genre] = Array.from(row.children).map(td => td.textContent);
            document.getElementById('bookTitle').value = title;
            document.getElementById('bookAuthor').value = author;
            document.getElementById('bookPublisher').value = publisher;
            document.getElementById('bookGenre').value = genre;
            bookForm.dataset.editing = bookId;
        }
    };

    // 도서 삭제
    const deleteBook = (bookId) => {
        fetchRequest(`/api/books/${bookId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${userInfo.token}`
            }
        }).then(() => {
            document.querySelector(`tr[data-id='${bookId}']`).remove();
            saveBooksToLocalStorage(getCurrentBooks());
        });
    };

    // 로컬 스토리지에 도서 목록 저장
    const saveBooksToLocalStorage = (books) => {
        localStorage.setItem('books', JSON.stringify(books));
    };

    // 로컬 스토리지에서 도서 목록 불러오기
    const loadBooksFromLocalStorage = () => {
        const books = JSON.parse(localStorage.getItem('books')) || [];
        renderBookList(books);
    };

    // 현재 도서 목록 가져오기
    const getCurrentBooks = () => {
        return Array.from(bookList.querySelectorAll('tr')).map(row => ({
            id: row.dataset.id,
            title: row.children[0].textContent,
            author: row.children[1].textContent,
            publisher: row.children[2].textContent,
            genre: row.children[3].textContent
        }));
    };

    // 페이지 로드 시 도서 목록 불러오기 및 API에서 도서 목록 조회
    loadBooksFromLocalStorage();
    fetchBooks();

    // 초기화 버튼 클릭 시 도서 목록 새로 고침
    resetButton.addEventListener('click', () => {
        searchInput.value = '';
        fetchBooks();
    });
});
