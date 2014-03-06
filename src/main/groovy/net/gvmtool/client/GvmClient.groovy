package net.gvmtool.client

import wslite.http.HTTPClientException
import wslite.rest.RESTClient

final class GvmClient {

    static PRODUCTION_API = "http://api.gvmtool.net"

    RESTClient restClient

    private GvmClient(){}

    static GvmClient instance(String apiUrl = PRODUCTION_API){
        new GvmClient(restClient: new RESTClient(apiUrl))
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

    Boolean validCandidateVersion(String candidate, String version) {
        try {
            def status = restClient.get(path: "/candidates/$candidate/$version").text
            status == "valid" ? true : false

        } catch (HTTPClientException hce) {
            throw new GvmClientException("Error on validating candidate version.", hce)
        }
    }
}
