package com.example.book_manage.book.controller;

import com.example.book_manage.book.db.BookEntity;
import com.example.book_manage.book.service.BookService;
import com.example.book_manage.user.db.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // 도서 등록
    @PostMapping("/add")
    public ResponseEntity<BookEntity> addBook(@RequestBody BookEntity book, @SessionAttribute(value = "user", required = false) UserEntity user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 사용자 정보가 없으면 401 응답
        }
        BookEntity createdBook = bookService.addBook(book, user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }


    // 로그인한 사용자의 도서 목록 조회
    @GetMapping
    public ResponseEntity<List<BookEntity>> getUserBooks(@SessionAttribute("user") UserEntity user) {
        List<BookEntity> books = bookService.getUserBooks(user);
        return ResponseEntity.ok(books);
    }

    // 도서 검색
    @GetMapping("/search")
    public ResponseEntity<List<BookEntity>> searchBooks(@RequestParam String keyword) {
        List<BookEntity> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }

    // 도서 수정
    @PutMapping("/{id}")
    public ResponseEntity<BookEntity> updateBook(@PathVariable Long id, @RequestBody BookEntity book) {
        BookEntity updatedBook = bookService.updateBook(id, book);
        return updatedBook != null ? ResponseEntity.ok(updatedBook) : ResponseEntity.notFound().build();
    }

    // 도서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }


}