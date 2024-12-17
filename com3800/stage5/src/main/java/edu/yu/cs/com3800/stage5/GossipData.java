package edu.yu.cs.com3800.stage5;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GossipData {

    private final ConcurrentHashMap<Long,Long> heartbeats;
    private final ConcurrentHashMap<Long,Long> lastHeartbeatTime;
    private final ConcurrentHashMap<Long,Boolean> failedNodes;

    public GossipData() {
        heartbeats = new ConcurrentHashMap<>();
        lastHeartbeatTime = new ConcurrentHashMap<>();
        failedNodes = new ConcurrentHashMap<>();
    }

    public synchronized void updateHeartbeat(long nodeId) {
        if(isNodeFailed(nodeId)) {
            return;
        }
        heartbeats.compute(nodeId, (key, val) -> val == null ? 1L : val + 1);
        lastHeartbeatTime.put(nodeId, System.currentTimeMillis());

    }

    public synchronized void updateFromGossip(long nodeId, long heartbeat, long timestamp) {
        if(isNodeFailed(nodeId)) {
            return;
        }
        Long currentHeartbeat = heartbeats.getOrDefault(nodeId, -1L);
        if(heartbeat > currentHeartbeat) {
            heartbeats.put(nodeId, heartbeat);
            lastHeartbeatTime.put(nodeId, timestamp);
        }
    }

    public synchronized void markNodeFailed(long nodeId) {
        failedNodes.put(nodeId, true);
        heartbeats.remove(nodeId);
        lastHeartbeatTime.remove(nodeId);
    }

    public synchronized boolean isNodeFailed(long nodeId) {
        return failedNodes.getOrDefault(nodeId, false);
    }

    public synchronized Map<Long,Long> getHeartbeats() {
        return new ConcurrentHashMap<>(heartbeats);
    }

    public synchronized Long getLastHeartbeatTime(Long nodeId) {
        return lastHeartbeatTime.get(nodeId);
    }

    public synchronized Long getHeartbeat(Long nodeId) {
        return heartbeats.get(nodeId);
    }

    public synchronized Map<Long,Boolean> getFailedNodes() {
        return new ConcurrentHashMap<>(failedNodes);
    }
}
