###GVM Java SDK

This library is for the purpose of interacting with the GVM REST API from within your Java project. It exposes various convenience methods for retrieving encapsulated data.

Using it is simple:

    def gvmClient = new GvmClient("http://dev.gvmtool.net")
    
    def candidates = gvmClient.getCandidates()
	candidates.each { println it.name }

    def versions = gvmClient.getVersionsFor("groovy")
    versions.each { println it.name }

Lots more features coming soon!!!
