package net.gvmtool.api;

/**
 * @author Noam Y. Tenne
 */
public class Gvm {

    public static Use use(String candidateName) {
        return new Use(Context.get(), candidateName);
    }

    public static Install install(String candidateName) {
        return new Install(Context.get(), candidateName);
    }

    public static Uninstall uninstall(String candidateName) {
        return new Uninstall(Context.get(), candidateName);
    }
}
