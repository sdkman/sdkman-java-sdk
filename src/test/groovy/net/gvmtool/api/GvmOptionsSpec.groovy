package net.gvmtool.api

import spock.lang.Specification

/**
 * @author Noam Y. Tenne
 */
class GvmOptionsSpec extends Specification {

    def 'Switch options'() {
        given:
        GvmOptions options = new GvmOptions(GvmOption.DEFAULT, GvmOption.INSTALL, GvmOption.OFFLINE)

        expect:
        options.default
        options.install
        options.offline
    }
}
