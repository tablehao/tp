package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.List;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VfsException;

/**
 * Creates a directory.
 * <p><b>v1.0</b>: Single directory creation with optional -p flag.</p>
 * <p><b>v2.0 [TODO]</b>: Support multiple directory paths in one command.</p>
 */
public class MkdirCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        boolean parents = false;
        List<String> paths = new ArrayList<>();

        for (String arg : args) {
            if (arg.equals("-p")) {
                parents = true;
            } else if (arg.startsWith("-")) {
                return CommandResult.error("mkdir: invalid option -- " + arg);
            } else {
                paths.add(arg);
            }
        }

        if (paths.isEmpty()) {
            return CommandResult.error("mkdir: missing operand");
        }

        try {
            for (String path : paths) {
                session.getVfs().createDirectory(path, session.getWorkingDir(), parents);
            }
            return CommandResult.success("");
        } catch (VfsException e) {
            return CommandResult.error("mkdir: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "mkdir [-p] <path> [path2...]";
    }

    @Override
    public String getDescription() {
        return "Create directory";
    }
}
