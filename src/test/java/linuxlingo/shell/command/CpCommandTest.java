package linuxlingo.shell.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

public class CpCommandTest {
    private CpCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new CpCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
    }

    @Test
    public void cpCommand_validFiles_copiesFile() {
        vfs.createFile("/src.txt", "/");
        vfs.writeFile("/src.txt", "/", "some data", false);

        String[] args = {"src.txt", "dest.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertTrue(vfs.exists("/dest.txt", "/"));
        assertEquals("some data", vfs.readFile("/dest.txt", "/"));
    }

    @Test
    public void cpCommand_recursiveFlag_copiesDirectoryAndContents() {
        vfs.createDirectory("/srcdir", "/", false);
        vfs.createFile("/srcdir/data.txt", "/");
        vfs.writeFile("/srcdir/data.txt", "/", "some other data", false);

        String[] args = {"-r", "srcdir", "destdir"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertTrue(vfs.exists("/destdir", "/"));
        assertTrue(vfs.exists("/destdir/data.txt", "/"));
        assertEquals("some other data", vfs.readFile("/destdir/data.txt", "/"));
    }

    @Test
    public void cpCommand_missingArgs_returnsError() {
        String[] args = {};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertEquals("cp: " + command.getUsage(), result.getStderr());
    }
}