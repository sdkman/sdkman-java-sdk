package net.gvmtool.api

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class InstallSpec extends Specification {

    def 'Install an existing version'() {
        setup:
        def context = Stub(Context)

        def candidateDir = Files.createTempDirectory('candidate')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def install = new Install(context, 'candidate')
        install.candidates = Stub(Candidates) {
            get(context, 'candidate') >> candidateDir
        }
        install.candidateVersions = Stub(CandidateVersions) {
            determine(context, candidateDir, '1.0', _ as GvmOptions) >> new CandidateVersion(candidateVersionDir)
        }

        when:
        def versionDir = install.version('1.0')

        then:
        versionDir == candidateVersionDir
    }

    def 'Install a non-existing version'() {
        setup:
        def context = Stub(Context)

        def candidateDir = Files.createTempDirectory('candidate')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def install = new Install(context, 'candidate')
        install.candidates = Stub(Candidates) {
            get(context, 'candidate') >> candidateDir
        }
        install.candidateVersions = Stub(CandidateVersions) {
            determine(context, candidateDir, '1.0', _ as GvmOptions) >> new CandidateVersion(candidateDir.resolve('momo'))
        }
        install.candidateInstaller = Stub(CandidateInstaller) {
            installCandidateVersion(context, 'candidate', '1.0') >> candidateVersionDir
        }

        when:
        def versionDir = install.version('1.0')

        then:
        versionDir == candidateVersionDir
    }

    def 'Install a non-existing version and set as default'() {
        setup:
        def candidateDir = Files.createTempDirectory('candidate')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def context = Stub(Context) {
            candidateCurrentVersion(candidateDir) >> candidateDir.resolve('current')
        }
        def install = new Install(context, 'candidate')
        install.candidates = Stub(Candidates) {
            get(context, 'candidate') >> candidateDir
        }
        install.candidateVersions = Stub(CandidateVersions) {
            determine(context, candidateDir, '1.0', _ as GvmOptions) >> new CandidateVersion(candidateDir.resolve('momo'))
        }
        install.candidateInstaller = Stub(CandidateInstaller) {
            installCandidateVersion(context, 'candidate', '1.0') >> candidateVersionDir
        }

        when:
        def versionDir = install.version('1.0', GvmOption.DEFAULT)

        then:
        versionDir == candidateDir.resolve('current')
    }
}
