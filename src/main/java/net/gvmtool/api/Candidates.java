package net.gvmtool.api;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Noam Y. Tenne
 */
public class Candidates {

    private Context context;

    public Candidates(Context context) {
        this.context = context;
    }

    Path get(String candidateName) {
        if (StringUtils.isBlank(candidateName)) {
            throw new IllegalArgumentException("No candidate name was provided");
        }

        return context.candidateDir(candidateName);
    }
}