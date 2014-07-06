package net.gvmtool.api;

/**
 * @author Noam Y. Tenne
 */
public class Gvm {

    public static Object use(String candidateName) {
        return constructUse(candidateName);
    }

    public static Object getUse(String candidateName) {
        return use(candidateName);
    }

    public static Object install(String candidateName) {
        return constructInstall(candidateName);
    }

    public static Object getInstall(String candidateName) {
        return install(candidateName);
    }

    public static Object uninstall(String candidateName) {
        return constructUninstall(candidateName);
    }

    public static Object getUninstall(String candidateName) {
        return uninstall(candidateName);
    }

    private static Object constructUse(String candidateName) {
        return new Object();
    }

    private static Object constructInstall(String candidateName) {
        return new Object();
    }

    private static Object constructUninstall(String candidateName) {
        return new Object();
    }
}
