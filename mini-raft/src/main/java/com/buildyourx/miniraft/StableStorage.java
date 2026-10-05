package com.buildyourx.miniraft;

import java.util.List;

/**
 * Durable storage for the state that Raft <b>must</b> persist before
 * responding to any RPC.
 *
 * <p>Per the Raft paper, three pieces of state are persistent:
 * {@code currentTerm}, {@code votedFor}, and the {@code log}. Persisting
 * these before acknowledging a vote or an append is essential for safety:
 * a node that acknowledges then crashes without persisting could vote twice
 * in a term or lose committed entries.
 *
 * <p>Volatile state ({@code commitIndex}, {@code lastApplied}) is intentionally
 * NOT stored here — it is safely recomputed after a restart.
 *
 * <p>Introduced in Phase 4. Tests may supply an in-memory implementation whose
 * contents survive a simulated "restart" (a new {@link RaftNode} instance
 * constructed over the same storage).
 */
public interface StableStorage {

    /**
     * Persist the current term.
     *
     * @param term the latest term the server has seen
     */
    void saveCurrentTerm(long term);

    /**
     * Read the persisted current term.
     *
     * @return the persisted term, or 0 if none was ever stored
     */
    long readCurrentTerm();

    /**
     * Persist the candidate this server voted for in the current term.
     *
     * @param candidateId the voted-for candidate, or null if none
     */
    void saveVotedFor(String candidateId);

    /**
     * Read the persisted vote for the current term.
     *
     * @return the voted-for candidate, or null if the server has not voted
     */
    String readVotedFor();

    /**
     * Persist the full log. Implementations may optimize to append-only,
     * but the contract is that after this call the log is durable.
     *
     * @param entries the complete log to persist
     */
    void saveLog(List<LogEntry> entries);

    /**
     * Read the persisted log.
     *
     * @return the persisted entries in index order, or an empty list
     */
    List<LogEntry> readLog();

    /**
     * Persist a state-machine snapshot together with the log position it
     * covers. Introduced in Phase 5.
     *
     * @param lastIncludedIndex the last log index captured by the snapshot
     * @param lastIncludedTerm  the term of {@code lastIncludedIndex}
     * @param snapshot          the serialized snapshot bytes
     */
    void saveSnapshot(long lastIncludedIndex, long lastIncludedTerm, byte[] snapshot);

    /**
     * Read the most recently persisted snapshot.
     *
     * @return the snapshot, or null if none has been stored
     */
    Snapshot readSnapshot();
}
