package com.candidatureformateur.exceptions;

import java.util.Map;


public class ApiException extends RuntimeException {

    private final String action;
    private final String message;

    public ApiException(String action, String message) {
        super(message);
        this.action = action;
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public String getMessageApi() {
        return message;
    }
}

/* public class ApiException extends RuntimeException {

    private final Map<String, Object> body;

    public ApiException(Map<String, Object> body) {
        super(body.getOrDefault("message", "Error").toString());
        this.body = body;
    }

    public Map<String, Object> getBody() {
        return body;
    }
}
 */