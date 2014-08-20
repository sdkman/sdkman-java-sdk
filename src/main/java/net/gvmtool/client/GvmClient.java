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
package net.gvmtool.client;

import wslite.http.HTTPClientException;
import wslite.http.HTTPResponse;
import wslite.rest.RESTClient;
import wslite.rest.RESTClientException;
import wslite.rest.Response;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Noam Y. Tenne
 */
public class GvmClient {

    static final String DEFAULT_API = "http://api.gvmtool.net";

    RESTClient restClient;

    private GvmClient() {
    }

    static GvmClient instance() {
        return instance(DEFAULT_API);
    }

    static GvmClient instance(String apiUrl) {
        GvmClient gvmClient = new GvmClient();
        gvmClient.restClient = new RESTClient(apiUrl);
        return gvmClient;
    }

    List<Candidate> getCandidates() throws GvmClientException {
        List<Candidate> candidates = new ArrayList<>();
        String csv = getText("/candidates");
        String[] splitCsv = csv.split(",");
        for (String token : splitCsv) {
            candidates.add(new Candidate(token));
        }
        return candidates;
    }

    List<Version> getVersionsFor(String candidate) throws GvmClientException {
        List<Version> versions = new ArrayList<>();
        String csv = getText("/candidates/" + candidate);
        String[] splitCsv = csv.split(",");
        for (String token : splitCsv) {
            versions.add(new Version(token));
        }
        return versions;
    }

    Boolean validCandidateVersion(String candidate, String version) throws GvmClientException {
        String status = getText("/candidates/" + candidate + "/" + version);
        return "valid".equals(status);
    }

    Boolean isAlive() throws GvmClientException {
        try {
            String alive = getText("/alive");
            return "OK".equals(alive);

        } catch (GvmClientException gce) {
            return false;
        }
    }

    Version getDefaultVersionFor(String candidate) throws GvmClientException {
        String defaultVersion = getText("/candidates/" + candidate + "/default");
        return new Version(defaultVersion);
    }

    Version getAppVersion() throws GvmClientException {
        String defaultVersion = getText("/app/version");
        return new Version(defaultVersion);
    }

    URL getDownloadURL(String candidate, String version) throws GvmClientException {
        HTTPResponse response = get("/candidates/" + candidate + "/" + version + "/download").getResponse();
        String location = response.getHeaders().get("Location").toString();
        try {
            return new URL(location);
        } catch (MalformedURLException e) {
            throw new GvmClientException("Problems creating download URL", e);
        }
    }

    private String getText(String path) throws GvmClientException {
        return new String(get(path).getData());
    }

    private Response get(String path) throws GvmClientException {
        Map params = new HashMap<>();
        params.put("path", path);
        params.put("followRedirects", true);
        try {
            return restClient.get(params);
        } catch (Throwable t) {
            throw new GvmClientException("Problems communicating with: " + path, t);
        }
    }
}
