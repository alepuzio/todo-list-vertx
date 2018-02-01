package io.vertx.mokabyte.model;

public class Error {

    private String message;

    private String detailMessage;

    public Error() {
    }

    public Error(String message, String detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }
}
