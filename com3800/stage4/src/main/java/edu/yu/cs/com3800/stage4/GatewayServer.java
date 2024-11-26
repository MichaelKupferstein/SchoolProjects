package edu.yu.cs.com3800.stage4;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.yu.cs.com3800.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static edu.yu.cs.com3800.Message.MessageType.WORK;


public class GatewayServer extends Thread implements LoggingServer {
    private class CachedResponse{
        final int code;
        final byte[] body;

        CachedResponse(int code, byte[] body){
            this.code = code;
            this.body = body;
        }

        @Override
        public int hashCode(){
            return new String(body).hashCode();
        }
        @Override
        public boolean equals(Object o){
            if(o == this) return true;
            if(!(o instanceof CachedResponse)) return false;
            CachedResponse other = (CachedResponse) o;
            return this.code == other.code && this.body.equals(other.body);
        }

    }

    private HttpServer httpServer;
    private final GatewayPeerServerImpl peerServer;
    private int httpPort;
    private ConcurrentHashMap<Integer, CachedResponse> cache;
    private Logger logger;
    private volatile boolean shutdown;


    public GatewayServer(int httpPort, int peerPort, long peerEpoch, Long serverID,
                         ConcurrentHashMap<Long, InetSocketAddress> peerIDtoAdress, int numberOfObservers) throws IOException {
        this.httpPort = httpPort; //Should be 8888
        this.httpServer = HttpServer.create(new InetSocketAddress(httpPort),0);
        this.cache = new ConcurrentHashMap<>();
        this.logger = initializeLogging(GatewayServer.class.getCanonicalName() + "-on-port-" + this.httpPort);
        this.peerServer = new GatewayPeerServerImpl(peerPort, peerEpoch,serverID,peerIDtoAdress,numberOfObservers);
        this.httpServer.createContext("/compileandrun", new CompileAndRunHandler());
        this.httpServer.setExecutor(Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors())*2));
        logger.info("GatewayServer initialized on HTTP port " + httpPort + " and peer port " + peerPort);
    }


    @Override
    public void run() {
        this.peerServer.start();
        this.httpServer.start();
        logger.info("GatewayServer started on HTTP port " + this.httpPort);
    }

    public void shutdown(){
        this.shutdown = true;
        this.peerServer.shutdown();
        this.httpServer.stop(0);
        logger.info("GatewayServer shutdown on HTTP port " + this.httpPort);
    }

    private class CompileAndRunHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            exchange.getResponseHeaders().add("Cached-Response", "false");

            if(!"POST".equals(exchange.getRequestMethod())){
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
                return;
            }

            if(!"text/x-java-source".equals(exchange.getRequestHeaders().getFirst("Content-Type"))){
                exchange.sendResponseHeaders(400, 0);
                exchange.getResponseBody().close();
                return;
            }

            byte[] body = exchange.getRequestBody().readAllBytes();
            int requestHash = new String(body).hashCode();

            CachedResponse cachedResponse = cache.get(requestHash);
            if(cachedResponse != null){
                exchange.getResponseHeaders().add("Cached-Response", "true");
                exchange.sendResponseHeaders(cachedResponse.code, cachedResponse.body.length);
                exchange.getResponseBody().write(cachedResponse.body);
                exchange.getResponseBody().close();
                return;
            }

            InetSocketAddress leaderAddress = peerServer.getPeerByID(peerServer.getCurrentLeader().getProposedLeaderID());
            int leaderPort = leaderAddress.getPort() + 2;

            InetSocketAddress exchangeAddress = exchange.getLocalAddress();
            Message message = new Message(WORK, body, exchangeAddress.getHostString(), exchangeAddress.getPort(), leaderAddress.getHostString(), leaderAddress.getPort());

            try(Socket socket = new Socket(leaderAddress.getAddress(), leaderPort)) {
                OutputStream out = socket.getOutputStream();
                out.write(message.getNetworkPayload());
                out.flush();

                InputStream in = socket.getInputStream();
                byte[] response = Util.readAllBytesFromNetwork(in);

                Message responseMsg = new Message(response);
                int responseCode = responseMsg.getErrorOccurred() ? 400 : 200;
                byte[] responseBody = responseMsg.getMessageContents();

                cache.put(requestHash, new CachedResponse(responseCode, responseBody));
                exchange.getResponseHeaders().add("Cached-Response", "false");
                exchange.sendResponseHeaders(responseCode, responseBody.length);
                exchange.getResponseBody().write(responseBody);
                exchange.getResponseBody().close();
            }catch (IOException e) {
                logger.severe("GatewayServer failed to forward request to leader: " + e.getMessage());
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }

        }
    }


}
