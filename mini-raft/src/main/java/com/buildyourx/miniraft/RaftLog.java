package com.buildyourx.miniraft;

import java.util.List;

/**
 * The replicated log — an ordered sequence of {@link LogEntry} records.
 *
 * <p>The log is 1-indexed to match the Raft paper (index 0 is a conceptual
 * empty sentinel with term 0). Implementations back this by stable storage
 * so entries survive restarts (see {@link StableStorage}).
 *
 * <p>The core invariant this type helps maintain is the <b>Log Matching
 * Property</b>: if two logs contain an entry with the same index and term,
 * the logs are identical in all preceding entries.
 */
public interface RaftLog {

    // --- Phase 2: Basic Log Operations ---

    /**
     * Append entries to the end of the log.
     *
     * @param entries the entries to append, in index order
     */
    void append(List<LogEntry> entries);

    /**
     * Get the entry at the given 1-based index.
     *
     * @param index the 1-based log index
     * @return the entry, or null if no entry exists at that index
     */
    LogEntry entryAt(long index);

    /**
     * Get the term of the entry at the given index.
     *
     * @param index the 1-based log index
     * @return the term at that index, or 0 if the index is 0 or refers to a
     *         snapshot boundary with no in-log entry
     */
    long termAt(long index);

    /**
     * Get the index of the last entry in the log.
     *
     * @return the last index, or 0 if the log is empty
     */
    long lastIndex();

    /**
     * Get the term of the last entry in the log.
     *
     * @return the last term, or 0 if the log is empty
     */
    long lastTerm();

    /**
     * Get all entries starting from the given index (inclusive) to the end.
     * Used by the leader to build AppendEntries payloads.
     *
     * @param fromIndex the 1-based start index (inclusive)
     * @return the entries from {@code fromIndex} onward, or an empty list
     */
    List<LogEntry> entriesFrom(long fromIndex);

    /**
     * Delete the entry at {@code fromIndex} and everything after it.
     * Used when a follower must overwrite a conflicting suffix so its log
     * converges with the leader's.
     *
     * @param fromIndex the 1-based index at which truncation begins (inclusive)
     */
    void truncateFrom(long fromIndex);

    // --- Phase 5: Compaction ---

    /**
     * Discard all entries up to and including {@code lastIncludedIndex}
     * because they are now captured by a snapshot.
     *
     * @param lastIncludedIndex the last index covered by the snapshot
     * @param lastIncludedTerm  the term of {@code lastIncludedIndex}
     */
    void compact(long lastIncludedIndex, long lastIncludedTerm);

    /**
     * The index immediately before the first entry still held in the log
     * (i.e. the {@code lastIncludedIndex} of the most recent snapshot, or 0).
     *
     * @return the offset established by compaction
     */
    long snapshotIndex();
}
