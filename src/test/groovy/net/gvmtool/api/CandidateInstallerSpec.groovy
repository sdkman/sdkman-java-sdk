package net.gvmtool.api

import net.gvmtool.client.GvmClient
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
class CandidateInstallerSpec extends Specification {

    def 'Install a not-downloaded distribution archive'() {
        setup:
        def contextTemp = Files.createTempDirectory('contextTemp')
        def archivesDir = Files.createTempDirectory('archives')
        def candidateDirectory = Files.createTempDirectory('candidateDir')

        def installer = new CandidateInstaller()

        def gvmClient = Stub(GvmClient) {
            getDownloadURL('grails', '2.1.4') >> { CandidateInstallerSpec.getResource('/grails-2.1.4.zip')}
        }

        def context = Stub(Context) {
            getClient() >> { gvmClient }
            candidateArchive('grails', '2.1.4') >> { Paths.get('bla') }
            archives() >> { archivesDir }
            tmp() >> { contextTemp }
            candidateDir('grails') >> { String candidate -> candidateDirectory }
            candidateVersionDir(candidateDirectory, '2.1.4') >> { Path candidate, String version ->
                candidateDirectory.resolve(version)
            }
        }

        when:
        Path installedDistro = installer.installCandidateVersion(context, 'grails', '2.1.4')

        then:
        contextTemp.toFile().list().size() == 0
        Files.exists(installedDistro.resolve('dummy.file'))
    }

    def 'Install a downloaded distribution archive'() {
        setup:
        def contextTemp = Files.createTempDirectory('contextTemp')
        def candidateDirectory = Files.createTempDirectory('candidateDir')

        def installer = new CandidateInstaller()
        def context = Stub(Context) {
            candidateArchive('grails', '2.1.4') >> {
                Paths.get(CandidateInstallerSpec.getResource('/grails-2.1.4.zip').toURI())
            }
            tmp() >> { contextTemp }
            candidateDir('grails') >> { String candidate -> candidateDirectory }
            candidateVersionDir(candidateDirectory, '2.1.4') >> { Path candidate, String version ->
                candidateDirectory.resolve(version)
            }
        }

        when:
        Path installedDistro = installer.installCandidateVersion(context, 'grails', '2.1.4')

        then:
        contextTemp.toFile().list().size() == 0

        def extractedPath = installedDistro.resolve('dummy.file')
        Files.exists(extractedPath)
        Files.isWritable(extractedPath)
        Files.isReadable(extractedPath)
        Files.isExecutable(extractedPath)
    }
}
