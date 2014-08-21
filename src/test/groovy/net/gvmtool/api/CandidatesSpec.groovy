package net.gvmtool.api

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class CandidatesSpec extends Specification {

    def 'Get the directory of a valid candidate'() {
        setup:
        def candidates = new Candidates()

        def candidateTempDir = Files.createTempDirectory('groovy')
        def context = Stub(Context) {
            candidateDir('groovy') >> candidateTempDir
        }

        when:
        def candidateDir = candidates.get(context, 'groovy')

        then:
        candidateDir == candidateTempDir
    }
}
