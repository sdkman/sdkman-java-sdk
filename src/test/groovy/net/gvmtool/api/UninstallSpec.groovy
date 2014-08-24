package net.gvmtool.api

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class UninstallSpec extends Specification {

    def 'Execute an uninstall without providing a version name'() {
        setup:
        def context = Stub(Context)
        def uninstall = new Uninstall(context, 'candidate')

        when:
        uninstall.version(null)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == 'No valid candidate version was provided.'
    }

    def 'Execute an uninstall'() {
        setup:
        def candidateDir = Files.createTempDirectory('candidate')
        def currentVersionDir = Files.createTempDirectory(candidateDir, 'current')
        def candidateVerDir = Files.createTempDirectory(candidateDir, '1.0')

        def context = Stub(Context) {
            candidateHasCurrentVersion(candidateDir) >> true
            candidateCurrentVersion(candidateDir) >> currentVersionDir
            candidateVersionInstalled(candidateDir, '1.0') >> true
            candidateVersionIsDir(candidateDir, '1.0') >> true
            candidateVersionDir(candidateDir, '1.0') >> candidateVerDir
        }
        def uninstall = new Uninstall(context, 'candidate')
        uninstall.candidates = Stub(Candidates) {
            get(context, 'candidate') >> candidateDir
        }

        when:
        uninstall.version('1.0')

        then:
        Files.notExists(currentVersionDir)
        Files.notExists(candidateVerDir)
    }
}
