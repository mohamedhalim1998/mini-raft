package phase4;

import com.buildyourx.miniraft.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("phase4")
@DisplayName("Persistence & Recovery")
class PersistenceTest {

    // TODO: Replace with your implementation.
    // A restart is modeled by constructing a new RaftNode over the SAME
    // StableStorage instance the crashed node used.
    //
    // private StableStorage storage;
    //
    // @BeforeEach
    // void setUp() {
    //     storage = new InMemoryStableStorage();
    // }

    @Test
    @DisplayName("should persist currentTerm before responding to RPCs")
    void shouldPersistCurrentTerm() {
        // RaftNode n = TestNode.with("n1", storage);
        // n.handleRequestVote(new RpcMessage.RequestVoteRequest(7, "n2", 0, 0));
        // assertThat(storage.readCurrentTerm()).isEqualTo(7);
        fail("Implement StableStorage/RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should persist votedFor when granting a vote")
    void shouldPersistVotedFor() {
        // RaftNode n = TestNode.with("n1", storage);
        // n.handleRequestVote(new RpcMessage.RequestVoteRequest(1, "n2", 0, 0));
        // assertThat(storage.readVotedFor()).isEqualTo("n2");
        fail("Implement StableStorage/RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should restore currentTerm and votedFor after a restart")
    void shouldRestoreTermAndVote() {
        // TestNode.with("n1", storage)
        //         .handleRequestVote(new RpcMessage.RequestVoteRequest(3, "n2", 0, 0));
        // RaftNode restarted = TestNode.with("n1", storage); // "crash + restart"
        // assertThat(restarted.currentTerm()).isEqualTo(3);
        fail("Implement StableStorage/RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should come back up as a follower after restart")
    void shouldRestartAsFollower() {
        // RaftNode restarted = TestNode.with("n1", storage);
        // assertThat(restarted.state()).isEqualTo(RaftState.FOLLOWER);
        fail("Implement StableStorage/RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should not vote twice in the same term across a restart")
    void shouldNotDoubleVoteAcrossRestart() {
        // TestNode.with("n1", storage)
        //         .handleRequestVote(new RpcMessage.RequestVoteRequest(4, "n2", 0, 0));
        // RaftNode restarted = TestNode.with("n1", storage);
        // var reply = restarted.handleRequestVote(
        //         new RpcMessage.RequestVoteRequest(4, "n3", 0, 0));
        // assertThat(reply.voteGranted()).isFalse();
        fail("Implement StableStorage/RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("should persist the log and restore it after restart")
    void shouldPersistAndRestoreLog() {
        // A leader appends entries; after restart the log is intact.
        // ...
        // assertThat(storage.readLog()).hasSize(2);
        fail("Implement StableStorage/RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("committed entries should survive a full-cluster restart")
    void committedEntriesSurviveFullRestart() {
        // Commit an entry across the cluster, restart every node from its
        // storage, and assert the committed entry is still present everywhere.
        // ...
        // assertThat(cluster.log("n1").entryAt(1)).isNotNull();
        fail("Implement StableStorage/RaftNode and uncomment this test");
    }

    @Test
    @DisplayName("volatile commitIndex/lastApplied should be rebuilt after restart")
    void shouldRebuildVolatileState() {
        // After restart, commitIndex/lastApplied start at their safe defaults
        // and are re-established by the leader's AppendEntries.
        // RaftNode restarted = TestNode.with("n1", storage);
        // assertThat(restarted.lastApplied()).isEqualTo(0);
        fail("Implement StableStorage/RaftNode and uncomment this test");
    }
}
