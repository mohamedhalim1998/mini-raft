package com.buildyourx.miniraft;

import java.util.*;

public class Cluster implements ClusterConfiguration {
    private Map<String, RaftNode> nodes = new HashMap<>();
    private Map<String, RaftNode> partioned = new HashMap<>();

    public Cluster(int numOfNodes) {
        for (int i = 0; i < numOfNodes; i++) {
            var id = UUID.randomUUID().toString();
            var node = new RaftNodeImpl(id, this);
            node.run();
            nodes.put(id, node);
        }
    }

    public Cluster(String... nodeIds) {
        for (var id : nodeIds) {
            var node = new RaftNodeImpl(id, this);
            node.run();
            nodes.put(id, node);
        }
    }

    public void sendToAll(String id, RpcMessage message) {
        var sender = nodes.get(id);
        for (var node : nodes.values()) {
            if (node.id().equals(id)) {
                continue;
            }
            if (message instanceof RpcMessage.RequestVoteRequest request) {
                sender.handleRequestVoteReply(node.handleRequestVote(request), node.id());
            } else if(message instanceof RpcMessage.AppendEntriesRequest request) {
                node.handleAppendEntries(request);
            }
        }
    }

    public RaftNode node(String id) {
        return nodes.get(id);
    }

    public void tickAll(int ms) {
        nodes.values().forEach(n -> n.tick(ms));
    }

    public List<RaftNode> leaders() {
        return nodes.values().stream().filter(n -> RaftState.LEADER.equals(n.state())).toList();
    }

    public void partition(String id) {
        var node = nodes.remove(id);
        node.pause();
        partioned.put(id, node);
    }

    @Override
    public Set<String> voters() {
        return nodes.keySet();
    }

    @Override
    public Set<String> learners() {
        return Set.of();
    }

    @Override
    public boolean isQuorum(Set<String> votes) {
        return votes.size() >= voters().size() / 2;
    }

    @Override
    public boolean isJoint() {
        return false;
    }

    public void reconnect(String id) {
        nodes.put(id, partioned.remove(id));
    }
}
