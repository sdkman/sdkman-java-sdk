package net.gvmtool.api

import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
@Ignore
class GvmSpec extends Specification {

    def 'Use'() {
        expect:
        Gvm.use.grails(version: '2.1.4') == Paths.get(System.properties['user.home'], '.gvm', 'grails', '2.1.4')
    }

    def 'Install'() {
        expect:
        Gvm.install.grails(version: '2.1.4') == Paths.get(System.properties['user.home'], '.gvm', 'grails', '2.1.4')
    }

    def 'Uninstall'() {
        expect:
        Gvm.uninstall.grails(version: '2.1.5')
    }
}
