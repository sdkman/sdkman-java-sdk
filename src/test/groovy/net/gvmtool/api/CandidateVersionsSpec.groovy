package net.gvmtool.api

import net.gvmtool.client.GvmClient
import net.gvmtool.client.Version
import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class CandidateVersionsSpec extends Specification {

    def 'Determine a provided version that\'s already installed while in offline mode'() {
        setup:
        def candidateDir = Files.createTempDirectory('temp')
        def context = Stub(Context) {
            candidateVersionInstalled(candidateDir, '1.0') >> { true }
            candidateVersionDir(candidateDir, '1.0') >> { candidateDir.resolve('1.0') }
        }

        CandidateVersions candidateVersions = new CandidateVersions()

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, '1.0',
                new GvmOptions(GvmOption.OFFLINE))

        then:
        determinedVersion.name() == '1.0'
        determinedVersion.dir() == candidateDir.resolve('1.0')
    }

    def 'Determine a current version while in offline mode'() {
        setup:
        def candidateDir = Files.createTempDirectory('temp')
        def installedVersionDir = Files.createDirectories(candidateDir.resolve('1.0'))

        def context = Stub(Context) {
            candidateHasCurrentVersion(candidateDir) >> { true }
            candidateResolveCurrentDir(candidateDir) >> { installedVersionDir }
            candidateVersionDir(candidateDir, '1.0') >> { candidateDir.resolve('1.0') }
        }

        CandidateVersions candidateVersions = new CandidateVersions()

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, null, new GvmOptions(GvmOption.OFFLINE))

        then:
        determinedVersion.name() == '1.0'
        determinedVersion.dir() == installedVersionDir
    }

    def 'Determine a provided version that\'s not installed while in offline mode'() {
        setup:
        def candidateDir = Files.createTempDirectory('temp')
        def context = Stub(Context)

        CandidateVersions candidateVersions = new CandidateVersions()

        when:
        candidateVersions.determine(context, candidateDir, '1.0', new GvmOptions(GvmOption.OFFLINE))

        then:
        def e = thrown(Exception)
        e.message == 'Not available offline'
    }

    def 'Determine the latest version while in offline mode'() {
        setup:
        def candidateDir = Files.createTempDirectory('temp')
        def context = Stub(Context)

        CandidateVersions candidateVersions = new CandidateVersions()

        when:
        candidateVersions.determine(context, candidateDir, null, new GvmOptions(GvmOption.OFFLINE))

        then:
        def e = thrown(Exception)
        e.message == 'Not available offline'
    }

    def 'Determine the latest version'() {
        setup:
        def candidateDir = Files.createTempDirectory('temp')

        def gvmClient = Stub(GvmClient) {
            getDefaultVersionFor(candidateDir.fileName.toString()) >> { new Version('1.0') }
        }

        def context = Stub(Context) {
            getClient() >> { gvmClient }
            candidateVersionDir(candidateDir, '1.0') >> { candidateDir.resolve('1.0') }
        }

        CandidateVersions candidateVersions = new CandidateVersions()

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, null, new GvmOptions())

        then:
        determinedVersion.name() == '1.0'
        determinedVersion.dir() == candidateDir.resolve('1.0')
    }

    def 'Validate a provided candidate version name'() {
        setup:
        def candidateDir = Files.createTempDirectory('temp')

        def gvmClient = Stub(GvmClient) {
            validCandidateVersion(candidateDir.fileName.toString(), '1.0') >> { true }
        }

        def context = Stub(Context) {
            getClient() >> { gvmClient }
            candidateVersionDir(candidateDir, '1.0') >> { candidateDir.resolve('1.0') }
        }

        CandidateVersions candidateVersions = new CandidateVersions()

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, '1.0', new GvmOptions())

        then:
        determinedVersion.name() == '1.0'
        determinedVersion.dir() == candidateDir.resolve('1.0')
    }

    def 'Validate a symlinked provided candidate version name'() {
        setup:
        def candidateDir = Files.createTempDirectory('temp')

        def gvmClient = Stub(GvmClient) {
            validCandidateVersion(candidateDir.fileName.toString(), '1.0') >> { false }
        }

        def context = Stub(Context) {
            getClient() >> { gvmClient }
            candidateVersionIsSymlink(candidateDir, '1.0') >> { true }
            candidateVersionDir(candidateDir, '1.0') >> { candidateDir.resolve('1.0') }
        }

        CandidateVersions candidateVersions = new CandidateVersions()

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, '1.0', new GvmOptions())

        then:
        determinedVersion.name() == '1.0'
        determinedVersion.dir() == candidateDir.resolve('1.0')
    }

    def 'Validate an installed provided candidate version name'() {
        setup:
        def candidateDir = Files.createTempDirectory('temp')

        def gvmClient = Stub(GvmClient) {
            validCandidateVersion(candidateDir.fileName.toString(), '1.0') >> { false }
        }

        def context = Stub(Context) {
            getClient() >> { gvmClient }
            candidateVersionIsSymlink(candidateDir, '1.0') >> { false }
            candidateVersionIsDir(candidateDir, '1.0') >> { true }
            candidateVersionDir(candidateDir, '1.0') >> { candidateDir.resolve('1.0') }
        }

        CandidateVersions candidateVersions = new CandidateVersions()

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, '1.0', new GvmOptions())

        then:
        determinedVersion.name() == '1.0'
        determinedVersion.dir() == candidateDir.resolve('1.0')
    }

    def 'Validate an invalid provided candidate version name'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')

        def gvmClient = Stub(GvmClient) {
            validCandidateVersion(candidateDir.fileName.toString(), '1.0') >> { false }
        }

        def context = Stub(Context) {
            getClient() >> { gvmClient }
            candidateVersionIsSymlink(candidateDir, '1.0') >> { false }
            candidateVersionIsDir(candidateDir, '1.0') >> { false }
        }

        when:
        candidateVersions.determine(context, candidateDir, '1.0', new GvmOptions())

        then:
        def e = thrown(Exception)
        e.message == "1.0 is not a valid ${candidateDir.fileName.toString()} version."
    }
}
