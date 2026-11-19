package com.campushub;

import com.campushub.database.DBConnection;
import com.campushub.exceptions.BookNotAvailableException;
import com.campushub.exceptions.BookingConflictException;
import com.campushub.interfaces.Authenticatable;
import com.campushub.interfaces.BookOperations;
import com.campushub.models.Admin;
import com.campushub.models.Book;
import com.campushub.models.Booking;
import com.campushub.models.Faculty;
import com.campushub.models.Person;
import com.campushub.models.Room;
import com.campushub.models.Student;
import com.campushub.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * UNIT 1: JUNIT 5 TEST SUITE FOR OOP SYLLABUS TOPICS
 * Verifies inheritance, polymorphism, encapsulation, constructors,
 * interfaces, inner classes, and custom exceptions.
 */
public class OOPConceptsTest {

    @Test
    public void testInheritanceAndPolymorphism() {
        // Person -> User -> Student
        Student student = new Student("23CS999", "Vikram Patel", "vikram@campus.edu", 
                                      "9999988888", "CSE", 4, "A");
        
        // Polymorphic assignment: Child object referenced via Abstract Parent references
        User userRef = student;
        Person personRef = student;
        Authenticatable authRef = student;
        BookOperations bookOpsRef = student;

        Assertions.assertEquals("STUDENT", userRef.getRole(), "Dynamic dispatch should invoke Student.getRole()");
        Assertions.assertTrue(personRef.getDetails().contains("23CS999"), "Student.getDetails() should override Person.getDetails()");
        Assertions.assertEquals("STUDENT", authRef.getRole());
        Assertions.assertNotNull(bookOpsRef);

        // Test Faculty inheritance
        Faculty faculty = new Faculty("FAC-99", "Dr. Anita Desai", "anita@campus.edu", "9876543210", "CSE", "HOD");
        User facultyUserRef = faculty;
        Assertions.assertEquals("FACULTY", facultyUserRef.getRole());

        // Test Admin inheritance
        Admin admin = new Admin(1, "Admin User", "admin@campus.edu", "admin", "hash", "salt", "SUPER_ADMIN");
        User adminUserRef = admin;
        Assertions.assertEquals("ADMIN", adminUserRef.getRole());
    }

    @Test
    public void testConstructorsAndCopyConstructor() {
        // 1. Parameterized constructor
        Student s1 = new Student("23CS101", "Aarav Sharma", "aarav@campus.edu", "9876543210", "CSE", 3, "B");
        
        // 2. User-defined Copy Constructor
        Student s2 = new Student(s1);

        Assertions.assertEquals(s1.getUid(), s2.getUid());
        Assertions.assertEquals(s1.getName(), s2.getName());
        Assertions.assertEquals(s1.getEmail(), s2.getEmail());
        Assertions.assertNotSame(s1, s2, "Copy constructor must create a distinct new object instance");
    }

    @Test
    public void testInnerClassAccess() {
        Booking booking = new Booking(101, 5, 2, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Practical Exam", "CONFIRMED");
        booking.setRoomName("Lab 101");
        booking.setUserName("Student Aarav");

        // Instantiate non-static member inner class
        Booking.BookingDetails details = booking.getBookingDetails();
        Assertions.assertNotNull(details);
        Assertions.assertSame(booking, details.getOuterBooking(), "Inner class must reference enclosing outer instance");

        String pass = details.getReservationPass();
        Assertions.assertTrue(pass.contains("Booking ID   : #101"));
        Assertions.assertTrue(pass.contains("Lab 101"));
    }

    @Test
    public void testStaticNestedClass() {
        // Demonstrates static nested class: DBConnection.DatabaseConfig
        Assertions.assertNotNull(DBConnection.DatabaseConfig.getUrl());
        Assertions.assertTrue(DBConnection.DatabaseConfig.isLoaded());
    }

    @Test
    public void testCustomExceptionBookNotAvailable() {
        Book book = new Book(1, "Java OOP", "Herbert Schildt", "123-456", "CS", 1, 0); // 0 copies available

        Assertions.assertThrows(BookNotAvailableException.class, () -> {
            book.issueCopy(); // Must throw custom BookNotAvailableException
        });
    }

    @Test
    public void testCustomExceptionBookingConflict() {
        BookingConflictException ex = new BookingConflictException(10, "Overlapping slot");
        Assertions.assertEquals(10, ex.getRoomId());
        Assertions.assertTrue(ex.getMessage().contains("booking conflict"));
    }
}

