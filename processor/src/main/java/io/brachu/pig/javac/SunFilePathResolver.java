package io.brachu.pig.javac;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import io.brachu.pig.FilePathResolver;

public final class SunFilePathResolver implements FilePathResolver {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean();

    private static Field sourcefileField;

    private static void initializeReflectionObjects(TypeElement type) {
        try {
            sourcefileField = type.getClass().getDeclaredField("sourcefile");
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("Cannot retrieve 'sourcefile' field from type " + type.getClass().getName(), ex);
        }
    }

    private static JavaFileObject getSourceFile(TypeElement type) {
        try {
            return (JavaFileObject) sourcefileField.get(type);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Cannot retrieve 'sourcefile' value from field " + sourcefileField, ex);
        }
    }

    @Override
    public boolean canResolve(TypeElement type) {
        return type.getClass().getCanonicalName().equals("com.sun.tools.javac.code.Symbol.ClassSymbol");
    }

    @Override
    public Path resolve(TypeElement type) {
        if (INITIALIZED.compareAndSet(false, true)) {
            JpmsUtils.addOpensForPig();
            initializeReflectionObjects(type);
        }
        JavaFileObject sourceFile = getSourceFile(type);
        return Paths.get(sourceFile.toUri());
    }

}
