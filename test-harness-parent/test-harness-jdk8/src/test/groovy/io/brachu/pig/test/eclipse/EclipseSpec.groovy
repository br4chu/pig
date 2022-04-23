package io.brachu.pig.test.eclipse

import io.brachu.pig.test.EclipseCompiler
import io.brachu.pig.test.ProjectUnderTest
import spock.lang.Specification

class EclipseSpec extends Specification {

    final compiler = new EclipseCompiler()

    def "should compile basic project with javac"() {
        given:
        def project = new ProjectUnderTest('basic')

        when:
        project.compileWith(compiler)

        then:
        assert project.hasGeneratedPackageInfo('io.brachu.pig.test.foo')
        assert project.hasGeneratedPackageInfo('io.brachu.pig.test.bar')
        assert project.hasGeneratedPackageInfo('io.brachu.pig.test.baz')

        cleanup:
        project.clean()
    }

    def "project compiled under jdk8 should use javax.annotation.Generated annotation"() {
        given:
        def project = new ProjectUnderTest('basic')

        when:
        project.compileWith(compiler)

        then:
        assert project.getPackageInfoContent('io.brachu.pig.test.foo').contains('import javax.annotation.Generated')

        cleanup:
        project.clean()
    }

}
