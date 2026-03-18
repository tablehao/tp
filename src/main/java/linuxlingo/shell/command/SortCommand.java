package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VfsException;

/**
 * Sorts lines of a file.
 * Syntax: sort [-r] [-n] &lt;file&gt;
 *
 * <p><b>Owner: C</b></p>
 */
public class SortCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        boolean reverse = false;
        boolean numeric = false;

        String file = null;

        for (String arg : args) {
            if (arg.equals("-r")) {
                reverse = true;
            } else if (arg.equals("-n")) {
                numeric = true;
            } else if (!arg.startsWith("-") && file == null) {
                file = arg;
            } else {
                return CommandResult.error("sort: " + getUsage());
            }
        }

        String content;
        if (file != null) {
            try {
                content = session.getVfs().readFile(file, session.getWorkingDir());
            } catch (VfsException e) {
                return CommandResult.error("sort: " + e.getMessage());
            }
        } else if (stdin != null) {
            content = stdin;
        } else {
            return CommandResult.error("sort: missing file operand");
        }

        if (content.isEmpty()) {
            return CommandResult.success("");
        }

        String[] linesArray = content.split("\n");
        List<String> results = new ArrayList<>(Arrays.asList(linesArray));

        if (numeric) {
            results.sort((line1, line2) -> {
                try {
                    int num1 = Integer.parseInt(line1.trim().split("\\s+")[0]);
                    int num2 = Integer.parseInt(line2.trim().split("\\s+")[0]);
                    return Integer.compare(num1, num2);
                } catch (NumberFormatException e) {
                    return line1.compareTo(line2);
                }
            });
        } else {
            Collections.sort(results);
        }

        if (reverse) {
            Collections.reverse(results);
        }

        return CommandResult.success(String.join("\n", results));
    }

    @Override
    public String getUsage() {
        return "sort [-r] [-n] <file>";
    }

    @Override
    public String getDescription() {
        return "Sort lines of a file";
    }
}
