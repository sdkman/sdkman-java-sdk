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

import spock.lang.Specification
import wslite.http.HTTPClientException
import wslite.rest.RESTClient

class GvmClientSpec extends Specification {

    GvmClient gvmClient

    void setup(){
        def apiUrl = "http://dev.gvmtool.net"
        gvmClient = new GvmClient(apiUrl)
    }

    void "should retrieve a list of candidates"() {
        given:
        def candidates = "gaiden,gradle,grails,griffon,groovy,groovyserv,lazybones,play,springboot,vertx"

        when:
        List<Candidate> results = gvmClient.getCandidates()

        then:
        results.find { it.name == "groovy" }
        results.find { it.name == "grails" }
    }

    void "should handle communication error on retrieving of candidates"() {
        given:
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        gvmClient.getCandidates()

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("boom")}

        and:
        thrown(GvmClientException)
    }

    void "should retrieve all versions for existing candidate"() {
        given:
        def candidate = "groovy"

        when:
        List<Version> versions = gvmClient.getVersionsFor(candidate)

        then:
        versions.find { it.name == "2.2.1" }
        versions.find { it.name == "2.1.9" }
    }

    void "should handle communication error on retrieving candidate versions"() {
        given:
        def candidate = "groovy"
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        gvmClient.getVersionsFor(candidate)

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("boom")}

        and:
        thrown(GvmClientException)
    }

}
