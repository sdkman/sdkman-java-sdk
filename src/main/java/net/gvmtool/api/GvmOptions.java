package net.gvmtool.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Noam Y. Tenne
 */
public class GvmOptions {

    private final Set<GvmOption> options;

    public GvmOptions(GvmOption[] gvmOptions) {
        options = new HashSet<>();
        Collections.addAll(options, gvmOptions);
    }

    public boolean isInstall() {
        return options.contains(GvmOption.INSTALL);
    }

    public boolean isDefault() {
        return options.contains(GvmOption.DEFAULT);
    }

    public boolean isOffline() {
        return options.contains(GvmOption.OFFLINE);
    }
}
