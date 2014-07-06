package net.gvmtool.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TODO: Documentation
 *
 * @author Shay Bagants
 */
public class Context {

    public Path gvmHomeDir;
    public GvmHttpClient service;

    public static Context get() throws Exception {
        Context context = new Context();
        context.gvmHomeDir = Paths.get(System.getProperty("user.home"), ".gvm");

        if (Files.notExists(context.gvmHomeDir) || !Files.isDirectory(context.gvmHomeDir)) {
            throw new Exception("Cannot find the GVM home directory at ${context.gvmHomeDir}");
        }
        if (!Files.isReadable(context.gvmHomeDir)) {
            throw new Exception("Cannot read the GVM home directory at ${context.gvmHomeDir}");
        }
        context.service = new GvmHttpClient();//need to import/implement the GVMHTTPclient in Java
        return context;
    }

    public Path archives() {
        return gvmHomeDir.resolve("archives");
    }

    public Path tmp(){
        return gvmHomeDir.resolve("tmp");
    }

    public Path candidateDir(String candidateName){
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
        try{
            return Files.readSymbolicLink(candidateCurrentVersion(candidateDir));
        }
        catch (IOException e){
            throw new RuntimeException("Unable to resolve symbolyc link for"+candidateDir.toString(),e);
        }

    }
    public Path candidateCurrentVersion(Path candidateDir) {
        return candidateVersionDir(candidateDir, "current");
    }
    public Path candidateArchive(String candidateName, String versionName) {
        return archives().resolve("$candidateName-${versionName}.zip");
    }



}
