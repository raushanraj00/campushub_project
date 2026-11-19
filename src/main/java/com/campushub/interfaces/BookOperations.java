package com.campushub.interfaces;

import com.campushub.exceptions.BookNotAvailableException;

/**
 * UNIT 1: INTERFACES & POLYMORPHISM
 * BookOperations defines library inventory interactions performed by users.
 */
public interface BookOperations {

    /**
     * Issue a book by its unique ID.
     * @param bookId The ID of the book to checkout.
     * @throws BookNotAvailableException Checked exception thrown if stock is 0.
     */
    void issueBook(int bookId) throws BookNotAvailableException;

    /**
     * Return a previously issued book.
     * @param bookId The ID of the book being returned.
     */
    void returnBook(int bookId);
}

