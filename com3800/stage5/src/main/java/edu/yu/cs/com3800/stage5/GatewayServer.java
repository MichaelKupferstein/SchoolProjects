package edu.yu.cs.com3800.stage5;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.yu.cs.com3800.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static edu.yu.cs.com3800.Message.MessageType.WORK;


public class GatewayServer extends Thread implements LoggingServer {
    private HttpServer httpServer;
    private final GatewayPeerServerImpl peerServer;
    private int httpPort;
    private ConcurrentHashMap<Integer, CachedResponse> cache;
    private Logger logger;
    private volatile boolean shutdown;
    private Queue<QueuedRequest> pendingRequests;
    private AtomicLong requestID;


    public GatewayServer(int httpPort, int peerPort, long peerEpoch, Long serverID,
                         ConcurrentHashMap<Long, InetSocketAddress> peerIDtoAdress, int numberOfObservers) throws IOException {
        this.httpPort = httpPort; //Should be 8888
        this.httpServer = HttpServer.create(new InetSocketAddress(httpPort),0);
        this.cache = new ConcurrentHashMap<>();
        this.logger = initializeLogging(GatewayServer.class.getCanonicalName() + "-on-port-" + this.httpPort);
        this.peerServer = new GatewayPeerServerImpl(peerPort, peerEpoch,serverID,peerIDtoAdress,numberOfObservers);
        this.requestID = new AtomicLong(1);
        this.pendingRequests = new ConcurrentLinkedQueue<>();
        this.httpServer.createContext("/compileandrun", new CompileAndRunHandler());
        this.httpServer.createContext("/logs/summary", new LogHander(true));
        this.httpServer.createContext("/logs/verbose", new LogHander(false));

        this.httpServer.setExecutor(Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors())*2));
        logger.info("GatewayServer initialized on HTTP port " + httpPort + " and peer port " + peerPort);
    }


    @Override
    public void run() {
        this.peerServer.start();
        this.httpServer.start();
        logger.info("GatewayServer started on HTTP port " + this.httpPort);

        while (!shutdown) {
            try {
                if (!pendingRequests.isEmpty()) {
                    QueuedRequest request = pendingRequests.poll();
                    if (request != null) {
                        proccessRequest(request.content, request.exchange,
                                new String(request.content).hashCode());
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                if (!shutdown) {
                    logger.warning("GatewayServer thread interrupted: " + e.getMessage());
                }
            }
        }
    }

    public void shutdown(){
        this.shutdown = true;
        this.peerServer.shutdown();
        this.httpServer.stop(0);
        logger.info("GatewayServer shutdown on HTTP port " + this.httpPort);
    }

    public GatewayPeerServerImpl getPeerServer() {
        return this.peerServer;
    }

    private void proccessRequest(byte[] body, HttpExchange exchange, int requestHash){
        try{
            InetSocketAddress leaderAddress = peerServer.getPeerByID(peerServer.getCurrentLeader().getProposedLeaderID());
            if (peerServer.isPeerDead(leaderAddress)) {
                long requestId = requestID.getAndIncrement();
                pendingRequests.add(new QueuedRequest(body, exchange, requestId));
                logger.info("No leader, GatewayServer added request to queue: " + requestId);
                return;
            }
            int leaderPort = leaderAddress.getPort() + 2;
            InetSocketAddress exchangeAddress = exchange.getLocalAddress();

            Message message = new Message(WORK, body, exchangeAddress.getHostString(), exchangeAddress.getPort(), leaderAddress.getHostString(),
                    leaderAddress.getPort(), requestID.getAndIncrement());

            try(Socket socket = new Socket(leaderAddress.getAddress(), leaderPort)) {
                OutputStream out = socket.getOutputStream();
                out.write(message.getNetworkPayload());
                out.flush();

                InputStream in = socket.getInputStream();
                byte[] response = Util.readAllBytesFromNetwork(in);

                if (peerServer.isPeerDead(leaderAddress)) {
                    pendingRequests.add(new QueuedRequest(body, exchange, message.getRequestID()));
                    logger.info("Leader died, GatewayServer added request to queue: " + message.getRequestID());
                    return;
                }

                Message responseMsg = new Message(response);
                int responseCode = responseMsg.getErrorOccurred() ? 400 : 200;
                byte[] responseBody = responseMsg.getMessageContents();

                cache.put(requestHash, new CachedResponse(responseCode, responseBody));
                exchange.getResponseHeaders().add("Cached-Response", "false");
                exchange.sendResponseHeaders(responseCode, responseBody.length);
                exchange.getResponseBody().write(responseBody);
                exchange.getResponseBody().close();
            }
        }catch (IOException e) {
            logger.severe("Failed to process request: " + e.getMessage());
            try{
                exchange.sendResponseHeaders(500, 0);
            }catch (IOException e2){
                logger.severe("Failed to send error response: " + e2.getMessage());
            }
        }finally {
            exchange.close();
        }
    }

    private class LogHander implements HttpHandler{
        private final boolean isSummary;

        LogHander(boolean summary){
            this.isSummary = summary;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(!"GET".equals(exchange.getRequestMethod())){
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
                return;
            }

            String logsDir = Files.list(Paths.get("."))
                    .filter(path -> path.toFile().isDirectory() && path.getFileName().toString().startsWith("logs"))
                    .max(Path::compareTo)
                    .orElseThrow(() -> new IOException("No logs directory found")).toString();

            String logFileName = String.format("%s/%s-%s-on-port-%d-Log.txt", logsDir, Gossiper.class.getCanonicalName(), isSummary ? "summary" : "verbose", peerServer.getUdpPort());

            try{
                byte[] logData = Files.readAllBytes(Paths.get(logFileName));
                exchange.sendResponseHeaders(200, logData.length);
                try(OutputStream out = exchange.getResponseBody()){
                    out.write(logData);
                }
            }catch (IOException e) {
                String error = "Error reading log file: " + e.getMessage();
                exchange.sendResponseHeaders(404, error.length());
                try(OutputStream out = exchange.getResponseBody()){
                    out.write(error.getBytes());
                }
            }
        }
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

            if(peerServer.getCurrentLeader() == null){
                long requestId = requestID.getAndIncrement();
                pendingRequests.add(new QueuedRequest(body, exchange, requestId));
                logger.info("No leader, GatewayServer added request to queue: " + requestId);
                return;
            }

            proccessRequest(body, exchange,requestHash);
        }
    }

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

    private class QueuedRequest {
        final byte[] content;
        final HttpExchange exchange;
        final long requestId;

        QueuedRequest(byte[] content, HttpExchange exchange, long requestId) {
            this.content = content;
            this.exchange = exchange;
            this.requestId = requestId;
        }
    }
}
