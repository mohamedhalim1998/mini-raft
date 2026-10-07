package phase1;

import com.buildyourx.miniraft.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

@Tag("phase1")
@DisplayName("Leader Election")
class LeaderElectionTest {


    private Cluster cluster;

    @BeforeEach
    void setUp() {
        cluster = new Cluster("n1", "n2", "n3");
    }

    @Test
    @DisplayName("should start every node as a follower")
    void shouldStartAsFollower() {
        assertThat(cluster.node("n1").state()).isEqualTo(RaftState.FOLLOWER);
        assertThat(cluster.node("n2").state()).isEqualTo(RaftState.FOLLOWER);
        assertThat(cluster.node("n3").state()).isEqualTo(RaftState.FOLLOWER);
    }

    @Test
    @DisplayName("should become candidate when election timeout elapses")
    void shouldBecomeCandidateOnTimeout() throws InterruptedException {
        cluster.partition("n2");
        cluster.partition("n3");
        RaftNode n1 = cluster.node("n1");
        n1.tick(400);
        assertThat(n1.state()).isEqualTo(RaftState.CANDIDATE);
    }

    @Test
    @DisplayName("should increment term when starting an election")
    void shouldIncrementTermOnElection() throws InterruptedException {
        RaftNode n1 = cluster.node("n1");
        long before = n1.currentTerm();
        n1.tick(400);
        assertThat(n1.currentTerm()).isEqualTo(before + 1);

    }

    @Test
    @DisplayName("should elect exactly one leader when a candidate wins a majority")
    void shouldElectSingleLeader() throws InterruptedException {
        cluster.node("n1").tick(400);
        assertThat(cluster.node("n1").state()).isEqualTo(RaftState.LEADER);
        assertThat(cluster.leaders()).hasSize(1);
    }

    @Test
    @DisplayName("should grant vote to an up-to-date candidate")
    void shouldGrantVote() {
        var req = new RpcMessage.RequestVoteRequest(1, "n1", 0, 0);
        var reply = cluster.node("n2").handleRequestVote(req);
        assertThat(reply.voteGranted()).isTrue();

    }

    @Test
    @DisplayName("should not grant a second vote in the same term")
    void shouldNotVoteTwiceInSameTerm() {
        RaftNode n2 = cluster.node("n2");
        n2.handleRequestVote(new RpcMessage.RequestVoteRequest(1, "n1", 0, 0));
        var reply = n2.handleRequestVote(new RpcMessage.RequestVoteRequest(1, "n3", 0, 0));
        assertThat(reply.voteGranted()).isFalse();

    }

    @Test
    @DisplayName("should reject a vote request with a stale term")
    @Disabled("Disabled until phase 4")
    void shouldRejectStaleTerm() {
        RaftNode n2 = cluster.node("n2");
        var reply = n2.handleRequestVote(new RpcMessage.RequestVoteRequest(2, "n1", 0, 0));
        assertThat(reply.voteGranted()).isFalse();
        assertThat(reply.term()).isGreaterThanOrEqualTo(5);

    }

    @Test
    @DisplayName("should step down to follower when it sees a higher term")
    void shouldStepDownOnHigherTerm() {
        RaftNode n1 = cluster.node("n1");
        n1.tick(400);
        n1.handleAppendEntries(new RpcMessage.AppendEntriesRequest(
                n1.currentTerm() + 1, "n2", 0, 0, java.util.List.of(), 0));
        assertThat(n1.state()).isEqualTo(RaftState.FOLLOWER);

    }

    @Test
    @DisplayName("leader should suppress new elections via heartbeats")
    void leaderHeartbeatsSuppressElections() throws InterruptedException {
        cluster.node("n1").tick(400);
        for (int i = 0; i < 10; i++) {
            cluster.tickAll(50);
        }
        assertThat(cluster.leaders()).containsExactly(cluster.node("n1"));
    }

    @Test
    @DisplayName("should elect a new leader after the current leader stops")
    void shouldReelectAfterLeader() throws InterruptedException {
        cluster.node("n1").tick(400);
        cluster.node("n1").tick(200);
        cluster.partition("n1");
        cluster.node("n2").tick(400);
        assertThat(cluster.leaders()).hasSize(1);
        cluster.reconnect("n1");
        cluster.node("n2").tick(200);
        assertThat(cluster.node("n1").state()).isNotEqualTo(RaftState.LEADER);
    }
}
