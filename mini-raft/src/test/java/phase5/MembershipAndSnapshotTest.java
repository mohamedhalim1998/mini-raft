package phase5;

import com.buildyourx.miniraft.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("phase5")
@DisplayName("Membership Changes & Snapshots")
class MembershipAndSnapshotTest {

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

    // --- Membership Changes ---

    @Test
    @DisplayName("should add a new server as a non-voting learner first")
    void shouldAddServerAsLearner() {
        // assertThat(leader.addServer("n4", true)).isTrue();
        // cluster.deliverAll();
        // assertThat(leader.configuration().learners()).contains("n4");
        // assertThat(leader.configuration().voters()).doesNotContain("n4");
        fail("Implement membership changes and uncomment this test");
    }

    @Test
    @DisplayName("should reject a membership change proposed to a follower")
    void shouldRejectMembershipChangeOnFollower() {
        // assertThat(cluster.node("n2").addServer("n4", false)).isFalse();
        fail("Implement membership changes and uncomment this test");
    }

    @Test
    @DisplayName("configuration change should take effect when appended, not when committed")
    void configChangeAppliesWhenAppended() {
        // After addServer appends the config entry (before commit), the leader
        // already uses the new configuration for quorum decisions.
        // leader.addServer("n4", false);
        // assertThat(leader.configuration().voters()).contains("n4");
        fail("Implement membership changes and uncomment this test");
    }

    @Test
    @DisplayName("should promote a caught-up learner to a voter")
    void shouldPromoteLearnerToVoter() {
        // leader.addServer("n4", true);
        // cluster.deliverAll(); // let n4 catch up
        // leader.addServer("n4", false); // promote
        // cluster.deliverAll();
        // assertThat(leader.configuration().voters()).contains("n4");
        fail("Implement membership changes and uncomment this test");
    }

    @Test
    @DisplayName("should remove a server from the configuration")
    void shouldRemoveServer() {
        // assertThat(leader.removeServer("n3")).isTrue();
        // cluster.deliverAll();
        // assertThat(leader.configuration().voters()).doesNotContain("n3");
        fail("Implement membership changes and uncomment this test");
    }

    @Test
    @DisplayName("should preserve majority overlap during a single-server change")
    void shouldPreserveMajorityOverlap() {
        // Adding/removing one server at a time must never allow two disjoint
        // majorities. Drive an add concurrently with an election and assert at
        // most one leader emerges.
        // ...
        // assertThat(cluster.leaders()).hasSizeLessThanOrEqualTo(1);
        fail("Implement membership changes and uncomment this test");
    }

    // --- Snapshots ---

    @Test
    @DisplayName("should take a snapshot capturing state up to lastApplied")
    void shouldTakeSnapshot() {
        // leader.submitCommand("SET x 1");
        // cluster.deliverAll();
        // Snapshot snap = leader.takeSnapshot();
        // assertThat(snap.lastIncludedIndex()).isEqualTo(leader.lastApplied());
        fail("Implement snapshots and uncomment this test");
    }

    @Test
    @DisplayName("should compact the log after taking a snapshot")
    void shouldCompactLogAfterSnapshot() {
        // for (int i = 0; i < 5; i++) leader.submitCommand("SET k" + i + " " + i);
        // cluster.deliverAll();
        // leader.takeSnapshot();
        // assertThat(cluster.log("n1").snapshotIndex()).isGreaterThan(0);
        fail("Implement snapshots and uncomment this test");
    }

    @Test
    @DisplayName("should catch up a far-behind follower via InstallSnapshot")
    void shouldInstallSnapshotOnLaggingFollower() {
        // Advance and snapshot on the leader while a follower is partitioned so
        // the needed entries are discarded. On reconnect, the follower is
        // brought up to date via InstallSnapshot.
        // var req = new RpcMessage.InstallSnapshotRequest(
        //         leader.currentTerm(), "n1", 5, leader.currentTerm(), snapshotBytes);
        // var reply = cluster.node("n3").handleInstallSnapshot(req);
        // assertThat(reply.term()).isGreaterThanOrEqualTo(leader.currentTerm());
        fail("Implement snapshots and uncomment this test");
    }

    @Test
    @DisplayName("state machine should be restored from an installed snapshot")
    void shouldRestoreStateMachineFromSnapshot() {
        // After InstallSnapshot, the follower's state machine reflects the
        // snapshot's contents and it resumes normal replication.
        // ...
        // assertThat(cluster.stateMachine("n3").apply("GET x")).isEqualTo("1");
        fail("Implement snapshots and uncomment this test");
    }
}
