package com.buildyourx.miniraft;

/**
 * The role a Raft server plays at any given moment.
 *
 * <p>A server transitions between these states according to the Raft
 * protocol: a {@link #FOLLOWER} that times out becomes a {@link #CANDIDATE};
 * a candidate that wins a majority becomes a {@link #LEADER}; any server that
 * observes a higher term reverts to {@link #FOLLOWER}.
 *
 * <p>Used across all phases.
 */
public enum RaftState {

    /**
     * Passive state. Responds to RequestVote and AppendEntries RPCs but
     * issues none of its own. Converts to {@link #CANDIDATE} if it receives
     * no valid heartbeat within its election timeout.
     */
    FOLLOWER,

    /**
     * Actively seeking election. Has voted for itself and is collecting
     * votes from peers. Becomes {@link #LEADER} on a majority, reverts to
     * {@link #FOLLOWER} if it discovers a current leader or higher term,
     * or starts a new election on a split vote.
     */
    CANDIDATE,

    /**
     * Handles all client requests and drives log replication via
     * AppendEntries. At most one leader exists per term.
     */
    LEADER
}
