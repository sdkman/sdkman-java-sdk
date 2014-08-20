package net.gvmtool.api;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

/**
 * @author Noam Y. Tenne
 */
public class CandidateVersions {

    private Context context;
    private Path candidateDir;

    public CandidateVersions(Context context, Path candidateDir) {
        this.context = context;
        this.candidateDir = candidateDir;
    }

    CandidateVersion determine(String versionName, GvmOptions options) {
        String candidateName = candidateDir.getFileName().toString();

        if (options.isOffline()) {
            if (StringUtils.isNotBlank(versionName)) {
                if (context.candidateVersionInstalled(candidateDir, versionName)) {
                    return version(context, candidateDir, versionName);
                } else {
                    throw new RuntimeException("Not available offline");
                }
            } else {
                if (context.candidateHasCurrentVersion(candidateDir)) {
                    Path resolvedCurrentDir = context.candidateResolveCurrentDir(candidateDir);
                    return version(context, candidateDir, resolvedCurrentDir.getFileName().toString());
                } else {
                    throw new RuntimeException("Not available offline");
                }
            }
        } else {
            if (StringUtils.isBlank(versionName)) {
//                Version defaultVersion = null;
//                try {
//                    defaultVersion = context.client.getDefaultVersionFor(candidateName);
//                } catch (GvmClientException e) {
//                    throw new RuntimeException("Error getting default version for " + candidateName, e);
//                }
                return version(context, candidateDir, "default");
            } else {
                boolean versionValid = false;
//                try {
//                    versionValid = context.client.validCandidateVersion(candidateName, versionName);
//                } catch (GvmClientException e) {
//                    throw new RuntimeException("Error validating version " + versionName + " of " + candidateName, e);
//                }
                if (versionValid) {
                    return version(context, candidateDir, versionName);
                }
                if (context.candidateVersionIsSymlink(candidateDir, versionName)) {
                    return version(context, candidateDir, versionName);
                }
                if (context.candidateVersionIsDir(candidateDir, versionName)) {
                    return version(context, candidateDir, versionName);
                }

                throw new RuntimeException(versionName + " is not a valid " + candidateName + " version.");
            }
        }
    }

    private CandidateVersion version(Context context, Path candidateDir, String versionName) {
        Path candidateVersionDir = context.candidateVersionDir(candidateDir, versionName);
        return new CandidateVersion(candidateVersionDir);
    }
}
