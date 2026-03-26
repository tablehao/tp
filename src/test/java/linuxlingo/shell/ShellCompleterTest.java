package linuxlingo.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.jline.reader.Candidate;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.vfs.VirtualFileSystem;

/**
 * Tests for ShellCompleter command and path completion behaviour.
 */
public class ShellCompleterTest {
    private VirtualFileSystem vfs;
    private ShellSession session;
    private ShellCompleter completer;

    @BeforeEach
    public void setUp() {
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
        session.setWorkingDir("/home/user");
        completer = new ShellCompleter(session);
    }

    // ─── Core behaviour ─────────────────────────────────────────

    @Test
    public void getCommandCompletions_matchesByPrefix() {
        SortedSet<String> results = completer.getCommandCompletions("gr");
        assertNotNull(results);
        assertTrue(results.contains("grep"));
    }

    @Test
    public void getPathCompletions_rootPrefix_matchesDirectory() {
        SortedSet<String> results = completer.getPathCompletions("/");
        assertNotNull(results);
        assertTrue(results.contains("/home/"));
    }

    @Test
    public void completeCommandName_addsCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        completer.completeCommandName("gr", candidates);
        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("grep")));
    }

    @Test
    public void completePath_addsCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        completer.completePath("/ho", candidates);
        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("/home/")));
    }

    // ─── Additional scenarios ────────────────────────────────────

    @Nested
    class CommandNameCompletion {
        @Test
        public void emptyPrefix_returnsAllCommands() {
            SortedSet<String> results = completer.getCommandCompletions("");
            assertTrue(results.contains("ls"));
            assertTrue(results.contains("cd"));
        }

        @Test
        public void prefixL_returnsLCommands() {
            SortedSet<String> results = completer.getCommandCompletions("l");
            assertTrue(results.contains("ls"));
        }
    }

    @Nested
    class PathCompletion {
        @Test
        public void absolutePath_rootChildren() {
            SortedSet<String> results = completer.getPathCompletions("/");
            assertTrue(results.contains("/home/"));
        }

        @Test
        public void relativePath_fromWorkingDir() {
            vfs.createFile("/home/user/notes.txt", "/");
            SortedSet<String> results = completer.getPathCompletions("n");
            assertTrue(results.contains("notes.txt"));
        }
    }

    @Nested
    class CandidateIntegration {
        @Test
        public void completeCommandName_addsCandidates() {
            List<Candidate> candidates = new ArrayList<>();
            completer.completeCommandName("gr", candidates);
            assertTrue(candidates.stream().anyMatch(c -> c.value().equals("grep")));
        }
    }
}
