package com.buildyourx.miniraft;

import java.util.List;

/**
 * The wire messages exchanged between Raft servers.
 *
 * <p>This is a sealed hierarchy covering the three Raft RPCs (each with a
 * request and a reply) plus their arguments and results, mirroring Figure 2
 * of the Raft paper. Use pattern matching to dispatch on message type.
 *
 * <ul>
 *   <li><b>RequestVote</b> — Phase 1: used by candidates to gather votes.</li>
 *   <li><b>AppendEntries</b> — Phase 2/3: used by the leader to replicate log
 *       entries and as a heartbeat; carries {@code leaderCommit} for commit
 *       propagation.</li>
 *   <li><b>InstallSnapshot</b> — Phase 5: used by the leader to bring a
 *       lagging follower up to date when the needed log entries have already
 *       been discarded by compaction.</li>
 * </ul>
 */
public sealed interface RpcMessage {

    /**
     * The sender's current term. Every RPC carries a term so receivers can
     * apply the universal rule: higher term wins (revert to follower),
     * lower term is rejected.
     *
     * @return the term attached to this message
     */
    long term();

    // --- Phase 1: Leader Election ---

    /**
     * Invoked by candidates to gather votes.
     *
     * @param term         the candidate's term
     * @param candidateId  the candidate requesting the vote
     * @param lastLogIndex index of the candidate's last log entry
     * @param lastLogTerm  term of the candidate's last log entry
     */
    record RequestVoteRequest(long term, String candidateId,
                              long lastLogIndex, long lastLogTerm)
            implements RpcMessage {
    }

    /**
     * Reply to a {@link RequestVoteRequest}.
     *
     * @param term        the receiver's current term, for the candidate to update itself
     * @param voteGranted true if the candidate received the vote
     */
    record RequestVoteReply(long term, boolean voteGranted)
            implements RpcMessage {
    }

    // --- Phase 2/3: Log Replication & Commit ---

    /**
     * Invoked by the leader to replicate log entries; also used as a
     * heartbeat (with an empty {@code entries} list).
     *
     * @param term         the leader's term
     * @param leaderId     so the follower can redirect clients
     * @param prevLogIndex index of the log entry immediately preceding the new ones
     * @param prevLogTerm  term of the {@code prevLogIndex} entry
     * @param entries      log entries to store (empty for heartbeat)
     * @param leaderCommit the leader's {@code commitIndex}
     */
    record AppendEntriesRequest(long term, String leaderId,
                                long prevLogIndex, long prevLogTerm,
                                List<LogEntry> entries, long leaderCommit)
            implements RpcMessage {
    }

    /**
     * Reply to an {@link AppendEntriesRequest}.
     *
     * <p>{@code matchIndex} is a convenience for the leader to update its
     * per-follower {@code matchIndex[]} on success. Implementations may also
     * use it (or additional fields) to accelerate {@code nextIndex} backtracking.
     *
     * @param term       the receiver's current term, for the leader to update itself
     * @param success    true if the follower's log matched {@code prevLogIndex}/{@code prevLogTerm}
     * @param matchIndex highest log index known to be replicated on success
     */
    record AppendEntriesReply(long term, boolean success, long matchIndex)
            implements RpcMessage {
    }

    // --- Phase 5: Snapshots ---

    /**
     * Invoked by the leader to send a snapshot to a follower that has fallen
     * too far behind (the needed entries have already been discarded).
     *
     * @param term              the leader's term
     * @param leaderId          so the follower can redirect clients
     * @param lastIncludedIndex the snapshot replaces all entries up through this index
     * @param lastIncludedTerm  the term of {@code lastIncludedIndex}
     * @param snapshot          the serialized state-machine snapshot
     */
    record InstallSnapshotRequest(long term, String leaderId,
                                  long lastIncludedIndex, long lastIncludedTerm,
                                  byte[] snapshot)
            implements RpcMessage {
    }

    /**
     * Reply to an {@link InstallSnapshotRequest}.
     *
     * @param term the receiver's current term, for the leader to update itself
     */
    record InstallSnapshotReply(long term) implements RpcMessage {
    }
}
