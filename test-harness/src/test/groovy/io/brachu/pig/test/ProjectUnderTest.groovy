package io.brachu.pig.test

import java.nio.file.Files
import java.nio.file.Path

class ProjectUnderTest {

    static final WORKING_DIR = Path.of(System.getProperty('user.dir'))

    final Path classPath
    final Path targetDir
    final Path generatedSourcesDir

    ProjectUnderTest(String name) {
        def projectDir = WORKING_DIR.resolve('projects').resolve(name)
        assert Files.isDirectory(projectDir), "Path $projectDir does not point to a directory."

        classPath = projectDir.resolve('src')
        targetDir = projectDir.resolve('target')
        generatedSourcesDir = targetDir.resolve('generated-sources').resolve('java')
    }

    void compileWith(CompilerUnderTest compiler) {
        compiler.compile(targetDir, generatedSourcesDir, findJavaFiles())
    }

    boolean hasGeneratedPackageInfo(String packageName) {
        def relativePath = Path.of(packageName.replace('.' as char, File.separatorChar)).resolve('package-info.java')
        Files.isRegularFile(generatedSourcesDir.resolve(relativePath))
    }

    void clean() {
        targetDir.toFile().deleteDir()
    }

    private List<Path> findJavaFiles() {
        Files.walk(classPath)
                .filter { it.fileName.toString().endsWith('.java') }
                .filter { it.toFile().isFile() }
                .collect()
    }

}
