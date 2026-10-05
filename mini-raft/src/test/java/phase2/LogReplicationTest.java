package phase2;

import com.buildyourx.miniraft.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Tag("phase2")
@DisplayName("Log Replication")
class LogReplicationTest {

    // TODO: Replace with your implementation.
    // private TestCluster cluster;
    // private RaftNode leader;
    //
    // @BeforeEach
    // void setUp() {
    //     cluster = new TestCluster("n1", "n2", "n3");
    //     cluster.node("n1").tick(400);
    //     cluster.deliverAll();          // elect n1
    //     leader = cluster.node("n1");
    // }

    @Test
    @DisplayName("should append a submitted command to the leader's log")
    void shouldAppendToLeaderLog() {
        // assertThat(leader.submitCommand("SET x 1")).isTrue();
        // assertThat(cluster.log("n1").lastIndex()).isEqualTo(1);
        // assertThat(cluster.log("n1").entryAt(1).command()).isEqualTo("SET x 1");
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should reject commands submitted to a follower")
    void shouldRejectCommandOnFollower() {
        // assertThat(cluster.node("n2").submitCommand("SET x 1")).isFalse();
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should replicate an entry to all followers")
    void shouldReplicateToFollowers() {
        // leader.submitCommand("SET x 1");
        // cluster.deliverAll();
        // assertThat(cluster.log("n2").entryAt(1).command()).isEqualTo("SET x 1");
        // assertThat(cluster.log("n3").entryAt(1).command()).isEqualTo("SET x 1");
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should preserve order across multiple commands")
    void shouldPreserveOrder() {
        // leader.submitCommand("SET x 1");
        // leader.submitCommand("SET y 2");
        // cluster.deliverAll();
        // assertThat(cluster.log("n2").entryAt(1).command()).isEqualTo("SET x 1");
        // assertThat(cluster.log("n2").entryAt(2).command()).isEqualTo("SET y 2");
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should reject AppendEntries when prevLogTerm does not match")
    void shouldRejectOnPrevLogMismatch() {
        // RaftNode n2 = cluster.node("n2");
        // var req = new RpcMessage.AppendEntriesRequest(
        //         leader.currentTerm(), "n1",
        //         5 /* prevLogIndex not present */, 3 /* prevLogTerm */,
        //         List.of(new LogEntry(6, leader.currentTerm(), "SET x 1")), 0);
        // var reply = n2.handleAppendEntries(req);
        // assertThat(reply.success()).isFalse();
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should accept heartbeat (empty AppendEntries) that matches the log")
    void shouldAcceptHeartbeat() {
        // RaftNode n2 = cluster.node("n2");
        // var hb = new RpcMessage.AppendEntriesRequest(
        //         leader.currentTerm(), "n1", 0, 0, List.of(), 0);
        // assertThat(n2.handleAppendEntries(hb).success()).isTrue();
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should overwrite a follower's conflicting suffix")
    void shouldOverwriteConflictingSuffix() {
        // Give n2 a divergent entry at index 1, then replicate the leader's
        // entry and assert n2's log converges to the leader's.
        // ...
        // cluster.deliverAll();
        // assertThat(cluster.log("n2").entryAt(1)).isEqualTo(cluster.log("n1").entryAt(1));
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should backtrack nextIndex until a stale follower's log converges")
    void shouldBacktrackUntilConvergence() {
        // A follower that is several entries behind should, after enough
        // AppendEntries rounds, have an identical log to the leader.
        // for (int i = 0; i < 5; i++) leader.submitCommand("SET k" + i + " " + i);
        // cluster.deliverAll();
        // assertThat(cluster.log("n3").entriesFrom(1))
        //         .isEqualTo(cluster.log("n1").entriesFrom(1));
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should track matchIndex for each follower after successful replication")
    void shouldTrackMatchIndex() {
        // leader.submitCommand("SET x 1");
        // cluster.deliverAll();
        // // Depending on your API, assert the leader knows n2/n3 match index 1.
        // assertThat(cluster.matchIndex("n1", "n2")).isEqualTo(1);
        fail("Implement RaftNode and uncomment this test");
    }
}
