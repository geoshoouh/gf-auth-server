package com.gf.server.exceptions;

public class FailedSaveException extends RuntimeException {
    
    public FailedSaveException() {
        super("Failed to save Entity.");
    }
}
