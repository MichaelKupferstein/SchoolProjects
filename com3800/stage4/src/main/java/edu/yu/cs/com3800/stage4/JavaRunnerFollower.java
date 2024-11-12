package edu.yu.cs.com3800.stage4;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.PeerServer;
import static edu.yu.cs.com3800.Message.MessageType.COMPLETED_WORK;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class JavaRunnerFollower extends Thread implements LoggingServer {
    private PeerServer myServer;
    private JavaRunner javaRunner;
    private volatile boolean shutdown;
    private static Logger logger;


    public JavaRunnerFollower(PeerServer myServer) throws IOException {
        this.myServer = myServer;
        this.javaRunner = new JavaRunner();
        this.logger = initializeLogging(JavaRunnerFollower.class.getCanonicalName() + "-on-port-" + this.myServer.getUdpPort());
        setDaemon(true);
        logger.fine("JavaRunnerFollower initialized on port: " + myServer.getUdpPort() + " from PeerServer: " + myServer.getServerId());
    }

    public void shutdown(){
        this.shutdown = true;
        interrupt();
    }

    public void work(Message message){
        logger.fine("JavaRunnerFollower received message");


        InetSocketAddress leaderAddress = myServer.getPeerByID(myServer.getCurrentLeader().getProposedLeaderID());
        long requestID = message.getRequestID();

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(message.getMessageContents());
            String res = javaRunner.compileAndRun(bais);
            logger.fine("JavaRunnerFollower compiled and ran code");
            Message response = new Message(COMPLETED_WORK, res.getBytes(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), leaderAddress.getHostString(), leaderAddress.getPort(), message.getRequestID());
            myServer.sendMessage(response.getMessageType(), response.getNetworkPayload(), leaderAddress);
        } catch (Exception e) {
            logger.severe("JavaRunnerFollower failed to compile and run code");
            Message errorMessage = new Message(COMPLETED_WORK, e.getMessage().getBytes(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), leaderAddress.getHostString(), leaderAddress.getPort(), message.getRequestID());
            myServer.sendMessage(errorMessage.getMessageType(), errorMessage.getNetworkPayload(), leaderAddress);
        }
    }

}
