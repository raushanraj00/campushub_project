package com.campushub.models;

import com.campushub.exceptions.BookNotAvailableException;
import java.io.Serializable;

/**
 * UNIT 1 & UNIT 2: ENCAPSULATION & SERIALIZATION
 * Book models library materials.
 * Demonstrates:
 * - java.io.Serializable for object serialization/deserialization
 * - Encapsulation with business invariants checked in methods
 * - Custom checked exception throwing (BookNotAvailableException)
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int totalQuantity;
    private int availableQuantity;

    public Book() {
        this(0, "Untitled", "Unknown Author", "ISBN-000", "General", 1, 1);
    }

    public Book(int bookId, String title, String author, String isbn, 
                String category, int totalQuantity, int availableQuantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.totalQuantity = Math.max(0, totalQuantity);
        this.availableQuantity = Math.max(0, Math.min(availableQuantity, totalQuantity));
    }

    public Book(String title, String author, String isbn, String category, int totalQuantity) {
        this(0, title, author, isbn, category, totalQuantity, totalQuantity);
    }

    public boolean isAvailable() {
        return availableQuantity > 0;
    }

    /**
     * Deducts one available copy from inventory.
     * @throws BookNotAvailableException if no copies remain.
     */
    public synchronized void issueCopy() throws BookNotAvailableException {
        if (availableQuantity <= 0) {
            throw new BookNotAvailableException(bookId, "All " + totalQuantity + " copies are currently issued.");
        }
        availableQuantity--;
    }

    /**
     * Returns one copy back to inventory.
     */
    public synchronized void returnCopy() {
        if (availableQuantity < totalQuantity) {
            availableQuantity++;
        }
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = Math.max(0, totalQuantity);
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = Math.max(0, Math.min(availableQuantity, totalQuantity));
    }

    @Override
    public String toString() {
        return String.format("Book[ID=%d, Title='%s', Author='%s', Available=%d/%d]",
                bookId, title, author, availableQuantity, totalQuantity);
    }
}

