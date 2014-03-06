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
        try {
            def csv = restClient.get(path: "/candidates").text
            csv.tokenize(',').each { candidates << new Candidate(name:it) }

        } catch(HTTPClientException hce) {
            throw new GvmClientException("Error on retrieving candidates.", hce)
        }
        candidates
    }

    List<Version> getVersionsFor(String candidate) throws GvmClientException {
        def versions = []
        try {
            def csv = restClient.get(path: "/candidates/$candidate").text
            csv.tokenize(',').each { versions << new Version(name:it) }

        } catch (HTTPClientException hce) {
            throw new GvmClientException("Error on retrieving versions.", hce)
        }
        versions
    }

    Boolean validCandidateVersion(String candidate, String version) throws GvmClientException {
        try {
            def status = restClient.get(path: "/candidates/$candidate/$version").text
            status == "valid" ? true : false

        } catch (HTTPClientException hce) {
            throw new GvmClientException("Error on validating candidate version.", hce)
        }
    }

    Boolean isAlive() throws GvmClientException {
        try {
            def alive = restClient.get(path: "/alive").text
            return alive == "OK" ? true : false

        } catch (HTTPClientException hce) {
            return false
        }
    }

    Version getDefaultVersionFor(String candidate) throws GvmClientException {
        try {
            def defaultVersion = restClient.get(path: "/candidates/$candidate/default").text
            new Version(name: defaultVersion)

        } catch (HTTPClientException hce) {
            throw new GvmClientException(hce)
        }
    }
}
