package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.List;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.FileNode;
import linuxlingo.shell.vfs.VfsException;

/**
 * Finds files by name pattern under a given path.
 * Syntax: find &lt;path&gt; -name &lt;pattern&gt;
 *
 * <p><b>Owner: C</b></p>
 */
public class FindCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        if (args.length != 3 || !args[1].equals("-name")) {
            return CommandResult.error("find: " + getUsage());
        }

        String path = args[0];
        String pattern = args[2];

        try {
            List<FileNode> matches = session.getVfs().findByName(path, session.getWorkingDir(), pattern);
            List<String> paths = new ArrayList<>();
            for (FileNode node : matches) {
                paths.add(node.getAbsolutePath());
            }
            return CommandResult.success(String.join("\n", paths));
        } catch (VfsException e) {
            return CommandResult.error("find: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "find <path> -name <pattern>";
    }

    @Override
    public String getDescription() {
        return "Find files by name pattern";
    }
}