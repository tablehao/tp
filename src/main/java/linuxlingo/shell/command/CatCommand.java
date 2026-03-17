package linuxlingo.shell.command;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VfsException;

/**
 * Displays file contents. Supports concatenating multiple files.
 * Syntax: cat &lt;file&gt; [file2...]
 *
 * <p><b>Owner: C</b></p>
 */
public class CatCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        if (args.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                try {
                    String content = session.getVfs().readFile(arg, session.getWorkingDir());
                    sb.append(content);
                } catch (VfsException e) {
                    return CommandResult.error("cat: " + e.getMessage());
                }
            }
            return CommandResult.success(sb.toString());
        } else if (stdin != null) {
            return CommandResult.success(stdin);
        } else {
            return CommandResult.error("cat: missing file operand");
        }
    }

    @Override
    public String getUsage() {
        return "cat <file> [file2...]";
    }

    @Override
    public String getDescription() {
        return "Display file contents";
    }
}