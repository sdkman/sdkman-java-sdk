package net.gvmtool.client

import wslite.http.HTTPClientException
import wslite.rest.RESTClient

class GvmClient {

    RESTClient restClient
    String gvmHome

    GvmClient(String apiUrl) {
        this(apiUrl, System.getProperty('user.home') + '/.gvm')
    }

    GvmClient(String apiUrl, String gvmHome) {
        Objects.requireNonNull(gvmHome, "gvmHome must not be null")
        assert new File(gvmHome).isDirectory(): "Directory '$gvmHome' does not exist"

        this.restClient = new RESTClient(apiUrl)
        this.gvmHome = gvmHome
    }

    List<String> getRemoteCandidates() {
        try {
            def csv = restClient.get(path: "/candidates").text
            return csv.tokenize(',')

        } catch(HTTPClientException hce) {
            throw new GvmClientException("Error on retrieving candidates.", hce)
        }
    }

    List<String> getRemoteVersionsFor(String candidate) {
        try {
              def csv = restClient.get(path: "/candidates/$candidate").text
          if (csv == 'invalid') {
              return []
          }
          else {
              return csv.tokenize(',')
          }

        } catch (HTTPClientException hce) {
            throw new GvmClientException("Error on retrieving versions.", hce)
        }
    }


}
