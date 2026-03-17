package linuxlingo.shell.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

public class ChmodCommandTest {
    private ChmodCommand command;
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        command = new ChmodCommand();
        vfs = new VirtualFileSystem();
        session = new ShellSession(vfs, null);
        vfs.createFile("/script.sh", "/");
    }

    @Test
    public void chmodCommand_octalMode_changesPermission() {
        String[] args = {"755", "script.sh"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("755", vfs.resolve("script.sh", "/").getPermission().toOctal());
    }

    @Test
    public void chmodCommand_symbolicMode_changesPermission() {
        String[] args = {"ug=rwx", "script.sh"};
        CommandResult result = command.execute(session, args, null);

        assertTrue(result.isSuccess());
        assertEquals("rwxrwxr--", vfs.resolve("script.sh", "/").getPermission().toString());
    }

    @Test
    public void chmodCommand_missingArgs_returnsError() {
        String[] args = {};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertEquals("chmod: " + command.getUsage(), result.getStderr());
    }

    @Test
    public void chmodCommand_invalidMode_returnsError() {
        String[] args = new String[]{"u=rwxsmth", "script.sh"};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getStderr().contains("chmod: invalid mode: "));
    }

    @Test
    public void chmodCommand_wrongArgsOrder_returnsError() {
        String[] args = {"script.sh", "755"};
        CommandResult result = command.execute(session, args, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getStderr().contains("chmod: invalid mode: "));
    }
}