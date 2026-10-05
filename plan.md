# Build Your Raft

## Overview

We're building **mini-raft** — a simplified but correct implementation of the Raft consensus algorithm. It maintains a replicated log across a small cluster of in-process nodes, elects a leader, replicates commands, applies committed entries to a state machine, survives restarts via persistence, and handles snapshots and membership changes.

To keep the focus on the algorithm (not networking), the skeleton models the cluster as in-process nodes communicating through a pluggable transport you can drive deterministically in tests — advancing a simulated clock and delivering RPCs by hand. By the end you'll have a state machine that never diverges across nodes, even under leader failures and partitions.

## Phases

### Phase 1: Leader Election (Difficulty: Beginner)

- [ ] Model the three server states (`FOLLOWER`, `CANDIDATE`, `LEADER`)
- [ ] Track persistent term state (`currentTerm`, `votedFor`)
- [ ] Implement election timeout with randomization
- [ ] Convert follower → candidate when the election timeout elapses
- [ ] Implement the `RequestVote` RPC handler (grant/deny logic, term rules)
- [ ] Count votes and transition candidate → leader on majority
- [ ] Send heartbeats (empty `AppendEntries`) as leader to suppress new elections
- [ ] Step down to follower when a higher term is observed

**Goal**: A cluster of nodes elects exactly one leader per term. When the leader "fails" (stops sending heartbeats), a follower times out and a new leader is elected. No two leaders coexist in the same term.

**Tests**: Run with `mvn test -Dgroups=phase1`

**Hints**:
- Terms are the logical clock — every RPC carries a term, and the rule "higher term wins, revert to follower" is applied everywhere.
- Inject a `Clock` and a controllable timeout source so tests are deterministic rather than sleeping on real time.
- A candidate votes for itself and needs `floor(N/2) + 1` votes total to win.
- Grant a vote only if you haven't voted this term (or already voted for this candidate).

---

### Phase 2: Log Replication (Difficulty: Intermediate)

- [ ] Define the log as an ordered list of `LogEntry(term, index, command)`
- [ ] Accept client commands at the leader and append them locally
- [ ] Implement `AppendEntries` with `prevLogIndex` / `prevLogTerm` consistency check
- [ ] Reject and retry: leader decrements `nextIndex` on rejection until logs converge
- [ ] Overwrite conflicting suffix entries on followers
- [ ] Maintain per-follower `nextIndex[]` and `matchIndex[]`
- [ ] Enforce the Log Matching Property across all nodes

**Goal**: The leader replicates client commands to followers. Divergent follower logs are reconciled by walking `nextIndex` backward until a matching point is found, then overwriting. All logs converge to the leader's.

**Tests**: Run with `mvn test -Dgroups=phase2`

**Hints**:
- The consistency check is the heart of replication: a follower only accepts entries if `log[prevLogIndex].term == prevLogTerm`.
- Heartbeats are just `AppendEntries` with no entries — reuse the same code path.
- After a successful append, advance `matchIndex` and `nextIndex` for that follower.
- Test with a follower that starts with a stale/conflicting log and assert it converges.

---

### Phase 3: Safety & Commit Rules (Difficulty: Advanced)

- [ ] Implement the Election Restriction (up-to-date log check in `RequestVote`)
- [ ] Compute `commitIndex` from a majority of `matchIndex` values
- [ ] Enforce the "only commit current-term entries directly" rule (Figure 8)
- [ ] Advance `commitIndex` and apply entries to the state machine in order
- [ ] Propagate `leaderCommit` so followers apply the same entries
- [ ] Track `lastApplied` and apply entries exactly once, in index order

**Goal**: Committed entries are never lost across leader changes, and no two nodes ever apply a different command at the same index. The safety properties (Leader Completeness, State Machine Safety) hold under leader failover.

**Tests**: Run with `mvn test -Dgroups=phase3`

**Hints**:
- "Up-to-date": compare last-log terms first; if equal, the longer log wins.
- A leader must NOT count an older-term entry as committed just because it's on a majority — only mark committed once an entry from its own term reaches a majority on top.
- Apply committed entries to the state machine strictly in index order, advancing `lastApplied` toward `commitIndex`.
- Recreate the Figure 8 scenario to prove your commit rule is safe.

---

### Phase 4: Persistence & Recovery (Difficulty: Intermediate)

- [ ] Persist `currentTerm`, `votedFor`, and `log[]` to stable storage before responding to RPCs
- [ ] Reload persisted state on node restart
- [ ] Rebuild volatile state (`commitIndex`, `lastApplied`) after restart
- [ ] Verify a restarted node does not vote twice in the same term
- [ ] Verify committed entries survive a full-cluster restart

**Goal**: A node can crash and restart without losing committed data or violating safety. Persistent state is durably written before any RPC is acknowledged.

**Tests**: Run with `mvn test -Dgroups=phase4`

**Hints**:
- Model stable storage behind a `StableStorage` interface so tests can use an in-memory implementation that survives a simulated "restart" (new node instance, same storage).
- Persist BEFORE replying — a vote or append acknowledged but not persisted can break safety after a crash.
- `commitIndex`/`lastApplied` are intentionally volatile; they're safely recomputed from replication.

---

### Phase 5: Membership Changes & Snapshots (Difficulty: Advanced)

- [ ] Represent cluster configuration as a set of voting members
- [ ] Implement single-server add/remove (one change at a time) with majority overlap
- [ ] (Optional) Implement joint consensus (C-old,new → C-new) for atomic multi-server changes
- [ ] Support non-voting learner members that catch up before counting toward quorum
- [ ] Implement snapshot creation (`lastIncludedIndex`, `lastIncludedTerm`, state)
- [ ] Discard log entries covered by a snapshot (compaction)
- [ ] Implement the `InstallSnapshot` RPC for lagging followers

**Goal**: The cluster can safely add and remove nodes without ever forming two disjoint majorities, and the log can be compacted via snapshots so it doesn't grow unbounded. A far-behind follower is caught up via `InstallSnapshot`.

**Tests**: Run with `mvn test -Dgroups=phase5`

**Hints**:
- Configuration entries take effect when APPENDED, not when committed — a node always uses the latest config in its log.
- Single-server changes preserve majority overlap automatically; joint consensus is only needed for atomic multi-node changes.
- A snapshot replaces the log prefix; `InstallSnapshot` is used only when the needed entries have already been discarded.
- Add a new node as a learner first, let it catch up, then promote it to a voter.

---

## Success Criteria

How to know you've successfully completed the project:

- All tests pass: `mvn test`
- Exactly one leader is elected per term; a new leader is elected after leader failure
- Client commands replicate to a majority and are applied in identical order on every node
- Committed entries are never lost across leader failover or full-cluster restart
- No two nodes ever apply a different command at the same log index (State Machine Safety)
- Persisted nodes recover correctly and never double-vote within a term
- Nodes can be added/removed and the log compacted via snapshots without violating safety
