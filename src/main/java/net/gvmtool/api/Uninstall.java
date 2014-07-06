package net.gvmtool.api;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Noam Y. Tenne
 */
public class Uninstall {
    private Object context;
    private String candidateName;

    public Uninstall(Object context, String candidateName) {

        this.context = context;
        this.candidateName = candidateName;
    }

    public void version(String version, GvmOption... gvmOptions) {
        Path candidateDir = new Candidates(context).get(candidateName);

        GvmOptions options = new GvmOptions(gvmOptions);
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("No valid candidate version was provided.");
        }

        //TODO
//        if (context.candidateHasCurrentVersion(candidateDir)) {
        if (true) {
//            Files.delete(context.candidateCurrentVersion(candidateDir))
        }

//        if (context.candidateVersionInstalled(candidateDir, versionName) &&
//                context.candidateVersionIsDir(candidateDir, versionName)) {
//            context.candidateVersionDir(candidateDir, versionName).toFile().deleteDir()
//        }
    }
}