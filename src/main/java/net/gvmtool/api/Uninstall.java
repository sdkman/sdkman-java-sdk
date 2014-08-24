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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Noam Y. Tenne
 */
public class Uninstall {
    private Context context;
    private String candidateName;
    private Candidates candidates = new Candidates();

    public Uninstall(Context context, String candidateName) {

        this.context = context;
        this.candidateName = candidateName;
    }

    public void version(String version) {
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("No valid candidate version was provided.");
        }
        
        Path candidateDir = candidates.get(context, candidateName);
        if (context.candidateHasCurrentVersion(candidateDir)) {
            try {
                Files.delete(context.candidateCurrentVersion(candidateDir));
            } catch (IOException e) {
                throw new RuntimeException("Error removing existing current symlink for candidate " + candidateName, e);
            }
        }

        if (context.candidateVersionInstalled(candidateDir, version) &&
                context.candidateVersionIsDir(candidateDir, version)) {
            try {
                Files.delete(context.candidateVersionDir(candidateDir, version));
            } catch (IOException e) {
                throw new RuntimeException("Error removing " + candidateName + " " + version, e);
            }
        }
    }
}