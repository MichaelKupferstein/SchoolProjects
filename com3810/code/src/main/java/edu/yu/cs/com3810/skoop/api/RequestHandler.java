package edu.yu.cs.com3810.skoop.api;

import java.util.Map;

/**
 * Request handler for the API routes.
 * Dispatches requests to the appropriate components.
 * HTTP
 */
public class RequestHandler {

    /**
     * Handle an incoming request.
     *
     * @param request the client request
     * @return the response to send back to the client
     */
    public Response handleRequest(Request request) {
        return null;
    }

    /**
     * Represents a client request.
     */
    public static class Request {
        private String path;
        private String method;
        private Map<String, String> headers;
        private String body;

        // Getters/setters
    }

    /**
     * Represents a response to send back to the client.
     */
    public static class Response {
        private int statusCode;
        private java.util.Map<String, String> headers;
        private String body;

        // Getters/setters
    }
}

