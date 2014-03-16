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
import wslite.http.HTTPResponse
import wslite.rest.RESTClient
import wslite.rest.Response

class GvmClientSpec extends Specification {

    GvmClient gvmClient

    void setup(){
        def api = "http://dev.gvmtool.net"
        gvmClient = GvmClient.instance(api)
    }

    void "should report alive when api is healthy"() {
        when:
        def alive = gvmClient.isAlive()

        then:
        alive
    }

    void "should report dead when api is unhealthy"() {
        given:
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        def alive = gvmClient.isAlive()

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("foof") }

        and:
        ! alive
    }

    void "should retrieve a list of candidates"() {
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

    void "should get default version for a candidate"() {
        given:
        def candidate = "lazybones"

        when:
        def defaultVersion = gvmClient.getDefaultVersionFor(candidate)

        then:
        defaultVersion.name == "0.6"
    }

    void "should handle communication error on retrieving default version"() {
        given:
        def candidate = "lazybones"
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        def defaultVersion = gvmClient.getDefaultVersionFor(candidate)

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("fazl") }

        and:
        thrown(GvmClientException)
    }

    void "should validate a valid candidate version"() {
        given:
        def candidate = "groovy"
        def version = "2.2.2"

        when:
        def valid = gvmClient.validCandidateVersion(candidate, version)

        then:
        valid
    }

    void "should validate an invalid candidate version"() {
        given:
        def candidate = "groovy"
        def version = "9.9.9"

        when:
        def valid = gvmClient.validCandidateVersion(candidate, version)

        then:
        ! valid
    }

    void "should handle communication error on candidate version validation"() {
        given:
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        gvmClient.validCandidateVersion("", "")

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("pow!") }

        and:
        thrown(GvmClientException)
    }

    void "should get default app version "() {			
        when:
        def defaultVersion = gvmClient.getAppVersion()

        then:
        defaultVersion.name ==~ /^1\.0\.0\+build-\d{3}$/
    }

    void "should handle communication error on retrieving app version"() {
        given:
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        gvmClient.getAppVersion()

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("D'oh!") }

        and:
        thrown(GvmClientException)
    }

    void "should retrieve download url for a candidate version"() {
        given:
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient
        def location = 'http://dl.bintray.com/pledbrook/lazybones-templates/lazybones-0.5.zip'

        def httpResponse = new HTTPResponse(headers: [Location: location])
        def response = new Response(null, httpResponse)

        and:
        def candidate = 'lazybones'
        def version = '0.5'

        when:
        URL url = gvmClient.getDownloadURL(candidate, version)

        then:
        1 * mockRestClient.get({ it.path == '/download/lazybones/0.5'}) >> response

        and:
        url instanceof URL
        url.toString() == location
    }

    void "should handle communication error on candidate version download"() {
        given:
        def candidate = 'lazybones'
        def version = '0.5'
        def mockRestClient = Mock(RESTClient)
        gvmClient.restClient = mockRestClient

        when:
        gvmClient.getDownloadURL(candidate, version)

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException('blamo') }

        and:
        thrown GvmClientException
    }
}
