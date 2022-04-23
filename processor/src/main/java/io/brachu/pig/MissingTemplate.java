package io.brachu.pig;

import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

final class MissingTemplate implements PackageInfoTemplate {

    private static final String MESSAGE = "[PIG] Cannot generate package-info.java file for package '%s'. "
            + "pig.template must be present in the package directory or in one of its parent directories.";

    private final ProcessingEnvironment processingEnv;

    MissingTemplate(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    @Override
    public void write(Writer writer, String packageName) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(MESSAGE, packageName));
    }

}
