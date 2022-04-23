package io.brachu.pig.test

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ProjectUnderTest {

    static final WORKING_DIR = Paths.get(System.getProperty('user.dir'))

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
        ensureDirectoryExists(targetDir)
        ensureDirectoryExists(generatedSourcesDir)
        compiler.compile(targetDir, generatedSourcesDir, findJavaFiles())
    }

    boolean hasGeneratedPackageInfo(String packageName) {
        def relativePath = Paths.get(packageName.replace('.' as char, File.separatorChar)).resolve('package-info.java')
        Files.isRegularFile(generatedSourcesDir.resolve(relativePath))
    }

    void clean() {
        targetDir.toFile().deleteDir()
    }

    private void ensureDirectoryExists(Path directory) {
        if (!directory.toFile().mkdirs()) {
            throw new IllegalStateException("Cannot create directory: " + directory)
        }
    }

    private List<File> findJavaFiles() {
        Files.walk(classPath)
                .filter { it.fileName.toString().endsWith('.java') }
                .filter { it.toFile().isFile() }
                .map { it.toFile() }
                .collect()
    }

}
