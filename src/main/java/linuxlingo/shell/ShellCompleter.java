package linuxlingo.shell;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import linuxlingo.shell.vfs.FileNode;

/**
 * JLine completer for the LinuxLingo shell.
 *
 * <p><b>Owner: B — stub; to be implemented.</b></p>
 *
 * <p>Provides tab-completion for:</p>
 * <ul>
 *   <li>Command names (from {@link CommandRegistry})</li>
 *   <li>VFS file/directory paths (absolute and relative)</li>
 * </ul>
 *
 * TODO: Member B should implement:
 * - complete() method integrating with JLine
 * - completeCommandName() for command name and alias completion
 * - completePath() for VFS path completion
 */
public class ShellCompleter implements Completer {
    private final ShellSession session;

    public ShellCompleter(ShellSession session) {
        this.session = session;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String currentWord = line.word() == null ? "" : line.word();
        if (line.wordIndex() == 0) {
            completeCommandName(currentWord, candidates);
        } else {
            completePath(currentWord, candidates);
        }
    }

    /**
     * Add matching command names to the candidate list.
     *
     * @param prefix the partial command name typed so far
     * @param candidates the candidate list to populate
     */
    void completeCommandName(String prefix, List<Candidate> candidates) {
        for (String name : getCommandCompletions(prefix)) {
            candidates.add(new Candidate(name));
        }
    }

    /**
     * Add matching VFS paths to the candidate list.
     *
     * @param partial the partial path typed so far
     * @param candidates the candidate list to populate
     */
    void completePath(String partial, List<Candidate> candidates) {
        for (String path : getPathCompletions(partial)) {
            candidates.add(new Candidate(path));
        }
    }

    /**
     * Get command name completions for the given prefix.
     * Useful for testing without JLine Candidate objects.
     *
     * @param prefix the partial command name
     * @return sorted set of matching command names
     */
    public SortedSet<String> getCommandCompletions(String prefix) {
        String normalizedPrefix = prefix == null ? "" : prefix;
        SortedSet<String> completions = new TreeSet<>();

        for (String commandName : session.getRegistry().getAllNames()) {
            if (commandName.startsWith(normalizedPrefix)) {
                completions.add(commandName);
            }
        }

        for (String aliasName : session.getAliases().keySet()) {
            if (aliasName.startsWith(normalizedPrefix)) {
                completions.add(aliasName);
            }
        }

        return completions;
    }

    /**
     * Get path completions for the given partial path.
     * Useful for testing without JLine Candidate objects.
     *
     * @param partial the partial path
     * @return sorted set of matching paths
     */
    public SortedSet<String> getPathCompletions(String partial) {
        String normalizedPartial = partial == null ? "" : partial;
        int lastSlash = normalizedPartial.lastIndexOf('/');

        String directoryPart;
        String namePrefix;
        if (lastSlash < 0) {
            directoryPart = ".";
            namePrefix = normalizedPartial;
        } else if (lastSlash == 0) {
            directoryPart = "/";
            namePrefix = normalizedPartial.substring(1);
        } else {
            directoryPart = normalizedPartial.substring(0, lastSlash);
            namePrefix = normalizedPartial.substring(lastSlash + 1);
        }

        SortedSet<String> completions = new TreeSet<>();
        try {
            List<FileNode> children = session.getVfs().listDirectory(
                    directoryPart, session.getWorkingDir(), true);
            for (FileNode child : children) {
                String childName = child.getName();
                if (!childName.startsWith(namePrefix)) {
                    continue;
                }
                if (!namePrefix.startsWith(".") && childName.startsWith(".")) {
                    continue;
                }

                String candidateValue;
                if (lastSlash < 0) {
                    candidateValue = childName;
                } else if (lastSlash == 0) {
                    candidateValue = "/" + childName;
                } else {
                    candidateValue = directoryPart + "/" + childName;
                }

                if (child.isDirectory()) {
                    candidateValue += "/";
                }
                completions.add(candidateValue);
            }
        } catch (RuntimeException e) {
            return completions;
        }

        return completions;
    }
}
