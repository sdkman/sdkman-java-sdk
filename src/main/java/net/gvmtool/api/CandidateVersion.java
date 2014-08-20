package net.gvmtool.api;

import java.nio.file.Path;

/**
 * @author Noam Y. Tenne
 */
public class CandidateVersion {

    private Path candidateVersionDir;

    public CandidateVersion(Path candidateVersionDir) {
        this.candidateVersionDir = candidateVersionDir;
    }

    public Path dir() {
        return candidateVersionDir;
    }

    public String name() {
        return candidateVersionDir.getFileName().toString();
    }
}
