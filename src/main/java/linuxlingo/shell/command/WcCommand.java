package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.List;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VfsException;

/**
 * Counts lines, words, and/or characters in a file.
 * Syntax: wc [-l] [-w] [-c] &lt;file&gt;
 *
 * <p><b>Owner: C</b></p>
 */
public class WcCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        boolean countLines = false;
        boolean countWords = false;
        boolean countChars = false;

        String file = null;

        for (String arg : args) {
            if (arg.equals("-l")) {
                countLines = true;
            } else if (arg.equals("-w")) {
                countWords = true;
            } else if (arg.equals("-c")) {
                countChars = true;
            } else if (!arg.startsWith("-") && file == null) {
                file = arg;
            } else {
                return CommandResult.error("wc: " + getUsage());
            }
        }

        if (!countLines && !countWords && !countChars) {
            countLines = true;
            countWords = true;
            countChars = true;
        }

        String content;
        if (file != null) {
            try {
                content = session.getVfs().readFile(file, session.getWorkingDir());
            } catch (VfsException e) {
                return CommandResult.error("wc: " + e.getMessage());
            }
        } else if (stdin != null) {
            content = stdin;
        } else {
            return CommandResult.error("wc: missing file operand");
        }

        int lines = content.isEmpty() ? 0 : content.split("\n", -1).length;
        int words = content.isBlank() ? 0 : content.trim().split("\\s+").length;
        int chars = content.length();

        List<String> results = new ArrayList<>();
        if (countLines) results.add(String.valueOf(lines));
        if (countWords) results.add(String.valueOf(words));
        if (countChars) results.add(String.valueOf(chars));
        if (file != null) results.add(file);

        return CommandResult.success(String.join(" ", results));
    }

    @Override
    public String getUsage() {
        return "wc [-l] [-w] [-c] <file>";
    }

    @Override
    public String getDescription() {
        return "Count lines, words, or characters";
    }
}
