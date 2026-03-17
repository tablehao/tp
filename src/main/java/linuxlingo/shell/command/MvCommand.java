package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.List;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VfsException;

/**
 * Moves or renames files or directories.
 * Syntax: mv &lt;src&gt; &lt;dest&gt;
 *
 * <p><b>Owner: C</b></p>
 */
public class MvCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        List<String> paths = new ArrayList<>();
        for (String arg : args) {
            if (!arg.startsWith("-")) {
                paths.add(arg);
            }
        }

        if (paths.size() != 2) {
            return CommandResult.error("mv: " + getUsage());
        }

        try {
            session.getVfs().move(paths.get(0), paths.get(1), session.getWorkingDir());
            return CommandResult.success("");
        } catch (VfsException e) {
            return CommandResult.error("mv: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "mv <src> <dest>";
    }

    @Override
    public String getDescription() {
        return "Move or rename file or directory";
    }
}