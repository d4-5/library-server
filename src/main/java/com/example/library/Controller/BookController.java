package com.example.library.Controller;

import com.example.library.Entity.Book;
import com.example.library.Repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return (List<Book>) bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody Book bookRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        boolean bookExists = bookRepository.existsByTitleAndAuthorNameAndAuthorSurnameAndAuthorPatronymicAndYear(
                bookRequest.getTitle(),
                bookRequest.getAuthorName(),
                bookRequest.getAuthorSurname(),
                bookRequest.getAuthorPatronymic(),
                bookRequest.getYear()
        );

        if (bookExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A book with the same details already exists.");
        }

        Book savedBook = bookRepository.save(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @Valid @RequestBody Book bookRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        return bookRepository.findById(id)
                .map(existingBook -> {
                    boolean duplicateExists = bookRepository.existsByTitleAndAuthorNameAndAuthorSurnameAndAuthorPatronymicAndYearAndIdNot(
                            bookRequest.getTitle(),
                            bookRequest.getAuthorName(),
                            bookRequest.getAuthorSurname(),
                            bookRequest.getAuthorPatronymic(),
                            bookRequest.getYear(),
                            id
                    );

                    if (duplicateExists) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("Updating the book would result in duplicate entries.");
                    }

                    existingBook.setTitle(bookRequest.getTitle());
                    existingBook.setAuthorName(bookRequest.getAuthorName());
                    existingBook.setAuthorSurname(bookRequest.getAuthorSurname());
                    existingBook.setAuthorPatronymic(bookRequest.getAuthorPatronymic());
                    existingBook.setYear(bookRequest.getYear());
                    existingBook.setQuantity(bookRequest.getQuantity());
                    return ResponseEntity.ok().body(bookRepository.save(existingBook));
                })
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            return bookRepository.searchByAllFields(query);
        } else {
            return getAllBooks();
        }
    }
}
