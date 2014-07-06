package net.gvmtool.api;

import java.nio.file.Path;

/**
 * @author Noam Y. Tenne
 */
public class CandidateVersion {

    private Object context;
    private Path candidateVersionDir;

    public CandidateVersion(Object context, Path candidateVersionDir) {
        this.context = context;
        this.candidateVersionDir = candidateVersionDir;
    }

    public Path dir() {
        return candidateVersionDir;
    }

    public String name() {
        return candidateVersionDir.getFileName().toString();
    }
}
