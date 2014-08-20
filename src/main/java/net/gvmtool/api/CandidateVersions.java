/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
