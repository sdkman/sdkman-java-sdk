package net.gvmtool.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Noam Y. Tenne
 */
public class Install {
    private Context context;
    private String candidateName;

    public Install(Context context, String candidateName) {
        this.context = context;
        this.candidateName = candidateName;
    }

    public Path version(String version, GvmOption...gvmOptions) {
        Path candidateDir = new Candidates(context).get(candidateName);

        GvmOptions options = new GvmOptions(gvmOptions);
        CandidateVersion candidateVersion = new CandidateVersions(context, candidateDir).determine(version, options);

        if (Files.exists(candidateVersion.dir())) {
            return candidateVersion.dir();
        }

        CandidateInstaller candidateInstaller = new CandidateInstaller();
        Path versionDir = candidateInstaller.installCandidateVersion(context, candidateName, version);
        if (options.isDefault()) {
            Path currentVersion = context.candidateCurrentVersion(candidateDir);
            try {
                Files.deleteIfExists(currentVersion);
            } catch (IOException e) {
                throw new RuntimeException("Error removing existing current symlink for candidate " + candidateName, e);
            }
            try {
                return Files.createSymbolicLink(currentVersion, versionDir);
            } catch (IOException e) {
                throw new RuntimeException("Error creating current symlink for candidate " + candidateName + " and version " + candidateVersion.name(), e);
            }
        }
        return versionDir;
    }
}
