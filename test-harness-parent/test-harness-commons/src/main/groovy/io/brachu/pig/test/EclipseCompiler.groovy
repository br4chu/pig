package io.brachu.pig.test

import java.nio.file.Path

import io.brachu.pig.PackageInfoGenerator
import org.eclipse.jdt.core.compiler.batch.BatchCompiler

class EclipseCompiler extends CompilerUnderTest {

    @Override
    void compile(Path targetDir, Path generatedSourcesDir, List<File> javaFiles) {
        def javaVersion = System.getProperty('java.specification.version')
        def cmd = "-source $javaVersion -processor ${PackageInfoGenerator.class.canonicalName} -d $targetDir -s $generatedSourcesDir ${javaFiles.join(' ')}"
        BatchCompiler.compile(cmd, System.out.newPrintWriter(), System.err.newPrintWriter(), null)
    }

}
