package net.gvmtool.api

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class UseSpec extends Specification {

    def 'Use a pre-installed version'() {
        setup:
        def context = Stub(Context)
        def candidateDir = Files.createTempDirectory('candidate')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def use = new Use(context, 'candidate')
        use.candidates = Stub(Candidates) {
            get(context, 'candidate') >> candidateDir
        }
        use.candidateVersions = Stub(CandidateVersions) {
            determine(context, candidateDir, '1.0', _ as GvmOptions) >> new CandidateVersion(candidateVersionDir)
        }

        when:
        def versionDir = use.version('1.0')

        then:
        versionDir == candidateVersionDir
    }

    def 'Use a non-installed version with the install GvmOptions switched off'() {
        setup:
        def context = Stub(Context)
        def candidateDir = Files.createTempDirectory('candidate')

        def use = new Use(context, 'candidate')
        use.candidates = Stub(Candidates) {
            get(context, 'candidate') >> candidateDir
        }
        use.candidateVersions = Stub(CandidateVersions) {
            determine(context, candidateDir, '1.0', _ as GvmOptions) >> new CandidateVersion(candidateDir.resolve('1.0'))
        }
        when:
        use.version('1.0')

        then:
        def exception = thrown(Exception)
        exception.message == "candidate 1.0 is not installed"
    }

    def 'Use a non-installed version with the install GvmOptions switched on'() {
        setup:
        def context = Stub(Context)

        def candidateDir = Files.createTempDirectory('candidate')

        def use = new Use(context, 'candidate')
        use.candidates = Stub(Candidates) {
            get(context, 'candidate') >> candidateDir
        }
        use.candidateVersions = Stub(CandidateVersions) {
            determine(context, candidateDir, '1.0', _ as GvmOptions) >> new CandidateVersion(candidateDir.resolve('1.0'))
        }
        use.candidateInstaller = Stub(CandidateInstaller){
            installCandidateVersion(context, 'candidate', '1.0') >> Files.createDirectory(candidateDir.resolve('1.0'))
        }

        when:
        def installedVersion = use.version('1.0', GvmOption.INSTALL)

        then:
        installedVersion == candidateDir.resolve('1.0')
    }
}
