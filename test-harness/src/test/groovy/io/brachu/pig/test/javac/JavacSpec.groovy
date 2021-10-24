package io.brachu.pig.test.javac

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

import javax.tools.ToolProvider

import io.brachu.pig.PackageInfoGenerator
import spock.lang.Specification

class JavacSpec extends Specification {

    def "should compile project with javac"() {
        given:
        def compiler = ToolProvider.systemJavaCompiler
        def fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)
        def workingDir = Paths.get(System.getProperty('user.dir'))
        def projectDir = workingDir.resolve('basic-project')
        def classPath = projectDir.resolve('src')
        def targetDir = projectDir.resolve('target')
        def generatedSourcesDir = targetDir.resolve('generated-sources').resolve('java')

        def javaFiles = Files.walk(classPath)
                .filter { it.fileName.toString().endsWith('.java') }
                .filter { it.toFile().isFile() }
                .collect()

        when:
        def task = compiler.getTask(System.out.newWriter(), fileManager, null, ['-d', targetDir.toAbsolutePath().toString(), '-s', generatedSourcesDir.toAbsolutePath().toString()], null, fileManager.getJavaFileObjectsFromPaths(javaFiles))
        task.processors = [new PackageInfoGenerator()]
        task.call()

        then:
        true
    }

}
