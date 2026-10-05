package com.buildyourx.miniraft;

import java.util.Set;

/**
 * The set of servers that make up the cluster at a point in time.
 *
 * <p>Configuration is itself stored in the log as special entries. A change
 * takes effect the moment its entry is <b>appended</b> (not when committed),
 * and a node always uses the latest configuration present in its log. This
 * type distinguishes <b>voters</b> (count toward quorum) from <b>learners</b>
 * (replicate the log but do not vote), which lets a new server catch up before
 * it affects majority calculations.
 *
 * <p>Introduced in Phase 5.
 */
public interface ClusterConfiguration {

    /**
     * The voting members of the cluster. A quorum is a majority of this set.
     *
     * @return the set of voter node ids
     */
    Set<String> voters();

    /**
     * The non-voting members (learners) that receive log entries but are not
     * counted when computing majorities.
     *
     * @return the set of learner node ids
     */
    Set<String> learners();

    /**
     * The number of votes required to form a quorum for the current
     * configuration ({@code floor(voters/2) + 1}). Under joint consensus this
     * must be satisfied for BOTH the old and new voter sets simultaneously.
     *
     * @param votes the set of node ids that granted a vote / acknowledged
     * @return true if {@code votes} constitutes a majority of the voters
     */
    boolean isQuorum(Set<String> votes);

    /**
     * Whether this configuration is a transitional <b>joint consensus</b>
     * configuration (C-old,new), in which agreement requires majorities from
     * both the old and new voter sets.
     *
     * @return true if this is a joint (transitional) configuration
     */
    boolean isJoint();
}
