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