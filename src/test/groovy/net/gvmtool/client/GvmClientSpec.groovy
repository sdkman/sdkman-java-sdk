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
        def result = gvmClient.getCandidates()

        then:
        result == candidates
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
        def candidate = "lazybones"

        when:
        def versions = gvmClient.getVersionsFor(candidate)

        then:
        versions == "0.2.1,0.3,0.4,0.5,0.6"
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
