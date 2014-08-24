package net.gvmtool.api

import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

import static java.nio.file.Files.isSameFile

/**
 * @author Noam Y. Tenne.
 */
class ContextSpec extends Specification {

    @Ignore
    def 'Construct a new context'() {
        setup:
        def context = Context.get()

        expect:
        isSameFile(context.gvmHomeDir, Paths.get(System.properties['user.home'], '.gvm'))
        context.service
    }

    def 'Resolve GVM directories'() {
        setup:
        def gvmHomeDir = Files.createTempDirectory('gvm')
        Context context = new Context()
        context.gvmHomeDir = gvmHomeDir

        expect: //Check methods that don't require FS state
        isSameFile(context.archives(), gvmHomeDir.resolve('archives'))
        isSameFile(context.tmp(), gvmHomeDir.resolve('tmp'))
        isSameFile(context.candidateArchive('candidateName', 'candidateVersion'),
                gvmHomeDir.resolve('archives').resolve('candidateName-candidateVersion.zip'))
        isSameFile(context.candidateDir('candidateName'), gvmHomeDir.resolve('candidateName'))
        isSameFile(context.candidateVersionDir(context.candidateDir('candidateName'), 'candidateVersion'),
                gvmHomeDir.resolve('candidateName').resolve('candidateVersion'))
        isSameFile(context.candidateCurrentVersion(context.candidateDir('candidateName')),
                gvmHomeDir.resolve('candidateName').resolve('current'))
    }

    def 'Resolve GVM current candidate version directory'() {
        setup:
        def gvmHomeDir = Files.createTempDirectory('gvm')
        Context context = new Context()
        context.gvmHomeDir = gvmHomeDir
        def candidateWithCurrentSymlink = context.candidateDir('candidateWithCurrentSymlink')
        def concreteVersion = context.candidateVersionDir(candidateWithCurrentSymlink, '1.0')
        Files.createDirectories(concreteVersion)

        Files.createSymbolicLink(candidateWithCurrentSymlink.resolve('current'), concreteVersion)

        expect: //Check methods that deal with the current version symlink
        context.candidateHasCurrentVersion(candidateWithCurrentSymlink)
        isSameFile(context.candidateResolveCurrentDir(candidateWithCurrentSymlink), concreteVersion)
        context.candidateVersionIsSymlink(candidateWithCurrentSymlink, 'current')

        and:
        context.candidateVersionInstalled(candidateWithCurrentSymlink, '1.0')
        context.candidateVersionIsDir(candidateWithCurrentSymlink, '1.0')
    }
}
