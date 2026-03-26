package linuxlingo.shell.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

/**
 * Unit tests for TouchCommand.
 */
public class TouchCommandTest {
    private TouchCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new TouchCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
    }

    @Test
    public void touch_multipleFiles_createsAllFiles() {
        CommandResult result = command.execute(session,
                new String[]{"/tmp/a.txt", "/tmp/b.txt", "/tmp/c.txt"}, null);

        assertTrue(result.isSuccess());
        assertTrue(vfs.exists("/tmp/a.txt", "/"));
        assertTrue(vfs.exists("/tmp/b.txt", "/"));
        assertTrue(vfs.exists("/tmp/c.txt", "/"));
    }

    @Test
    public void touch_existingAndNewFiles_succeeds() {
        vfs.createFile("/tmp/existing.txt", "/");

        CommandResult result = command.execute(session,
                new String[]{"/tmp/existing.txt", "/tmp/new.txt"}, null);

        assertTrue(result.isSuccess());
        assertTrue(vfs.exists("/tmp/existing.txt", "/"));
        assertTrue(vfs.exists("/tmp/new.txt", "/"));
    }

    @Test
    public void touch_missingOperand_returnsError() {
        CommandResult result = command.execute(session, new String[]{}, null);
        assertFalse(result.isSuccess());
        assertTrue(result.getStderr().contains("missing file operand"));
    }
}
