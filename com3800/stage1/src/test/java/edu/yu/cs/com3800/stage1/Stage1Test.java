package edu.yu.cs.com3800.stage1;

import edu.yu.cs.com3800.stage1.SimpleServerImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Stage1Test {

    private SimpleServerImpl server;
    private Client client;

    @BeforeEach
    public void setUp() {
        try {
            server = new SimpleServerImpl(9000);
            server.start();
            client = new ClientImpl("localhost", 9000);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            server.stop();
        }
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }



    @Test
    public void testCompileAndRun() throws IOException {
        String code = "public class Test {" +
                "    public String run (){ " +
                "       return \"Hello, World!\";" +
                "    }" +
                "}";

        client.sendCompileAndRunRequest(code);
        Client.Response response = client.getResponse();
        assertEquals(200, response.getCode());
        assertEquals("Hello, World!", response.getBody());
    }

    @Test
    public void testCompileAndRunWithThrowingException() throws IOException {
        String code = "public class Test {" +
                "    public String run (){ " +
                "       throw new RuntimeException(\"This is a test exception\");" +
                "    }" +
                "}";

        client.sendCompileAndRunRequest(code);
        Client.Response response = client.getResponse();
        assertEquals(400, response.getCode());
    }

    @Test
    public void testSendCompileAndRunRequestSuccess() throws IOException {
        String code = "public class Test { public String run() { return \"Hello, World!\"; } }";
        client.sendCompileAndRunRequest(code);
        Client.Response response = client.getResponse();

        String expectedResponse = "Hello, World!";
        String actualResponse = response.getBody();

        System.out.println("\nExpected response:\n" + expectedResponse);
        System.out.println("Actual response:\n" + actualResponse + "\n");

        assertEquals(expectedResponse, actualResponse);
    }
}
