package net.gvmtool.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Noam Y. Tenne
 */
public class Use {

    private Object context;
    private String candidateName;

    public Use(Object context, String candidateName) {
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
                //TODO
//                candidateInstaller.installCandidateVersion(context, name, candidateVersion.name)
                return Paths.get("context", candidateVersion.name());
            } else {
                throw new RuntimeException(candidateName + " " + version + " is not installed");
            }
        }
    }
}
