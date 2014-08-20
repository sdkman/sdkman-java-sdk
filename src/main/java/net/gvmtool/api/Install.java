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
    private Candidates candidates = new Candidates();
    private CandidateVersions candidateVersions = new CandidateVersions();
    private CandidateInstaller candidateInstaller = new CandidateInstaller();

    public Install(Context context, String candidateName) {
        this.context = context;
        this.candidateName = candidateName;
    }

    public Path version(String version, GvmOption...gvmOptions) {
        Path candidateDir = candidates.get(context, candidateName);
        GvmOptions options = new GvmOptions(gvmOptions);
        CandidateVersion candidateVersion = candidateVersions.determine(context, candidateDir, version, options);

        if (Files.exists(candidateVersion.dir())) {
            return candidateVersion.dir();
        }

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
