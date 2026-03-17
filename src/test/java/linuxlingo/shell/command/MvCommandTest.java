package linuxlingo.shell.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MvCommandTest {
    private MvCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new MvCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
    }

    @Test
    public void mvCommand_validSrcFileAndDestFile_renamesFile() {
        vfs.createFile("/src.txt", "/");

        String[] args = {"src.txt", "dest.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertFalse(vfs.exists("/src.txt", "/"));
        assertTrue(vfs.exists("/dest.txt", "/"));
    }

    @Test
    public void mvCommand_validSrcFileAndDestDir_movesFile() {
        vfs.createFile("/src.txt", "/");
        vfs.createDirectory("/destdir", "/", false);

        String[] args = {"src.txt", "destdir"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertFalse(vfs.exists("/src.txt", "/"));
        assertTrue(vfs.exists("/destdir/src.txt", "/"));
    }

    @Test
    public void mvCommand_missingArgs_returnsError() {
        String[] args = {"src.txt"};
        CommandResult result = command.execute(session, args, null);
        assertFalse(result.isSuccess());
        assertEquals("mv: " + command.getUsage(), result.getStderr());
    }
}