package phase3;

import com.buildyourx.miniraft.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("phase3")
@DisplayName("Safety & Commit Rules")
class SafetyAndCommitTest {

    // TODO: Replace with your implementation.
    // private TestCluster cluster;
    // private RaftNode leader;
    //
    // @BeforeEach
    // void setUp() {
    //     cluster = new TestCluster("n1", "n2", "n3");
    //     cluster.node("n1").tick(400);
    //     cluster.deliverAll();
    //     leader = cluster.node("n1");
    // }

    @Test
    @DisplayName("should commit an entry once it is replicated on a majority")
    void shouldCommitOnMajority() {
        // leader.submitCommand("SET x 1");
        // cluster.deliverAll();
        // assertThat(leader.commitIndex()).isEqualTo(1);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should apply committed entries to the state machine in order")
    void shouldApplyInOrder() {
        // leader.submitCommand("SET x 1");
        // leader.submitCommand("SET y 2");
        // cluster.deliverAll();
        // assertThat(leader.lastApplied()).isEqualTo(2);
        // assertThat(cluster.stateMachine("n1").apply("GET x")).isEqualTo("1");
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should propagate leaderCommit so followers apply the same entries")
    void shouldPropagateCommitToFollowers() {
        // leader.submitCommand("SET x 1");
        // cluster.deliverAll(); // replicate
        // cluster.deliverAll(); // next heartbeat carries leaderCommit
        // assertThat(cluster.node("n2").lastApplied()).isEqualTo(1);
        // assertThat(cluster.node("n3").lastApplied()).isEqualTo(1);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should not grant a vote to a candidate with a less up-to-date log")
    void shouldEnforceElectionRestriction() {
        // Give n2 a longer/more-recent log than candidate n3, then n3 requests
        // a vote with a stale lastLogIndex/lastLogTerm.
        // var reply = cluster.node("n2").handleRequestVote(
        //         new RpcMessage.RequestVoteRequest(5, "n3", 0, 0));
        // assertThat(reply.voteGranted()).isFalse();
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should grant a vote when the candidate's log is at least as up-to-date")
    void shouldGrantVoteToUpToDateCandidate() {
        // A candidate whose lastLogTerm is higher (or equal term but log at
        // least as long) must be granted the vote.
        // ...
        // assertThat(reply.voteGranted()).isTrue();
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should NOT commit an entry from a previous term via majority alone (Figure 8)")
    void shouldNotCommitPreviousTermByCountingReplicas() {
        // Recreate the paper's Figure 8 scenario: an entry from an earlier term
        // is present on a majority, but must not be considered committed until
        // an entry from the current term is committed on top of it.
        // assertThat(leader.commitIndex()).isLessThan(indexOfOldTermEntry);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should commit prior-term entries indirectly once a current-term entry commits")
    void shouldCommitPriorEntriesIndirectly() {
        // After a current-term entry reaches a majority, all preceding entries
        // (including older-term ones) become committed.
        // assertThat(leader.commitIndex()).isEqualTo(indexOfCurrentTermEntry);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should preserve committed entries across a leader change (Leader Completeness)")
    void shouldPreserveCommittedEntriesAcrossLeaderChange() {
        // Commit an entry, fail the leader, elect a new leader, and assert the
        // committed entry is still present in the new leader's log.
        // ...
        // assertThat(cluster.currentLeader().commitIndex()).isGreaterThanOrEqualTo(1);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("no two nodes should apply a different command at the same index (State Machine Safety)")
    void shouldNeverApplyDifferentCommandAtSameIndex() {
        // Drive several elections and replications under partitions, then
        // assert every node's applied log agrees index-by-index.
        // assertThat(cluster.appliedLog("n2")).isEqualTo(cluster.appliedLog("n3"));
        fail("Implement RaftNode and uncomment this test");
    }
}
