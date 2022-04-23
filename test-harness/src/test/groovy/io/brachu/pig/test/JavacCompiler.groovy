package io.brachu.pig.test

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import javax.tools.JavaCompiler
import javax.tools.StandardJavaFileManager
import javax.tools.ToolProvider

import io.brachu.pig.PackageInfoGenerator

class JavacCompiler extends CompilerUnderTest {

    @Override
    void compile(Path targetDir, Path generatedSourcesDir, List<File> javaFiles) {
        def compiler = ToolProvider.systemJavaCompiler
        assert compiler, "Compiler instance unavailable. Are we running in JRE?"

        def fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)
        def task = createCompilationTask(compiler, fileManager, targetDir, generatedSourcesDir, javaFiles)
        task.processors = [new PackageInfoGenerator()]
        task.call()
    }

    private static createCompilationTask(JavaCompiler compiler,
                                         StandardJavaFileManager fileManager,
                                         Path targetDir,
                                         Path generatedSourcesDir,
                                         List<File> javaFiles) {
        compiler.getTask(
                System.out.newWriter(),
                fileManager,
                null,
                ['-d', targetDir.toAbsolutePath().toString(), '-s', generatedSourcesDir.toAbsolutePath().toString()],
                null,
                fileManager.getJavaFileObjectsFromFiles(javaFiles)
        )
    }

}
