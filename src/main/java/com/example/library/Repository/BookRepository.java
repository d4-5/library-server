package com.example.library.Repository;

import com.example.library.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByTitleContainingIgnoreCaseOrAuthorSurnameContainingIgnoreCase(String title, String authorSurname);

    boolean existsByTitleAndAuthorNameAndAuthorSurnameAndAuthorPatronymicAndYear(String title, String authorName, String authorSurname, String authorPatronymic, Integer year);

    boolean existsByTitleAndAuthorNameAndAuthorSurnameAndAuthorPatronymicAndYearAndIdNot(String title, String authorName, String authorSurname, String authorPatronymic, Integer year, Integer id);

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.authorName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.authorSurname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.authorPatronymic) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "STR(b.year) LIKE :query OR " +
            "STR(b.quantity) LIKE :query")
    List<Book> searchByAllFields(@Param("query") String query);
}
