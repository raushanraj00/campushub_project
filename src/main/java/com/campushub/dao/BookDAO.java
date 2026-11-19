package com.campushub.dao;

import com.campushub.database.DBConnection;
import com.campushub.exceptions.BookNotAvailableException;
import com.campushub.models.Book;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * UNIT 1: COMPILE-TIME POLYMORPHISM (METHOD OVERLOADING)
 * UNIT 3: JDBC TRANSACTIONS (commit & rollback)
 * BookDAO manages library book inventory and checkout transactions.
 */
public class BookDAO {

    private static final List<Book> MEMORY_BOOKS = new ArrayList<>();
    static {
        MEMORY_BOOKS.add(new Book(1, "Core Java: Fundamentals", "Cay S. Horstmann", "978-0135114063", "Computer Science", 10, 8));
        MEMORY_BOOKS.add(new Book(2, "Effective Java", "Joshua Bloch", "978-0134685991", "Computer Science", 5, 4));
        MEMORY_BOOKS.add(new Book(3, "Database System Concepts", "Abraham Silberschatz", "978-0078022159", "Database", 8, 8));
        MEMORY_BOOKS.add(new Book(4, "Operating System Concepts", "Peter B. Galvin", "978-1118063330", "Systems", 6, 5));
    }

    // =========================================================================
    // UNIT 1: METHOD OVERLOADING (Compile-Time Polymorphism)
    // Three distinct methods sharing the name 'searchBook' with different signatures
    // =========================================================================

    /**
     * Overloaded method 1: Search by unique integer Book ID.
     */
    public List<Book> searchBook(int bookId) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO] Overload 1 (by ID) error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Overloaded method 2: Search by title substring.
     */
    public List<Book> searchBook(String title) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO] Overload 2 (by Title) error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Overloaded method 3: Search by both title AND author substrings.
     */
    public List<Book> searchBook(String title, String author) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? AND author LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + title + "%");
            ps.setString(2, "%" + author + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO] Overload 3 (by Title & Author) error: " + e.getMessage());
        }
        return list;
    }

    // =========================================================================
    // UNIT 3: JDBC CRUD & INVENTORY OPERATIONS
    // =========================================================================

    public boolean addBook(Book b) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, category, total_quantity, available_quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getIsbn());
            ps.setString(4, b.getCategory());
            ps.setInt(5, b.getTotalQuantity());
            ps.setInt(6, b.getAvailableQuantity());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) b.setBookId(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY book_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookDAO] Using memory books (MySQL offline): " + e.getMessage());
        }
        if (list.isEmpty()) {
            list.addAll(MEMORY_BOOKS);
        }
        return list;
    }

    public boolean updateBook(Book b) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, category = ?, total_quantity = ?, available_quantity = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getIsbn());
            ps.setString(4, b.getCategory());
            ps.setInt(5, b.getTotalQuantity());
            ps.setInt(6, b.getAvailableQuantity());
            ps.setInt(7, b.getBookId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // UNIT 3: JDBC TRANSACTIONS (commit & rollback)
    // =========================================================================

    /**
     * Issues a book inside a strict JDBC transaction.
     * Decrements inventory and creates checkout record.
     */
    public boolean issueBookTransaction(int studentId, int bookId, int issueDays) 
            throws SQLException, BookNotAvailableException {
        Connection conn = DBConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Check availability with pessimistic lock
            String checkSql = "SELECT available_quantity, total_quantity FROM books WHERE book_id = ? FOR UPDATE";
            int available = 0;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, bookId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        available = rs.getInt("available_quantity");
                    } else {
                        throw new BookNotAvailableException(bookId, "Book ID not found in system.");
                    }
                }
            }

            if (available <= 0) {
                throw new BookNotAvailableException(bookId, "No copies currently available for issue.");
            }

            // 2. Decrement available quantity
            String updateSql = "UPDATE books SET available_quantity = available_quantity - 1 WHERE book_id = ?";
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setInt(1, bookId);
                updatePs.executeUpdate();
            }

            // 3. Insert into issued_books
            LocalDate today = LocalDate.now();
            LocalDate dueDate = today.plusDays(issueDays > 0 ? issueDays : 14);
            String issueSql = "INSERT INTO issued_books (student_id, book_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, 'ISSUED')";
            try (PreparedStatement issuePs = conn.prepareStatement(issueSql)) {
                issuePs.setInt(1, studentId);
                issuePs.setInt(2, bookId);
                issuePs.setDate(3, Date.valueOf(today));
                issuePs.setDate(4, Date.valueOf(dueDate));
                issuePs.executeUpdate();
            }

            conn.commit(); // Commit Transaction
            return true;
        } catch (Exception e) {
            conn.rollback(); // Rollback on failure to prevent inconsistent database state
            if (e instanceof BookNotAvailableException) {
                throw (BookNotAvailableException) e;
            }
            throw new SQLException("Issue transaction rolled back: " + e.getMessage(), e);
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    /**
     * Returns a book inside a JDBC transaction.
     * Increments inventory and marks issue record as RETURNED with calculated fine.
     */
    public boolean returnBookTransaction(int issueId, int bookId, double fine) throws SQLException {
        Connection conn = DBConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Increment available quantity
            String updateSql = "UPDATE books SET available_quantity = LEAST(available_quantity + 1, total_quantity) WHERE book_id = ?";
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setInt(1, bookId);
                updatePs.executeUpdate();
            }

            // 2. Mark issued_books as RETURNED
            String returnSql = "UPDATE issued_books SET return_date = CURDATE(), fine_amount = ?, status = 'RETURNED' WHERE issue_id = ?";
            try (PreparedStatement returnPs = conn.prepareStatement(returnSql)) {
                returnPs.setDouble(1, Math.max(0.0, fine));
                returnPs.setInt(2, issueId);
                returnPs.executeUpdate();
            }

            conn.commit(); // Commit Transaction
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public int countTotalBooks() {
        return getCountQuery("SELECT SUM(total_quantity) FROM books");
    }

    public int countAvailableBooks() {
        return getCountQuery("SELECT SUM(available_quantity) FROM books");
    }

    public int countIssuedBooks() {
        return getCountQuery("SELECT COUNT(*) FROM issued_books WHERE status = 'ISSUED'");
    }

    private int getCountQuery(String sql) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ignored) {}
        return 0;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getString("category"),
            rs.getInt("total_quantity"),
            rs.getInt("available_quantity")
        );
    }
}

