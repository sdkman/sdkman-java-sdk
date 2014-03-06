package net.gvmtool.client

import spock.lang.Specification
import wslite.http.HTTPClientException
import wslite.rest.RESTClient

class GvmClientSpec extends Specification {

    GvmClient gvmClient

    void setup(){
        def api = "http://dev.gvmtool.net"
        gvmClient = GvmClient.instance(api)
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

}
