package com.feup.sdis.exceptions;

public class MessageError extends Throwable {
    private final String message;

    public MessageError(String message) {
        this.message = message;
    }

    @Override
    public void printStackTrace() {
        System.err.println(this.message);
        super.printStackTrace();
    }
}
