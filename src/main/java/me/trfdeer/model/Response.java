package me.trfdeer.model;

public class Response<T> {
    public boolean error;
    public String message;
    public T data;

    public Response(boolean error, String message, T data) {
        this.error = error;
        this.message = message;
        this.data = data;
    }
}
