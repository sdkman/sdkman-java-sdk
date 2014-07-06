package net.gvmtool.api;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Noam Y. Tenne
 */
public class Candidates {

    private Object context;

    public Candidates(Object context) {
        this.context = context;
    }

    Path get(String candidateName) {
        if (StringUtils.isBlank(candidateName)) {
            throw new IllegalArgumentException("No candidate name was provided");
        }

        //TODO: get candidate context
//        return context.candidateDir(name);
        return Paths.get("context", candidateName);
    }
}