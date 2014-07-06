package net.gvmtool.api;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Noam Y. Tenne
 */
public class CandidateVersions {

    private Object context;
    private Path candidateDir;

    public CandidateVersions(Object context, Path candidateDir) {
        this.context = context;
        this.candidateDir = candidateDir;
    }

    CandidateVersion determine(String versionName, GvmOptions options) {
        String candidateName = candidateDir.getFileName().toString();

        if (options.isOffline()) {
            if (StringUtils.isNotBlank(versionName)) {
//TODO
//                if (context.candidateVersionInstalled(candidateDir, versionName)) {
                if (true) {
                    return version(context, candidateDir, versionName);
                } else {
                    throw new RuntimeException("Not available offline");
                }
            } else {
//TODO
//                if (context.candidateHasCurrentVersion(candidateDir)) {
                if (true) {
//                    Path resolvedCurrentDir = context.candidateResolveCurrentDir(candidateDir)
                    Path resolvedCurrentDir = Paths.get("context", "current");
                    return version(context, candidateDir, resolvedCurrentDir.getFileName().toString());
                } else {
                    throw new RuntimeException("Not available offline");
                }
            }
        } else {
            if (StringUtils.isBlank(versionName)) {
                //TODO
                //    return version(context, candidateDir, context.service.defaultVersion(candidateName))
                return version(context, candidateDir, "String");
            } else {
                boolean versionValid = true;

//TODO
//                boolean versionValid = context.service.validCandidateVersion(candidateName, versionName)
                if (versionValid) {
                    return version(context, candidateDir, versionName);
                }
//TODO
//                if (context.candidateVersionIsSymlink(candidateDir, versionName)) {
                if (true) {
                    return version(context, candidateDir, versionName);
                }
//TODO
//                if (context.candidateVersionIsDir(candidateDir, versionName)) {
                if (true) {
                    return version(context, candidateDir, versionName);
                }

                throw new RuntimeException(versionName+" is not a valid "+candidateName+" version.");
            }
        }
    }

    private CandidateVersion version(Object context, Path candidateDir, String versionName) {
        //TODO
//        Path candidateVersionDir = context.candidateVersionDir(candidateDir, name)
        Path candidateVersionDir = Paths.get("context", versionName);
        return new CandidateVersion(context, candidateVersionDir);
    }
}
