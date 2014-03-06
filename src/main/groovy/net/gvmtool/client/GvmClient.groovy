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
