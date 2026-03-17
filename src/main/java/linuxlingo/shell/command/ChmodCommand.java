package linuxlingo.shell.command;

import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.FileNode;
import linuxlingo.shell.vfs.Permission;
import linuxlingo.shell.vfs.VfsException;

/**
 * Changes file permissions.
 * Supports both octal (e.g., 755) and symbolic (e.g., u+x) notation.
 * Syntax: chmod &lt;mode&gt; &lt;file&gt;
 *
 * <p><b>Owner: C</b></p>
 */
public class ChmodCommand implements Command {
    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        if (args.length != 2) {
            return CommandResult.error("chmod: " + getUsage());
        }

        String mode = args[0];
        String file = args[1];

        boolean isOctal = mode.matches("[0-7]{3}");
        boolean isSymbolic = mode.matches("[ugoa]+[+-=][rwx]+");
        if (!isOctal && !isSymbolic) {
            return CommandResult.error("chmod: invalid mode: " + mode);
        }

        try {
            FileNode node = session.getVfs().resolve(file, session.getWorkingDir());
            Permission newPerm;

            if (isOctal) {
                newPerm = Permission.fromOctal(mode);
            } else {
                newPerm = Permission.fromSymbolic(mode, node.getPermission());
            }

            node.setPermission(newPerm);
            return CommandResult.success("");
        } catch (VfsException e) {
            return CommandResult.error("chmod: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "chmod <mode> <file>";
    }

    @Override
    public String getDescription() {
        return "Change file permissions";
    }
}