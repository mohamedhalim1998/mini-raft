package phase1;

import com.buildyourx.miniraft.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("phase1")
@DisplayName("Leader Election")
class LeaderElectionTest {

    // TODO: Replace with your implementation.
    // A 3-node cluster wired over a deterministic in-memory transport is a
    // good starting point. Advance time with node.tick(...) to fire timeouts.
    //
    // private TestCluster cluster; // 3 nodes: "n1", "n2", "n3"
    //
    // @BeforeEach
    // void setUp() {
    //     cluster = new TestCluster("n1", "n2", "n3");
    // }

    @Test
    @DisplayName("should start every node as a follower")
    void shouldStartAsFollower() {
        // assertThat(cluster.node("n1").state()).isEqualTo(RaftState.FOLLOWER);
        // assertThat(cluster.node("n2").state()).isEqualTo(RaftState.FOLLOWER);
        // assertThat(cluster.node("n3").state()).isEqualTo(RaftState.FOLLOWER);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should become candidate when election timeout elapses")
    void shouldBecomeCandidateOnTimeout() {
        // RaftNode n1 = cluster.node("n1");
        // n1.tick(400); // exceed the election timeout
        // assertThat(n1.state()).isEqualTo(RaftState.CANDIDATE);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should increment term when starting an election")
    void shouldIncrementTermOnElection() {
        // RaftNode n1 = cluster.node("n1");
        // long before = n1.currentTerm();
        // n1.tick(400);
        // assertThat(n1.currentTerm()).isEqualTo(before + 1);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should elect exactly one leader when a candidate wins a majority")
    void shouldElectSingleLeader() {
        // cluster.node("n1").tick(400);   // n1 times out first, requests votes
        // cluster.deliverAll();           // deliver RequestVote + replies
        // assertThat(cluster.node("n1").state()).isEqualTo(RaftState.LEADER);
        // assertThat(cluster.leaders()).hasSize(1);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should grant vote to an up-to-date candidate")
    void shouldGrantVote() {
        // var req = new RpcMessage.RequestVoteRequest(1, "n1", 0, 0);
        // var reply = cluster.node("n2").handleRequestVote(req);
        // assertThat(reply.voteGranted()).isTrue();
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should not grant a second vote in the same term")
    void shouldNotVoteTwiceInSameTerm() {
        // RaftNode n2 = cluster.node("n2");
        // n2.handleRequestVote(new RpcMessage.RequestVoteRequest(1, "n1", 0, 0));
        // var reply = n2.handleRequestVote(new RpcMessage.RequestVoteRequest(1, "n3", 0, 0));
        // assertThat(reply.voteGranted()).isFalse();
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should reject a vote request with a stale term")
    void shouldRejectStaleTerm() {
        // RaftNode n2 = cluster.node("n2");
        // // advance n2 to term 5 first...
        // var reply = n2.handleRequestVote(new RpcMessage.RequestVoteRequest(2, "n1", 0, 0));
        // assertThat(reply.voteGranted()).isFalse();
        // assertThat(reply.term()).isGreaterThanOrEqualTo(5);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should step down to follower when it sees a higher term")
    void shouldStepDownOnHigherTerm() {
        // RaftNode n1 = cluster.node("n1");
        // n1.tick(400); // becomes candidate at some term T
        // n1.handleAppendEntries(new RpcMessage.AppendEntriesRequest(
        //         n1.currentTerm() + 1, "n2", 0, 0, java.util.List.of(), 0));
        // assertThat(n1.state()).isEqualTo(RaftState.FOLLOWER);
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("leader should suppress new elections via heartbeats")
    void leaderHeartbeatsSuppressElections() {
        // cluster.node("n1").tick(400);
        // cluster.deliverAll(); // n1 becomes leader
        // // repeatedly tick a small amount and deliver heartbeats
        // for (int i = 0; i < 10; i++) { cluster.tickAll(50); cluster.deliverAll(); }
        // assertThat(cluster.leaders()).containsExactly(cluster.node("n1"));
        fail("Implement RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should elect a new leader after the current leader stops")
    void shouldReelectAfterLeaderFailure() {
        // cluster.node("n1").tick(400);
        // cluster.deliverAll();               // n1 leader
        // cluster.partition("n1");            // n1 goes silent
        // cluster.node("n2").tick(400);       // a follower times out
        // cluster.deliverAll();
        // assertThat(cluster.leaders()).hasSize(1);
        // assertThat(cluster.node("n1").state()).isNotEqualTo(RaftState.LEADER);
        fail("Implement RaftNode and uncomment this test");
    }
}
