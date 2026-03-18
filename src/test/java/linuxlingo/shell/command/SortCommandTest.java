package linuxlingo.shell.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

public class SortCommandTest {
    private SortCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new SortCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
        vfs.createFile("/list.txt", "/");
        vfs.writeFile("/list.txt", "/", "zebra\napple\nmonkey", false);
        vfs.createFile("/nums.txt", "/");
        vfs.writeFile("/nums.txt", "/", "10\n2\n30", false);
    }

    @Test
    public void sortCommand_default_sortsAlphabetically() {
        String[] args = {"list.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("apple\nmonkey\nzebra", result.getStdout());
    }

    @Test
    public void sortCommand_numericFlag_sortsNumerically() {
        String[] args = {"-n", "nums.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("2\n10\n30", result.getStdout());
    }
}
