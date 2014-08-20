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
