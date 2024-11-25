package edu.yu.cs.com3800.stage4;

import com.sun.net.httpserver.HttpServer;
import edu.yu.cs.com3800.LoggingServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class GatewayServer implements LoggingServer {

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

    }

}
