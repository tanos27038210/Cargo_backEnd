package com.cargocrm.exception;

public class ParcelNotFoundException extends ResourceNotFoundException {
    public ParcelNotFoundException(String message) {
        super(message);
    }
}
