package io.brachu.pig.test

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import javax.tools.JavaCompiler
import javax.tools.StandardJavaFileManager

import io.brachu.pig.PackageInfoGenerator

abstract class CompilerUnderTest {

    abstract JavaCompiler implementation()

    void compile(Path targetDir, Path generatedSourcesDir, List<Path> javaFiles) {
        def compiler = implementation()
        def fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)
        def task = createCompilationTask(compiler, fileManager, targetDir, generatedSourcesDir, javaFiles)
        task.processors = [new PackageInfoGenerator()]
        task.call()
    }

    private static createCompilationTask(JavaCompiler compiler,
                                         StandardJavaFileManager fileManager,
                                         Path targetDir,
                                         Path generatedSourcesDir,
                                         List<Path> javaFiles) {
        compiler.getTask(
                System.out.newWriter(),
                fileManager,
                null,
                ['-d', targetDir.toAbsolutePath().toString(), '-s', generatedSourcesDir.toAbsolutePath().toString()],
                null,
                fileManager.getJavaFileObjectsFromPaths(javaFiles)
        )
    }

}
