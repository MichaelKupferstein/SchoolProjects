package edu.yu.cs.com3800.stage1;

import java.io.IOException;
import java.net.MalformedURLException;

public class ClientImpl implements Client{

    private String hostName;
    private int hostPort;
    private Response res;

    public ClientImpl(String hostName, int hostPort) throws MalformedURLException {
        this.hostName = hostName;
        this.hostPort = hostPort;
    }

    @Override
    public void sendCompileAndRunRequest(String src) throws IOException {
        // send request to server

    }

    @Override
    public Response getResponse() throws IOException {
        return null;
    }
}
