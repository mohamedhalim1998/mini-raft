package com.buildyourx.miniraft;

import java.util.Set;

/**
 * A single Raft server — the consensus module that drives one node through
 * elections, log replication, commitment, persistence, and membership changes.
 *
 * <p>This is the primary entry point of mini-raft. A node is driven by two
 * kinds of events: <b>timers</b> (the election timeout on followers/candidates
 * and the heartbeat interval on the leader) and <b>incoming RPCs</b>. To keep
 * tests deterministic, time is advanced explicitly via {@link #tick(long)}
 * rather than by real sleeping, and RPCs are delivered through a
 * {@link Transport}.
 *
 * <p>Implementations must uphold the five Raft safety properties: Election
 * Safety, Leader Append-Only, Log Matching, Leader Completeness, and State
 * Machine Safety.
 */
public interface RaftNode {

    /**
     * This node's unique identifier within the cluster.
     *
     * @return the node id
     */
    String id();

    // --- Phase 1: Leader Election ---

    /**
     * The node's current role.
     *
     * @return {@link RaftState#FOLLOWER}, {@link RaftState#CANDIDATE}, or
     *         {@link RaftState#LEADER}
     */
    RaftState state();

    /**
     * The node's current term.
     *
     * @return the current term
     */
    long currentTerm();

    /**
     * Advance this node's logical clock by {@code elapsedMillis}. Drives the
     * election timeout (followers/candidates start a new election when it
     * expires) and the heartbeat interval (a leader emits heartbeats). Called
     * by tests to deterministically trigger timeouts.
     *
     * @param elapsedMillis milliseconds of simulated time to advance
     */
    void tick(long elapsedMillis);

    /**
     * Handle an incoming RequestVote RPC from a candidate.
     *
     * <p>Grant the vote iff: (1) the request's term is not stale, (2) this
     * node has not already voted for a different candidate this term, and
     * (3) the candidate's log is at least as up-to-date as this node's
     * (the Election Restriction, enforced from Phase 3).
     *
     * @param request the candidate's vote request
     * @return the vote reply carrying this node's term and the decision
     */
    RpcMessage.RequestVoteReply handleRequestVote(RpcMessage.RequestVoteRequest request);

    /**
     * Handle an incoming RequestVoteReply RPC response from a peer.
     *
     * <p>Process the peer's voting decision: (1) if the reply's term is higher
     * than this node's current term, step down to a follower immediately, and
     * (2) if the vote was granted and this node is still a candidate, increment
     * the vote count and transition to leader if a majority quorum is reached.
     *
     * @param reply the vote reply received from a peer node
     * @param from the node ID of the peer that sent the reply
     */
    void handleRequestVoteReply(RpcMessage.RequestVoteReply reply, String from);

    // --- Phase 2: Log Replication ---

    /**
     * Submit a client command to be replicated. Only meaningful on the leader;
     * a follower/candidate should reject or redirect. The command is appended
     * to the leader's log and replicated to followers via AppendEntries.
     *
     * @param command the state-machine command to replicate
     * @return true if accepted for replication (this node is the leader);
     *         false otherwise
     */
    boolean submitCommand(String command);

    /**
     * Handle an incoming AppendEntries RPC (also used as a heartbeat).
     *
     * <p>Reject if the term is stale or if the log does not contain a matching
     * entry at {@code prevLogIndex}/{@code prevLogTerm}. On success, append any
     * new entries (overwriting a conflicting suffix) and advance the local
     * commit index toward {@code leaderCommit} (from Phase 3).
     *
     * @param request the leader's append request
     * @return the reply carrying this node's term and success flag
     */
    RpcMessage.AppendEntriesReply handleAppendEntries(RpcMessage.AppendEntriesRequest request);

    // --- Phase 3: Safety & Commit Rules ---

    /**
     * The highest log index known to be committed on this node.
     *
     * @return the commit index (0 if nothing is committed yet)
     */
    long commitIndex();

    /**
     * The highest log index that has been applied to the state machine.
     * Always {@code <= commitIndex()}.
     *
     * @return the last-applied index
     */
    long lastApplied();

    // --- Phase 5: Membership Changes & Snapshots ---

    /**
     * The current cluster configuration (voters and learners) as seen by this
     * node, derived from the latest configuration entry in its log.
     *
     * @return the current cluster configuration
     */
    ClusterConfiguration configuration();

    /**
     * Propose adding a server to the cluster. Implementations should use a
     * safe change mechanism (single-server change, or joint consensus for
     * multi-server changes) so that no two disjoint majorities can form during
     * the transition. New servers may join as learners first to catch up.
     *
     * @param serverId the id of the server to add
     * @param asLearner true to add as a non-voting learner initially
     * @return true if the configuration change was proposed (this node is leader)
     */
    boolean addServer(String serverId, boolean asLearner);

    /**
     * Propose removing a server from the cluster.
     *
     * @param serverId the id of the server to remove
     * @return true if the configuration change was proposed (this node is leader)
     */
    boolean removeServer(String serverId);

    /**
     * Handle an incoming InstallSnapshot RPC. Used when this node has fallen
     * so far behind that the leader has already discarded the log entries it
     * needs. Installs the snapshot into the state machine, discards a
     * conflicting log prefix, and resumes normal replication.
     *
     * @param request the leader's snapshot request
     * @return the reply carrying this node's term
     */
    RpcMessage.InstallSnapshotReply handleInstallSnapshot(RpcMessage.InstallSnapshotRequest request);

    /**
     * Trigger creation of a snapshot up to {@link #lastApplied()}, compacting
     * the log by discarding the entries the snapshot now covers.
     *
     * @return the snapshot that was taken
     */
    Snapshot takeSnapshot();

    /**
     * The set of peer node ids this node knows about (all cluster members
     * except itself).
     *
     * @return the peer ids
     */
    Set<String> peers();

    void pause();
}
