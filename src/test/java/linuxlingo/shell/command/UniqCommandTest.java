package linuxlingo.shell.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

public class UniqCommandTest {
    private UniqCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new UniqCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
        vfs.createFile("/dupes.txt", "/");
        vfs.writeFile("/dupes.txt", "/", "apple\napple\nbanana\napple", false);
    }

    @Test
    public void uniqCommand_default_removesAdjacentDuplicates() {
        String[] args = {"dupes.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("apple\nbanana\napple", result.getStdout());
    }

    @Test
    public void uniqCommand_countFlag_prefixesWithOccurrences() {
        String[] args = {"-c", "dupes.txt"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("      2 apple\n      1 banana\n      1 apple", result.getStdout());
    }
}
