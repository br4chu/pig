package io.brachu.pig.test.javac

import io.brachu.pig.test.JavacCompiler
import io.brachu.pig.test.ProjectUnderTest
import spock.lang.Specification

class JavacSpec extends Specification {

    final compiler = new JavacCompiler()

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

}
