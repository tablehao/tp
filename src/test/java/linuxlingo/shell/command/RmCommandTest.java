package linuxlingo.shell.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RmCommandTest {
    private RmCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new RmCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
    }

    @Test
    public void rmCommand_singleFile_deletesFile() {
        vfs.createFile("/test.txt", "/");

        String[] args = {"test.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertFalse(vfs.exists("/test.txt", "/"));
    }

    @Test
    public void rmCommand_recursiveFlag_deletesDirectory() {
        vfs.createDirectory("/dir", "/", false);

        String[] args = {"-r", "dir"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertFalse(vfs.exists("/dir", "/"));
    }

    @Test
    public void rmCommand_noRecursiveFlag_returnsError() {
        vfs.createDirectory("/dir", "/", false);

        String[] args = {"dir"};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
    }

    @Test
    public void rmCommand_missingArgs_returnsError() {
        String[] args = {"-f"};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertEquals("rm: missing operand", result.getStderr());
    }
}