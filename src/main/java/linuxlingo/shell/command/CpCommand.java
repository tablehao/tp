package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.List;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VfsException;

/**
 * Copies files or directories.
 * Syntax: cp [-r] &lt;src&gt; &lt;dest&gt;
 *
 * <p><b>Owner: C</b></p>
 */
public class CpCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        boolean recursive = false;
        List<String> paths = new ArrayList<>();

        for (String arg : args) {
            if (arg.equals("-r")) {
                recursive = true;
            } else if (!arg.startsWith("-")) {
                paths.add(arg);
            }
        }

        if (paths.size() != 2) {
            return CommandResult.error("cp: " + getUsage());
        }

        try {
            session.getVfs().copy(paths.get(0), paths.get(1), session.getWorkingDir(), recursive);
            return CommandResult.success("");
        } catch (VfsException e) {
            return CommandResult.error("cp: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "cp [-r] <src> <dest>";
    }

    @Override
    public String getDescription() {
        return "Copy file or directory";
    }
}