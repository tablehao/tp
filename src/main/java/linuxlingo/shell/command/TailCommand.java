package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.List;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VfsException;

/**
 * Displays the last N lines of a file (default 10).
 * Syntax: tail [-n N] &lt;file&gt;
 *
 * <p><b>Owner: C</b></p>
 */
public class TailCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        int n = 10;
        String file = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                if (i + 1 >= args.length) {
                    return CommandResult.error("tail: option requires an argument -- n");
                }

                try {
                    n = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    return CommandResult.error("tail: invalid number of lines: " + args[i + 1]);
                }

                if (n < 0) {
                    return CommandResult.error("tail: invalid number of lines: " + args[i]);
                }
            } else {
                file = args[i];
            }
        }

        String content;
        if (file != null) {
            try {
                content = session.getVfs().readFile(file, session.getWorkingDir());
            } catch (VfsException e) {
                return CommandResult.error("tail: " + e.getMessage());
            }
        } else if (stdin != null) {
            content = stdin;
        } else {
            return CommandResult.error("tail: missing file operand");
        }

        if (content.isEmpty()) {
            return CommandResult.success("");
        }

        String[] linesArray = content.split("\n", -1);
        int start = Math.max(0, linesArray.length - n);

        List<String> results = new ArrayList<>();
        for (int i = start; i < linesArray.length; i++) {
            results.add(linesArray[i]);
        }

        return CommandResult.success(String.join("\n", results));
    }

    @Override
    public String getUsage() {
        return "tail [-n N] <file>";
    }

    @Override
    public String getDescription() {
        return "Display last N lines of a file (default 10)";
    }
}
