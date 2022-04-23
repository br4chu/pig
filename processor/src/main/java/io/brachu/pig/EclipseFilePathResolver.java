package io.brachu.pig;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.lang.model.element.TypeElement;

import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;

final class EclipseFilePathResolver {

    boolean canResolve(TypeElement type) {
        return type.getClass().getCanonicalName().equals("org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl");
    }

    Path resolve(TypeElement type) {
        TypeElementImpl element = (TypeElementImpl) type;
        return Paths.get(element.getFileName());
    }

}
