package io.vertx.mokabyte.model;

public class Error {

    private final String message;

    private final String detailMessage;

    public Error(String message, String detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
