package edu.yu.cs.com3800.stage1;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.SimpleServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class SimpleServerImpl implements SimpleServer {

    private static final Logger logger = Logger.getLogger(SimpleServerImpl.class.getName());
    private int port;
    private HttpServer server;

    public SimpleServerImpl(int port) throws IOException {
        this.port = port;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.createContext("/compileandrun", new MyHandler());

    }

    /**
     * start the server
     */
    @Override
    public void start() {
        this.server.start();
        logger.info("Server started on port " + this.port);
    }

    /**
     * stop the server
     */
    @Override
    public void stop() {
        this.server.stop(0);
        logger.info("Server stopped");
    }

    public static void main(String[] args) {

        int port = 9000;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        SimpleServer myServer = null;
        try {
            myServer = new SimpleServerImpl(port);
            myServer.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            myServer.stop();
        }
    }

    private class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(!"POST".equals(exchange.getRequestMethod())){
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            if(!"text/x-java-source".equals(exchange.getRequestHeaders().getFirst("Content-Type"))){
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            InputStream is = exchange.getRequestBody();
            is.close();

            JavaRunner runner = new JavaRunner();
            try{
                String result = runner.compileAndRun(is);
                exchange.sendResponseHeaders(200, result.length());
                exchange.getResponseBody().write(result.getBytes());
            } catch (Exception e) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baos));
                String response = e.getMessage() + "\n" + baos.toString();
                exchange.sendResponseHeaders(400, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } finally {
                exchange.close();
            }
        }
    }
}
