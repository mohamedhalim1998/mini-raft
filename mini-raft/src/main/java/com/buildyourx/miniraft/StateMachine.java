package com.buildyourx.miniraft;

/**
 * The application state machine that consumes committed log entries.
 *
 * <p>Raft's guarantee is that every node applies the same committed commands
 * in the same order, so if the state machine is deterministic every node
 * reaches an identical state. The Raft layer calls {@link #apply(String)}
 * exactly once per committed entry, in strictly increasing index order.
 *
 * <p>For learning, a trivial key-value state machine (commands like
 * {@code "SET x 1"}) is sufficient. Snapshot support is used in Phase 5.
 */
public interface StateMachine {

    // --- Phase 3: Applying Committed Entries ---

    /**
     * Apply a single committed command to the state machine. Called exactly
     * once per committed entry, in index order.
     *
     * @param command the command from a committed {@link LogEntry}
     * @return an optional result to return to the client (may be null)
     */
    String apply(String command);

    // --- Phase 5: Snapshot Support ---

    /**
     * Serialize the entire current state so it can be captured in a snapshot.
     *
     * @return the serialized state
     */
    byte[] takeSnapshot();

    /**
     * Replace the entire current state with the contents of a snapshot.
     *
     * @param snapshot the serialized state produced by {@link #takeSnapshot()}
     */
    void restoreSnapshot(byte[] snapshot);
}
