package com.buildyourx.miniraft;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RaftNodeImpl implements RaftNode, Runnable {
    private final String id;
    private RaftState state;
    private long timeout;
    private long term;
    private String votedFor;
    private final Cluster cluster;
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> timeOutTask;
    private ScheduledFuture<?> heartbeatTask;

    private final Set<String> votes;

    public RaftNodeImpl(String id, Cluster cluster) {
        this.id = id;
        this.state = RaftState.FOLLOWER;
        this.timeout = RandomUtils.heartbeatInterval();
        this.term = 0;
        this.cluster = cluster;
        this.votes = new HashSet<>();
    }


    @Override
    public String id() {
        return id;
    }

    @Override
    public RaftState state() {
        return state;
    }

    @Override
    public long currentTerm() {
        return term;
    }

    @Override
    public void tick(long elapsedMillis) {
        timeout -= elapsedMillis;
        System.out.printf("%s tick by %d remaining %d\n", id, elapsedMillis, timeout);
        if (state == RaftState.LEADER) {
            sendHeartBeat();
        } else if (timeout < 0) {
            runCycle();
        }
    }

    @Override
    public RpcMessage.RequestVoteReply handleRequestVote(RpcMessage.RequestVoteRequest request) {
        System.out.printf("%s vote request from %s for term %d my term %d\n", id, request.candidateId(), request.term(), term);

        var voted = false;
        if (request.term() >= term
                && votedFor == null) {
            System.out.printf("%s vote request from %s for term %d my term %d voted for him\n", id, request.candidateId(), request.term(), term);
            votedFor = request.candidateId();
            this.term = request.term();
            voted = true;
            resetTimeOut();
        }
        return new RpcMessage.RequestVoteReply(term, voted);
    }

    private void resetTimeOut() {
        cancel(timeOutTask);
        timeout = RandomUtils.heartbeatInterval();
        var jobId = UUID.randomUUID().toString();
        System.out.printf("%s: reset timeout %d %s\n", id, timeout, jobId);
        timeOutTask = scheduler.schedule(() -> {
            System.out.printf("%s running JOB %s\n", id, jobId);
            runCycle();
        }, timeout, TimeUnit.MILLISECONDS);
    }

    private void cancel(ScheduledFuture<?> task) {
        if (task != null) {
            System.out.printf("%s: cancel task\n", id);
            task.cancel(true);
        }
    }

    @Override
    public void handleRequestVoteReply(RpcMessage.RequestVoteReply reply, String from) {
        if (reply.voteGranted()) {
            votes.add(from);
        }
        if (configuration().isQuorum(votes) && state == RaftState.CANDIDATE) {
            System.out.printf("%s switch to leader for term %d\n", id, term);
            this.state = RaftState.LEADER;
            this.votedFor = null;
            this.votes.clear();
            heartbeatTask = scheduler.scheduleAtFixedRate(this::sendHeartBeat, 0, 50, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean submitCommand(String command) {
        return false;
    }

    @Override
    public RpcMessage.AppendEntriesReply handleAppendEntries(RpcMessage.AppendEntriesRequest request) {
        if (request.entries() == null || request.entries().isEmpty()) {
            return handleHeartbeat(request);
        }
        return new RpcMessage.AppendEntriesReply(
                request.term(),
                true,
                0
        );
    }

    private RpcMessage.AppendEntriesReply handleHeartbeat(RpcMessage.AppendEntriesRequest request) {
        if (term < request.term()) {
            term = request.term();
            state = RaftState.FOLLOWER;
            if (heartbeatTask != null) heartbeatTask.cancel(true);
        }
        votedFor = null;
        resetTimeOut();
        return new RpcMessage.AppendEntriesReply(
                request.term(),
                true,
                0
        );
    }

    @Override
    public long commitIndex() {
        return 0;
    }

    @Override
    public long lastApplied() {
        return 0;
    }

    @Override
    public ClusterConfiguration configuration() {
        return cluster;
    }

    @Override
    public boolean addServer(String serverId, boolean asLearner) {
        return false;
    }

    @Override
    public boolean removeServer(String serverId) {
        return false;
    }

    @Override
    public RpcMessage.InstallSnapshotReply handleInstallSnapshot(RpcMessage.InstallSnapshotRequest request) {
        return null;
    }

    @Override
    public Snapshot takeSnapshot() {
        return null;
    }

    @Override
    public Set<String> peers() {
        return Set.of();
    }

    @Override
    public void pause() {
        System.out.printf("%s pause everything\n", id);
        cancel(heartbeatTask);
        cancel(timeOutTask);
    }

    @Override
    public void run() {
        timeOutTask = scheduler.schedule(() -> {
            System.out.printf("%s running JOB %s\n", id, "STARTUP");
            runCycle();
        }, timeout, TimeUnit.MILLISECONDS);
    }

    private void runCycle() {
        if (state == RaftState.FOLLOWER) {
            startElection();
        }
    }

    private void sendHeartBeat() {
        System.out.printf("%s sending heart beat I am the leader\n", id);
        cluster.sendToAll(
                id,
                new RpcMessage.AppendEntriesRequest(
                        term,
                        id,
                        0,
                        0,
                        List.of(),
                        0
                )
        );
    }

    private void startElection() {
        this.state = RaftState.CANDIDATE;
        this.term++;
        System.out.printf("%s switch to candidate for term %d\n", id, term);
        cluster.sendToAll(id, new RpcMessage.RequestVoteRequest(
                term,
                id,
                commitIndex(),
                lastApplied()
        ));
    }
}
