package net.gvmtool.client

import wslite.http.HTTPClientException
import wslite.rest.RESTClient

class GvmClient {

    RESTClient restClient

    GvmClient(String apiUrl) {
        this.restClient = new RESTClient(apiUrl)
    }

    List<String> getCandidates() {
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
