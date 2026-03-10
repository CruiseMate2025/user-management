package org.genc.usermgmt.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionTest {

    @Test
    @DisplayName("InsufficientPointsException - Coverage")
    void testInsufficientPointsException() {
        String msg = "Not enough loyalty points";
        InsufficientPointsException ex = new InsufficientPointsException(msg);

        assertEquals(msg, ex.getMessage());
    }

    @Test
    @DisplayName("UserNotFoundException - Coverage")
    void testUserNotFoundException() {
        String msg = "User not found with ID 1";
        UserNotFoundException ex = new UserNotFoundException(msg);

        assertEquals(msg, ex.getMessage());
    }

    @Test
    @DisplayName("RecordAlreadyExistsException - Full Constructor Coverage")
    void testRecordAlreadyExistsException() {
        String msg = "Record already in database";
        Throwable cause = new RuntimeException("Database constraint");

        // Testing both constructors for 100% coverage
        RecordAlreadyExistsException ex1 = new RecordAlreadyExistsException(msg);
        RecordAlreadyExistsException ex2 = new RecordAlreadyExistsException(msg, cause);

        assertEquals(msg, ex1.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    @DisplayName("ResourceNotFoundException - Full Constructor Coverage")
    void testResourceNotFoundException() {
        String msg = "Resource missing";
        Throwable cause = new RuntimeException("IO error");

        // Testing both constructors for 100% coverage
        ResourceNotFoundException ex1 = new ResourceNotFoundException(msg);
        ResourceNotFoundException ex2 = new ResourceNotFoundException(msg, cause);

        assertEquals(msg, ex1.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Full Constructor Coverage")
    void testUserAlreadyExistsException() {
        String msg = "User already exists";
        Throwable cause = new RuntimeException("Conflict");

        // Testing both constructors for 100% coverage
        UserAlreadyExistsException ex1 = new UserAlreadyExistsException(msg);
        UserAlreadyExistsException ex2 = new UserAlreadyExistsException(msg, cause);

        assertEquals(msg, ex1.getMessage());
        assertEquals(cause, ex2.getCause());
    }
}