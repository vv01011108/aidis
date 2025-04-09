package com.example.book_manage.book.service;

import com.example.book_manage.book.db.BookEntity;
import com.example.book_manage.book.db.BookRepository;
import com.example.book_manage.user.db.UserEntity;
import com.example.book_manage.user.db.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    // 도서 등록
    public BookEntity addBook(BookEntity book, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail) // Optional<UserEntity> 반환
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")); // 사용자 없을 시 예외 처리

        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다."); // 사용자 없을 시 예외 처리
        }
        book.setUser(user); // BookEntity에 사용자 설정
        return bookRepository.save(book); // 도서 저장
    }

    // 도서 조회 (로그인한 사용자)
    public List<BookEntity> getUserBooks(UserEntity user) {
        return bookRepository.findByUser(user);
    }

    // 도서 검색
    public List<BookEntity> searchBooks(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    // 도서 수정
    public BookEntity updateBook(Long id, BookEntity newBook) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(newBook.getTitle());
            book.setAuthor(newBook.getAuthor());
            book.setPublisher(newBook.getPublisher());
            book.setGenre(newBook.getGenre());
            return bookRepository.save(book);
        }).orElse(null);
    }

    // 도서 삭제
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}


