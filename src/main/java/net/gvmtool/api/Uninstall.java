package net.gvmtool.api;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Noam Y. Tenne
 */
public class Uninstall {
    private Context context;
    private String candidateName;

    public Uninstall(Context context, String candidateName) {

        this.context = context;
        this.candidateName = candidateName;
    }

    public void version(String version) {
        Path candidateDir = new Candidates(context).get(candidateName);

        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("No valid candidate version was provided.");
        }

        if (context.candidateHasCurrentVersion(candidateDir)) {
            try {
                Files.delete(context.candidateCurrentVersion(candidateDir));
            } catch (IOException e) {
                throw new RuntimeException("Error removing existing current symlink for candidate " + candidateName, e);
            }
        }

        if (context.candidateVersionInstalled(candidateDir, version) &&
                context.candidateVersionIsDir(candidateDir, version)) {
            try {
                Files.delete(context.candidateVersionDir(candidateDir, version));
            } catch (IOException e) {
                throw new RuntimeException("Error removing " + candidateName + " " + version, e);
            }
        }
    }
}