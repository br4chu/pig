package io.brachu.pig.test

import java.nio.file.Path

abstract class CompilerUnderTest {

    abstract void compile(Path targetDir, Path generatedSourcesDir, List<File> javaFiles)

}
