package edu.yu.cs.com3800.stage4;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.PeerServer;
import edu.yu.cs.com3800.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class GatewayServer extends Thread implements LoggingServer {


    private HttpServer httpServer;
    private final GatewayPeerServerImpl peerServer;
    private int httpPort;
    private ConcurrentHashMap<Integer, byte[]> cache;
    private Logger logger;
    private volatile boolean shutdown;

    public GatewayServer(int httpPort, int peerPort, long peerEpoch, Long serverID,
                         ConcurrentHashMap<Long, InetSocketAddress> peerIDtoAdress, int numberOfObservers) throws IOException{
        this.httpPort = httpPort;
        this.httpServer = HttpServer.create(new InetSocketAddress(httpPort),0); //create an HTTPServer on...
        this.cache = new ConcurrentHashMap<>();
        this.logger = initializeLogging(GatewayServer.class.getCanonicalName() + "-on-port-" + this.httpPort);
        this.peerServer = new GatewayPeerServerImpl(peerPort, peerEpoch,serverID,peerIDtoAdress,numberOfObservers);

        this.httpServer.createContext("/compileandrun", new CompileAndRunHandler(this.httpPort));
        this.httpServer.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    @Override
    public void run(){
        try{
            this.logger.fine("Starting Gateway Server on port: " + this.httpPort);
            this.httpServer.start();
            this.peerServer.start();

            while (!shutdown && !isInterrupted()){
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    if(shutdown){
                        break;
                    }
                }
            }
        } catch (Exception e){
            this.logger.severe("Gateway server Failed: " + e.getMessage());
        }finally {
            shutdown();
        }
    }

    public void shutdown(){
        this.logger.fine("Shutting down Gateway Server");
        this.shutdown = true;
        this.httpServer.stop(0);
        this.peerServer.shutdown();
        interrupt();
    }

    public PeerServer getPeerServer() {
        return this.peerServer;
    }
    public boolean isReady(){
        return this.peerServer.getCurrentLeader() != null;
    }


    private class CompileAndRunHandler implements HttpHandler{

        private int port;

        public CompileAndRunHandler(int port){
            this.port = port;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try{
                if(!"POST".equals(exchange.getRequestMethod())){
                    exchange.sendResponseHeaders(405,-1);
                    return;
                }

                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
                if(contentType == null || !contentType.equals("text/x-java-source")){
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                InputStream is = exchange.getRequestBody();
                byte[] requestBody = Util.readAllBytes(is);
                is.close();

                int requestHash = new String(requestBody).hashCode();
                byte[] cacheRespnose = cache.get(requestHash);
                if(cacheRespnose != null){ //i.e there was something in cache
                    exchange.getResponseHeaders().set("Cached-Response","true");
                    sendResponse(exchange, 200,cacheRespnose);
                }else{
                    exchange.getResponseHeaders().set("Cached-Response","false");
                    byte[] response = giveToLeader(requestBody);
                    cache.put(requestHash,response);
                    sendResponse(exchange,200,response);
                }

            }catch(Exception e){
                logger.severe("Error handling request: " + e.getMessage());
                sendResponse(exchange,400,("Server eror: " + e.getMessage()).getBytes());
            }
        }

        private void sendResponse(HttpExchange ex, int code, byte[] res) throws IOException{
            ex.sendResponseHeaders(code,res.length);
            try(OutputStream os = ex.getResponseBody()){
                os.write(res);
                os.flush();
            }
        }

        private byte[] giveToLeader(byte[] request) throws IOException{
            if(peerServer.getCurrentLeader() == null){
                logger.severe("No leader, current state: " + peerServer.getState());
                throw new IllegalStateException("No leader");
            }

            InetSocketAddress leaderAddress = peerServer.getPeerByID(peerServer.getCurrentLeader().getProposedLeaderID());
            if(leaderAddress == null){
                throw new IllegalStateException("Leader not in peer list");
            }

            String leaderHost = leaderAddress.getHostString();
            int leaderPort = leaderAddress.getPort() + 2; //TCP

            logger.fine("Connectin to leader at: " + leaderAddress + "(TCP port: " + leaderPort + ")");

            try(Socket socket = new Socket(leaderHost,leaderPort)) {
                Message msg = new Message(Message.MessageType.WORK,request,InetAddress.getLocalHost().getHostAddress(),this.port,leaderHost,leaderPort);

                OutputStream os = socket.getOutputStream();
                os.write(msg.getNetworkPayload());
                os.flush();

                InputStream is = socket.getInputStream();
                byte[] response = Util.readAllBytesFromNetwork(is);
                Message responseMsg = new Message(response);

                return responseMsg.getMessageContents();
            }
        }
    }
}
