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

import net.gvmtool.client.GvmClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Shay Bagants
 */
public class Context {

    public Path gvmHomeDir;
    public GvmClient client;

    public static Context get() {
        Context context = new Context();
        context.gvmHomeDir = Paths.get(System.getProperty("user.home"), ".gvm");

        if (Files.notExists(context.gvmHomeDir) || !Files.isDirectory(context.gvmHomeDir)) {
            throw new RuntimeException("Cannot find the GVM home directory at " + context.gvmHomeDir);
        }
        if (!Files.isReadable(context.gvmHomeDir)) {
            throw new RuntimeException("Cannot read the GVM home directory at " + context.gvmHomeDir);
        }
        context.client = GvmClient.instance();
        return context;
    }

    public Path archives() {
        return gvmHomeDir.resolve("archives");
    }

    public Path tmp() {
        return gvmHomeDir.resolve("tmp");
    }

    public Path candidateDir(String candidateName) {
        return gvmHomeDir.resolve(candidateName);
    }

    public boolean candidateVersionInstalled(Path candidateDir, String versionName) {
        return Files.exists(candidateVersionDir(candidateDir, versionName));
    }

    public boolean candidateVersionIsSymlink(Path candidateDir, String versionName) {
        return Files.isSymbolicLink(candidateVersionDir(candidateDir, versionName));
    }

    public boolean candidateVersionIsDir(Path candidateDir, String versionName) {
        return Files.isDirectory(candidateVersionDir(candidateDir, versionName));
    }

    public Path candidateVersionDir(Path candidateDir, String versionName) {
        return candidateDir.resolve(versionName);
    }

    public boolean candidateHasCurrentVersion(Path candidateDir) {
        return candidateVersionIsSymlink(candidateDir, "current");
    }

    public Path candidateResolveCurrentDir(Path candidateDir) {
        try {
            return Files.readSymbolicLink(candidateCurrentVersion(candidateDir));
        } catch (IOException e) {
            throw new RuntimeException("Unable to resolve symbolic link for" + candidateDir.toString(), e);
        }
    }

    public Path candidateCurrentVersion(Path candidateDir) {
        return candidateVersionDir(candidateDir, "current");
    }

    public Path candidateArchive(String candidateName, String versionName) {
        return archives().resolve(candidateName + "-" + versionName + ".zip");
    }
}
