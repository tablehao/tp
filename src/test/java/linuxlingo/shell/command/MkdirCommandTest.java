package linuxlingo.shell.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

/**
 * Unit tests for MkdirCommand.
 */
public class MkdirCommandTest {
    private MkdirCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new MkdirCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
    }

    @Test
    public void mkdir_multiplePaths_createsAllDirectories() {
        CommandResult result = command.execute(session,
                new String[]{"/tmp/a", "/tmp/b", "/tmp/c"}, null);

        assertTrue(result.isSuccess());
        assertTrue(vfs.exists("/tmp/a", "/"));
        assertTrue(vfs.exists("/tmp/b", "/"));
        assertTrue(vfs.exists("/tmp/c", "/"));
    }

    @Test
    public void mkdir_withP_createsNestedPaths() {
        CommandResult result = command.execute(session,
                new String[]{"-p", "/tmp/x/y", "/tmp/u/v"}, null);

        assertTrue(result.isSuccess());
        assertTrue(vfs.exists("/tmp/x/y", "/"));
        assertTrue(vfs.exists("/tmp/u/v", "/"));
    }

    @Test
    public void mkdir_missingOperand_returnsError() {
        CommandResult result = command.execute(session, new String[]{}, null);
        assertFalse(result.isSuccess());
        assertTrue(result.getStderr().contains("missing operand"));
    }

    @Test
    public void mkdir_invalidOption_returnsError() {
        CommandResult result = command.execute(session, new String[]{"-z"}, null);
        assertFalse(result.isSuccess());
        assertTrue(result.getStderr().contains("invalid option"));
    }
}
