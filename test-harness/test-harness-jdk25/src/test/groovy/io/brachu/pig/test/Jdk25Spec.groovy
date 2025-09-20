package io.brachu.pig.test

import spock.lang.Shared
import spock.lang.Specification

class Jdk25Spec extends Specification {

    @Shared
    def compilers = [new JavacCompiler(), new EclipseCompiler()]

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

        where:
        compiler << compilers
    }

    def "project compiled under jdk25 should use javax.annotation.processing.Generated annotation"() {
        given:
        def project = new ProjectUnderTest('basic')

        when:
        project.compileWith(compiler)

        then:
        assert project.getPackageInfoContent('io.brachu.pig.test.foo').contains('import javax.annotation.processing.Generated')

        cleanup:
        project.clean()

        where:
        compiler << compilers
    }

    def "should generate package-info.java files from freemarker template"() {
        given:
        def project = new ProjectUnderTest('freemarker')

        when:
        project.compileWith(compiler)

        then:
        project.getPackageInfoContent('io.brachu.pig.test.bar').contains('// bar')
        project.getPackageInfoContent('io.brachu.pig.test.baz').contains('// baz')
        project.getPackageInfoContent('io.brachu.pig.test.foo').contains('// other')

        cleanup:
        project.clean()

        where:
        compiler << compilers
    }

}
