package linuxlingo.shell.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

public class WcCommandTest {
    private WcCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new WcCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
        vfs.createFile("/words.txt", "/");
        vfs.writeFile("/words.txt", "/", "hello world\nlinux lingo", false);
    }

    @Test
    public void wcCommand_default_returnsLinesWordsChars() {
        String[] args = {"words.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("2 4 23 words.txt", result.getStdout());
    }

    @Test
    public void wcCommand_linesFlagOnly_returnsLinesOnly() {
        String[] args = {"words.txt", "-l"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("2 words.txt", result.getStdout());
    }

    @Test
    public void wcCommand_invalidFlag_returnsError() {
        String[] args = {"words.txt", "-r"};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertEquals("wc: " + command.getUsage(), result.getStderr());
    }
}
