package net.gvmtool.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Noam Y. Tenne
 */
public class Use {

    private Context context;
    private String candidateName;

    public Use(Context context, String candidateName) {
        this.context = context;
        this.candidateName = candidateName;
    }

    public Path version(String version, GvmOption...gvmOptions) {
        GvmOptions options = new GvmOptions(gvmOptions);

        Path candidateDir = new Candidates(context).get(candidateName);

        CandidateVersion candidateVersion = new CandidateVersions(context, candidateDir).determine(version, options);

        if (Files.exists(candidateVersion.dir())) {
            return candidateVersion.dir();
        } else {
            if (options.isInstall()) {
                CandidateInstaller candidateInstaller = new CandidateInstaller();
                candidateInstaller.installCandidateVersion(context, candidateName, version);
                return Paths.get("context", candidateVersion.name());
            } else {
                throw new RuntimeException(candidateName + " " + version + " is not installed");
            }
        }
    }
}