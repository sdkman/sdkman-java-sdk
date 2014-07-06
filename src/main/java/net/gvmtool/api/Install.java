package net.gvmtool.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Noam Y. Tenne
 */
public class Install {
    private Object context;
    private String candidateName;

    public Install(Object context, String candidateName) {
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

        //TODO
//        def versionDir = candidateInstaller.installCandidateVersion(context, name, version.name)
        Path versionDir = Paths.get("context", candidateVersion.name());
        if (options.isDefault()) {
            //TODO
//            def currentVersion = context.candidateCurrentVersion(candidateDir)
            Path currentVersion = Paths.get("context", "current");
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
