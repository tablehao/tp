package linuxlingo.shell.command;

import java.util.ArrayList;
import java.util.List;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.FileNode;
import linuxlingo.shell.vfs.RegularFile;
import linuxlingo.shell.vfs.VfsException;

/**
 * Lists directory contents.
 * Supports: ls [-l] [-a] [-R] [path]
 *
 * <p><b>v1.0</b>: Single directory listing with -l (long format) and -a (show hidden).</p>
 * <p><b>v2.0</b>: Adds {@code -R} recursive listing via
 * {@code listDirectory()} and {@code listRecursive()} helpers.</p>
 *
 * <p><b>Owner: B</b></p>
 */
public class LsCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        boolean longFormat = false;
        boolean showHidden = false;
        boolean recursive = false;
        String targetPath = session.getWorkingDir();
        boolean hasExplicitPath = false;

        for (String arg : args) {
            if (arg.startsWith("-") && arg.length() > 1) {
                for (int i = 1; i < arg.length(); i++) {
                    char option = arg.charAt(i);
                    if (option == 'l') {
                        longFormat = true;
                    } else if (option == 'a') {
                        showHidden = true;
                    } else if (option == 'R') {
                        recursive = true;
                    } else {
                        return CommandResult.error("ls: invalid option -- " + option);
                    }
                }
            } else if (!hasExplicitPath) {
                targetPath = arg;
                hasExplicitPath = true;
            } else {
                return CommandResult.error("ls: too many arguments");
            }
        }

        try {
            List<String> lines = new ArrayList<>();
            if (recursive) {
                listRecursive(session, targetPath, longFormat, showHidden, lines);
            } else {
                listDirectory(session, targetPath, longFormat, showHidden, lines);
            }
            return CommandResult.success(String.join("\n", lines));
        } catch (VfsException e) {
            return CommandResult.error("ls: " + e.getMessage());
        }
    }

    /**
     * Lists the contents of a single directory (non-recursive).
     * <p><b>[v2.0 stub]</b></p>
     *
     * @param session    the current shell session
     * @param path       the directory path to list
     * @param longFormat whether to use long listing format
     * @param showHidden whether to include hidden files
     * @param lines      the output list to append results to
     */
    private void listDirectory(ShellSession session, String path,
                               boolean longFormat, boolean showHidden,
                               List<String> lines) {
        List<FileNode> children = session.getVfs().listDirectory(path, session.getWorkingDir(), showHidden);
        for (FileNode child : children) {
            String name = child.getName() + (child.isDirectory() ? "/" : "");
            if (!longFormat) {
                lines.add(name);
                continue;
            }
            int size = child.isDirectory() ? 0 : ((RegularFile) child).getSize();
            lines.add(child.getPermission().toString() + "  " + size + "  " + name);
        }
    }

    /**
     * Recursively lists directory contents, printing a header for each directory.
     * <p><b>[v2.0 stub]</b></p>
     *
     * @param session    the current shell session
     * @param path       the directory path to list recursively
     * @param longFormat whether to use long listing format
     * @param showHidden whether to include hidden files
     * @param lines      the output list to append results to
     */
    private void listRecursive(ShellSession session, String path,
                               boolean longFormat, boolean showHidden,
                               List<String> lines) {
        String absolutePath = session.getVfs().getAbsolutePath(path, session.getWorkingDir());
        lines.add(absolutePath + ":");

        List<FileNode> children = session.getVfs().listDirectory(path, session.getWorkingDir(), showHidden);
        List<String> childLines = new ArrayList<>();
        List<String> subDirectories = new ArrayList<>();

        for (FileNode child : children) {
            String name = child.getName() + (child.isDirectory() ? "/" : "");
            if (!longFormat) {
                childLines.add(name);
            } else {
                int size = child.isDirectory() ? 0 : ((RegularFile) child).getSize();
                childLines.add(child.getPermission().toString() + "  " + size + "  " + name);
            }
            if (child.isDirectory()) {
                subDirectories.add(child.getAbsolutePath());
            }
        }

        lines.addAll(childLines);
        for (String subDirectoryPath : subDirectories) {
            lines.add("");
            listRecursive(session, subDirectoryPath, longFormat, showHidden, lines);
        }
    }

    @Override
    public String getUsage() {
        return "ls [-l] [-a] [-R] [path]";
    }

    @Override
    public String getDescription() {
        return "List directory contents";
    }
}
