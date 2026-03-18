package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.List;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VfsException;

/**
 * Removes adjacent duplicate lines.
 * Syntax: uniq [-c] &lt;file&gt;
 *
 * <p><b>Owner: C</b></p>
 */
public class UniqCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        boolean countOccurrences = false;
        String file = null;

        for (String arg : args) {
            if (arg.equals("-c")) {
                countOccurrences = true;
            } else if (!arg.startsWith("-") && file == null) {
                file = arg;
            }
        }

        String content;
        if (file != null) {
            try {
                content = session.getVfs().readFile(file, session.getWorkingDir());
            } catch (VfsException e) {
                return CommandResult.error("uniq: " + e.getMessage());
            }
        } else if (stdin != null) {
            content = stdin;
        } else {
            return CommandResult.error("uniq: missing file operand");
        }

        if (content.isEmpty()) {
            return CommandResult.success("");
        }

        String[] linesArray = content.split("\n", -1);
        List<String> results = new ArrayList<>();

        if (linesArray.length > 0) {
            String currentLine = linesArray[0];
            int count = 1;

            for (int i = 1; i < linesArray.length; i++) {
                if (linesArray[i].equals(currentLine)) {
                    count++;
                } else {
                    if (countOccurrences) {
                        results.add(String.format("%7d %s", count, currentLine));
                    } else {
                        results.add(currentLine);
                    }
                    currentLine = linesArray[i];
                    count = 1;
                }
            }

            if (countOccurrences) {
                results.add(String.format("%7d %s", count, currentLine));
            } else {
                results.add(currentLine);
            }
        }

        return CommandResult.success(String.join("\n", results));
    }

    @Override
    public String getUsage() {
        return "uniq [-c] <file>";
    }

    @Override
    public String getDescription() {
        return "Remove adjacent duplicate lines";
    }
}
