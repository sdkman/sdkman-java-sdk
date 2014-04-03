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

class GvmClientIntegrationSpec extends Specification {

    static DEV_API_URL = "http://dev.gvmtool.net"

    GvmClient gvmClient

    void setup(){
        gvmClient = GvmClient.instance(DEV_API_URL)
    }

    void "should report alive when api is healthy"() {
        when:
        def alive = gvmClient.isAlive()

        then:
        alive
    }

    void "should retrieve a list of candidates"() {
        when:
        List<Candidate> results = gvmClient.getCandidates()

        then:
        results.find { it.name == "groovy" }
        results.find { it.name == "grails" }
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

    void "should get default version for a candidate"() {
        given:
        def candidate = "lazybones"

        when:
        def defaultVersion = gvmClient.getDefaultVersionFor(candidate)

        then:
        defaultVersion.name == "0.6"
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

    void "should get default app version "() {
        when:
        def defaultVersion = gvmClient.getAppVersion()

        then:
        defaultVersion.name ==~ /^1\.0\.0\+build-\d{3}$/
    }

    void "should retrieve download url for a candidate version"() {
        given:
        def candidate = 'grails'
        def version = '0.0.0'

        when:
        URL url = gvmClient.getDownloadURL(candidate, version)

        then:
        url instanceof URL
        url.toString() == 'https://dl.dropboxusercontent.com/u/5383535/grails-0.0.0.zip'
    }
}
