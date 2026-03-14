package com.hinduprayerlock.backend.exceptions;

public class PhoneAlreadyExistsException extends RuntimeException {

    public PhoneAlreadyExistsException(String message) {
        super(message);
    }

}
