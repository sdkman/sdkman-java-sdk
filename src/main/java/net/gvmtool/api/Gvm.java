package net.gvmtool.api;

/**
 * @author Noam Y. Tenne
 */
public class Gvm {

    public static Object use(String candidateName) {
        return new Use(new Object(), candidateName);
    }

    public static Object install(String candidateName) {
        return new Object();
    }

    public static Object uninstall(String candidateName) {
        return new Object();
    }

}
