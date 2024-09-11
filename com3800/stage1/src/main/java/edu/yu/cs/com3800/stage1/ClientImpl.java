package edu.yu.cs.com3800.stage1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.logging.Logger;

public class ClientImpl implements Client{

    private static final Logger logger = Logger.getLogger(ClientImpl.class.getName());
    private String hostName;
    private int hostPort;
    private Response res;

    public ClientImpl(String hostName, int hostPort) throws MalformedURLException {
        this.hostName = hostName;
        this.hostPort = hostPort;
    }

    @Override
    public void sendCompileAndRunRequest(String src) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(src))
                .uri(URI.create("http://" + hostName + ":" + hostPort + "/compileandrun"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .header("Content-Type", "text/x-java-source")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.res = new Response(response.statusCode(), response.body());
        } catch (InterruptedException e) {
            logger.severe("Error sending request: " + e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }


    }

    @Override
    public Response getResponse() throws IOException {
        return this.res;
    }
}
