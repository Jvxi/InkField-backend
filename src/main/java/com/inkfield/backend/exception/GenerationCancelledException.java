package com.inkfield.backend.exception;

public class GenerationCancelledException extends RuntimeException {
    public GenerationCancelledException() {
        super("Generation cancelled.");
    }
}
