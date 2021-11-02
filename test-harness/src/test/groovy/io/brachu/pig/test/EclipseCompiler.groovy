package io.brachu.pig.test

import java.nio.file.Path

import io.brachu.pig.PackageInfoGenerator
import org.eclipse.jdt.core.compiler.batch.BatchCompiler

class EclipseCompiler extends CompilerUnderTest {

    @Override
    void compile(Path targetDir, Path generatedSourcesDir, List<Path> javaFiles) {
        def javaVersion = System.getProperty('java.specification.version')
        def cmd = "--release $javaVersion -processor ${PackageInfoGenerator.class.canonicalName} -d $targetDir -s $generatedSourcesDir ${javaFiles.join(' ')}"
        BatchCompiler.compile(cmd, System.out.newPrintWriter(), System.err.newPrintWriter(), null)
    }

}
