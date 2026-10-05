package com.buildyourx.miniraft;

/**
 * A single entry in the replicated log.
 *
 * <p>Each entry carries the state-machine {@code command} to execute, the
 * {@code term} in which the leader created it, and its {@code index}
 * (1-based position in the log). The (index, term) pair uniquely identifies
 * an entry for the purposes of the Log Matching Property: if two logs contain
 * an entry with the same index and term, those entries — and all entries
 * before them — are identical.
 *
 * <p>Used from Phase 2 onward.
 *
 * @param index   the 1-based position of this entry in the log
 * @param term    the term in which the leader created this entry
 * @param command the opaque state-machine command this entry represents
 */
public record LogEntry(long index, long term, String command) {
}
