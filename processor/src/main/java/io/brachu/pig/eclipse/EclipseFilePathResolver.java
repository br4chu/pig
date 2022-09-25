package io.brachu.pig.eclipse;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.lang.model.element.TypeElement;

import io.brachu.pig.FilePathResolver;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;

public final class EclipseFilePathResolver implements FilePathResolver {

    @Override
    public boolean canResolve(TypeElement type) {
        return type.getClass().getCanonicalName().equals("org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl");
    }

    @Override
    public Path resolve(TypeElement type) {
        TypeElementImpl element = (TypeElementImpl) type;
        return Paths.get(element.getFileName());
    }

}
