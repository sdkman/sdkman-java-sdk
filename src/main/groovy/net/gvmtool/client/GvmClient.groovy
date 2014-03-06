package net.gvmtool.client

import wslite.http.HTTPClientException
import wslite.rest.RESTClient

class GvmClient {

    private RESTClient restClient

    GvmClient(String apiUrl) {
        this.restClient = new RESTClient(apiUrl)
    }

    GvmClient(RESTClient restClient) {
        this.restClient = restClient
    }

    String getCandidates() {
        try {
            restClient.get(path: "/candidates").text

        } catch(HTTPClientException hce) {
            throw new GvmClientException("Error on retrieving candidates.", hce)
        }
    }

    String getVersionsFor(String candidate) {
        try {
            restClient.get(path: "/candidates/$candidate").text

        } catch (HTTPClientException hce) {
            throw new GvmClientException("Error on retrieving versions.", hce)
        }
    }
}
