package edu.yu.cs.com3800.stage4;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class GatewayServer extends Thread implements LoggingServer {

    private HttpServer httpServer;
    private GatewayPeerServerImpl peerServer;
    private int httpPort;
    private ConcurrentHashMap<Integer, byte[]> cache;
    private Logger logger;

    public GatewayServer(int httpPort, int peerPort, long peerEpoch, Long serverID,
                         ConcurrentHashMap<Long, InetSocketAddress> peerIDtoAdress, int numberOfObservers) throws IOException{
        this.httpPort = httpPort;
        this.httpServer = HttpServer.create(new InetSocketAddress(httpPort),0); //create an HTTPServer on...
        this.cache = new ConcurrentHashMap<>();
        this.logger = initializeLogging(GatewayServer.class.getCanonicalName() + "-on-port-" + this.httpPort);
        this.peerServer = new GatewayPeerServerImpl(peerPort, peerEpoch,serverID,peerIDtoAdress,numberOfObservers);

        this.httpServer.createContext("/compileandrun", new CompileAndRunHandler());
    }




    private class CompileAndRunHandler implements HttpHandler{
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
            OutputStream os = ex.getResponseBody();
            try{
                os.write(res);
            }finally {
                os.close();
            }
        }

        private byte[] giveToLeader(byte[] request){
            //TODO: send to leader with TCP
            return null;
        }
    }
}
