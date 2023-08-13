package ru.practicum.ewm.exception;

public class MainErrorResponse {

    private final String error;

    public MainErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}