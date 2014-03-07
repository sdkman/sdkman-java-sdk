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

class GvmClient {

    RESTClient restClient

    GvmClient(String apiUrl) {
        this.restClient = new RESTClient(apiUrl)
    }

    List<Candidate> getCandidates() {
        def candidates = []
        try {

            def csv = restClient.get(path: "/candidates").text
            csv.tokenize(',').each { candidates << new Candidate(name:it) }

        } catch(HTTPClientException hce) {
            throw new GvmClientException("Error on retrieving candidates.", hce)
        }
        candidates
    }

    List<Version> getVersionsFor(String candidate) {
        def versions = []
        try {
            def csv = restClient.get(path: "/candidates/$candidate").text
            csv.tokenize(',').each { versions << new Version(name:it) }

        } catch (HTTPClientException hce) {
            throw new GvmClientException("Error on retrieving versions.", hce)
        }
        versions
    }
}
