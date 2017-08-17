package ru.hd.olaf.util;

/**
 * Created by d.v.hozyashev on 17.08.2017.
 */
public class JsonResponse {
    private String message;

    public JsonResponse() {
    }

    public JsonResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
