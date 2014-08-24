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

    RESTClient mockRestClient = Mock()
    Response mockResponse = Mock()

    void setup(){
        gvmClient = GvmClient.instance()
        gvmClient.restClient = mockRestClient
    }

    void "should report alive when api is healthy"() {
        when:
        def alive = gvmClient.isAlive()

        then:
        1 * mockResponse.getData() >> "OK".bytes
        1 * mockRestClient.get({it.path == '/alive'}) >> mockResponse

        and:
        alive
    }

    void "should report dead when api is unhealthy"() {
        when:
        def alive = gvmClient.isAlive()

        then:
        mockRestClient.get({it.path == '/alive'}) >> { throw new HTTPClientException("foof") }

        and:
        ! alive
    }

    void "should retrieve a list of candidates"() {
        when:
        List<Candidate> results = gvmClient.getCandidates()

        then:
        1 * mockResponse.getData() >> "grails,groovy".bytes
        1 * mockRestClient.get({it.path == '/candidates'}) >> mockResponse

        and:
        results.find { it.name == "groovy" }
        results.find { it.name == "grails" }
    }

    void "should handle communication error on retrieving of candidates"() {
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
        1 * mockResponse.getData() >> "2.1.9,2.2.1".bytes
        1 * mockRestClient.get({it.path == "/candidates/$candidate"}) >> mockResponse

        versions.find { it.name == "2.2.1" }
        versions.find { it.name == "2.1.9" }
    }

    void "should handle communication error on retrieving candidate versions"() {
        given:
        def candidate = "groovy"

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
        1 * mockResponse.getData() >> '0.6'.bytes
        1 * mockRestClient.get({it.path == "/candidates/$candidate/default"}) >> mockResponse

        and:
        defaultVersion.name == "0.6"
    }

    void "should handle communication error on retrieving default version"() {
        given:
        def candidate = "lazybones"

        when:
        gvmClient.getDefaultVersionFor(candidate)

        then:
        1 * mockRestClient.get(_) >> { throw new HTTPClientException("fazl") }

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
        1 * mockResponse.getData() >> 'valid'.bytes
        1 * mockRestClient.get({it.path == "/candidates/$candidate/$version"}) >> mockResponse

        and:
        valid
    }

    void "should validate an invalid candidate version"() {
        given:
        def candidate = "groovy"
        def version = "9.9.9"

        when:
        def valid = gvmClient.validCandidateVersion(candidate, version)

        then:
        1 * mockResponse.getData() >> 'invalid'.bytes
        1 * mockRestClient.get({it.path == "/candidates/$candidate/$version"}) >> mockResponse

        and:
        ! valid
    }

    void "should handle communication error on candidate version validation"() {
        when:
        gvmClient.validCandidateVersion("", "")

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("pow!") }

        and:
        thrown(GvmClientException)
    }

    void "should get default app version "() {
        given:
        def version = "1.0.0"

        when:
        def defaultVersion = gvmClient.getAppVersion()

        then:
        1 * mockResponse.getData() >> version.bytes
        1 * mockRestClient.get({it.path == '/app/version'}) >> mockResponse

        and:
        defaultVersion.name == version
    }

    void "should handle communication error on retrieving app version"() {
        when:
        gvmClient.getAppVersion()

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException("D'oh!") }

        and:
        thrown(GvmClientException)
    }

    void "should retrieve download url for a candidate version"() {
        given:
        def candidate = 'groovy'
        def version = '2.2.2'
        def url = "http://pathtozip/archive.zip"
        def response = Mock(HTTPResponse)

        when:
        URL responseUrl = gvmClient.getDownloadURL(candidate, version)

        then:
        1 * response.getHeaders() >> [Location:url]
        1 * mockResponse.getResponse() >> response
        1 * mockRestClient.get({it.path == "/candidates/$candidate/$version/download" && it.followRedirects}) >> mockResponse

        responseUrl instanceof URL
        responseUrl.toString() == url
    }

    void "should handle communication error on candidate version download"() {
        given:
        def candidate = 'lazybones'
        def version = '0.5'

        when:
        gvmClient.getDownloadURL(candidate, version)

        then:
        mockRestClient.get(_) >> { throw new HTTPClientException('blamo') }

        and:
        thrown GvmClientException
    }
}
