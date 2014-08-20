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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Shay Bagants
 */
public class CandidateInstaller {

    public Path installCandidateVersion(Context context, String name, String version) {

        Path archive = getArchive(context, name, version);

        Path tempDir = context.tmp();
        File file = archive.toFile();

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(file))) {
            copyZipContentToTemp(tempDir, zipIn);
        } catch (IOException e) {
            throw new RuntimeException(file.getAbsolutePath() + " Not found", e);
        }

        String extractedDirName = findExtractedDirectoryName(version, tempDir);
        Path candidateDir = context.candidateDir(name);
        Path versionDir = context.candidateVersionDir(candidateDir, version);
        try {
            return Files.move(tempDir.resolve(extractedDirName), versionDir, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Unable to move extracted directory " + extractedDirName + " to " + versionDir, e);
        }
    }

    private String findExtractedDirectoryName(String version, Path tempDir) {
        String[] tmpDirFileList = tempDir.toFile().list();
        String extractedDirName = "";
        for (String aTmpDirFileList : tmpDirFileList) {

            if (aTmpDirFileList.endsWith(version)) {
                extractedDirName = aTmpDirFileList;
                break;
            }

        }
        return extractedDirName;
    }

    private void copyZipContentToTemp(Path tempDir, ZipInputStream zipIn) throws IOException {
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {

            if (!entry.isDirectory()) {
                Path entryPath = tempDir.resolve(entry.getName());
                Files.createDirectories(entryPath.getParent());
                File entryFile = entryPath.toFile();

                try (OutputStream ous = new FileOutputStream(entryFile)) {
                    copyFromZipEntryToOutput(zipIn, ous);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Unable to write to temp file " + entryFile.getAbsolutePath(), e);
                }
                entryFile.setExecutable(true, true);
                entryFile.setReadable(true, true);
                entryFile.setWritable(true, true);
            }
        }
    }

    private Path getArchive(Context context, String name, String version) {
        Path archive = context.candidateArchive(name, version);
        if (!Files.exists(archive)) {
            try {
                Files.createDirectories(context.archives());
                //TODO: download archive
//                archive = context.service.downloadCandidate(context, name, version);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create directory " + archive.toString(), e);
            }
        }
        return archive;
    }

    private void copyFromZipEntryToOutput(ZipInputStream zipIn, OutputStream ous) throws IOException {
        int len = 0;
        byte[] buffer = new byte[4096];
        while ((len = zipIn.read(buffer)) > 0) {
            ous.write(buffer, 0, len);
        }
    }
}
