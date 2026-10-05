package com.buildyourx.miniraft;

/**
 * A point-in-time capture of the state machine used for log compaction.
 *
 * <p>A snapshot records the state machine's serialized state along with the
 * log position it covers ({@code lastIncludedIndex} and
 * {@code lastIncludedTerm}). Once a snapshot exists, all log entries through
 * {@code lastIncludedIndex} can be discarded. A far-behind follower is caught
 * up by transferring a snapshot via
 * {@link RpcMessage.InstallSnapshotRequest}.
 *
 * <p>Introduced in Phase 5.
 *
 * @param lastIncludedIndex the last log index captured by this snapshot
 * @param lastIncludedTerm  the term of {@code lastIncludedIndex}
 * @param state             the serialized state-machine contents
 */
public record Snapshot(long lastIncludedIndex, long lastIncludedTerm, byte[] state) {
}
