package com.buildyourx.miniraft;

/**
 * Pluggable message transport between Raft nodes.
 *
 * <p>To keep the focus on the consensus algorithm rather than networking,
 * a node sends and receives {@link RpcMessage}s through this abstraction.
 * Tests supply a deterministic in-memory transport that lets them deliver
 * (or drop, delay, reorder, partition) messages by hand, making election
 * and replication scenarios fully reproducible.
 *
 * <p>Used across all phases.
 */
public interface Transport {

    /**
     * Send a message to a specific peer. Delivery may be asynchronous,
     * dropped, or delayed depending on the implementation (e.g. to simulate
     * a network partition).
     *
     * @param destinationId the target node id
     * @param message       the RPC request or reply to deliver
     */
    void send(String destinationId, RpcMessage message);

    /**
     * Register the node that should receive messages delivered to this
     * transport endpoint. The transport invokes the node's RPC handlers when
     * a message arrives.
     *
     * @param node the local node bound to this transport
     */
    void register(RaftNode node);
}
