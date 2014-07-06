package net.gvmtool.api;

/**
 * @author Noam Y. Tenne
 */
public class Gvm {

    public static Use use(String candidateName) {
        return new Use(new Object(), candidateName);
    }

    public static Install install(String candidateName) {
        return new Install(new Object(), candidateName);
    }

    public static Uninstall uninstall(String candidateName) {
        return new Uninstall(new Object(), candidateName);
    }

}
