package com.campushub.services;

import com.campushub.dao.BookDAO;
import com.campushub.exceptions.BookNotAvailableException;
import com.campushub.models.Book;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * UNIT 1: OPERATORS, EXPRESSIONS & FINE COMPUTATION
 * LibraryService coordinates library inventory and policy rules.
 */
public class LibraryService {

    public static final double DAILY_FINE_RATE = 5.0; // ₹5 per day late fine
    private final BookDAO bookDAO;

    public LibraryService() {
        this.bookDAO = new BookDAO();
    }

    public LibraryService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Calculates overdue fine based on elapsed days.
     * Demonstrates arithmetic operators and control logic.
     */
    public double calculateOverdueFine(LocalDate dueDate, LocalDate returnDate) {
        if (dueDate == null) return 0.0;
        LocalDate actualReturn = (returnDate != null) ? returnDate : LocalDate.now();

        if (actualReturn.isAfter(dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, actualReturn);
            return daysOverdue * DAILY_FINE_RATE; // Operator expression
        }
        return 0.0;
    }

    public boolean registerNewBook(Book book) throws SQLException, IllegalArgumentException {
        if (book == null) throw new IllegalArgumentException("Book details cannot be null.");
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book Title is required.");
        }
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("Book ISBN is required.");
        }
        return bookDAO.addBook(book);
    }

    public boolean updateBook(Book book) throws SQLException {
        return bookDAO.updateBook(book);
    }

    public boolean removeBook(int bookId) throws SQLException {
        return bookDAO.deleteBook(bookId);
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    // Demonstrates calling overloaded DAO search methods
    public List<Book> search(String title) {
        return bookDAO.searchBook(title);
    }

    public List<Book> search(String title, String author) {
        return bookDAO.searchBook(title, author);
    }

    public List<Book> search(int bookId) {
        return bookDAO.searchBook(bookId);
    }

    public boolean issueBookToStudent(int studentId, int bookId, int days) 
            throws SQLException, BookNotAvailableException {
        return bookDAO.issueBookTransaction(studentId, bookId, days);
    }

    public boolean processBookReturn(int issueId, int bookId, double fine) throws SQLException {
        return bookDAO.returnBookTransaction(issueId, bookId, fine);
    }

    public int getTotalBooks() { return bookDAO.countTotalBooks(); }
    public int getAvailableBooks() { return bookDAO.countAvailableBooks(); }
    public int getIssuedBooks() { return bookDAO.countIssuedBooks(); }
}

