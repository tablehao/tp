package linuxlingo.shell.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

public class TailCommandTest {
    private TailCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new TailCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
        vfs.createFile("/data.txt", "/");
        vfs.writeFile("/data.txt", "/", "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12", false);
    }

    @Test
    public void tailCommand_default_returnsLastTenLines() {
        String[] args = {"data.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("3\n4\n5\n6\n7\n8\n9\n10\n11\n12", result.getStdout());
    }

    @Test
    public void tailCommand_returnsTrailingEmptyLines() {
        vfs.createFile("/trailing.txt", "/");
        vfs.writeFile("/trailing.txt", "/", "1\n\n\n\n", false);

        String[] args = {"trailing.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("1\n\n\n\n", result.getStdout());
    }

    @Test
    public void tailCommand_nFlag_returnsLastNLines() {
        String[] args = {"data.txt", "-n", "4"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("9\n10\n11\n12", result.getStdout());
    }

    @Test
    public void tailCommand_negativeNFlagValue_returnsError() {
        String[] args = {"data.txt", "-n", "-4"};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getStderr().contains("tail: invalid number of lines: "));
    }

    @Test
    public void tailCommand_missingNFlagValue_returnsError() {
        String[] args = {"data.txt", "-n"};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertEquals("tail: option requires an argument -- n", result.getStderr());
    }

    @Test
    public void tailCommand_invalidNFlagValue_returnsError() {
        String[] args = {"data.txt", "-n", "inf"};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getStderr().contains("tail: invalid number of lines: "));
    }
}
