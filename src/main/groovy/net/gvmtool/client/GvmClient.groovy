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
package net.gvmtool.client

import wslite.http.HTTPClientException
import wslite.rest.RESTClient

final class GvmClient {

    static final DEFAULT_API = "http://api.gvmtool.net"

    RESTClient restClient

    private GvmClient(){}

    static GvmClient instance(String apiUrl = DEFAULT_API){
        new GvmClient(restClient: new RESTClient(apiUrl))
    }

    List<Candidate> getCandidates() throws GvmClientException {
        def candidates = []
        def csv = call("/candidates")
        csv.tokenize(',').each { candidates << new Candidate(name:it) }
        candidates
    }

    List<Version> getVersionsFor(String candidate) throws GvmClientException {
        def versions = []
        def csv = call("/candidates/$candidate")
        csv.tokenize(',').each { versions << new Version(name:it) }
        versions
    }

    Boolean validCandidateVersion(String candidate, String version) throws GvmClientException {
        def status = call("/candidates/$candidate/$version")
        status == "valid" ? true : false
    }

    Boolean isAlive() throws GvmClientException {
        try {
            def alive = call("/alive")
            return alive == "OK" ? true : false

        } catch (GvmClientException gce) {
            return false
        }
    }

    Version getDefaultVersionFor(String candidate) throws GvmClientException {
        def defaultVersion = call("/candidates/$candidate/default")
        new Version(name: defaultVersion)
    }

    Version getAppVersion() throws GvmClientException {
        def  defaultVersion = call("/app/version")
        new Version(name: defaultVersion)
    }

    private String call(String path) throws GvmClientException {
        try {
            restClient.get(path: path).text

        } catch (HTTPClientException hce) {
            throw new GvmClientException("Problems communicating with: $path", hce)
        }
    }

}
