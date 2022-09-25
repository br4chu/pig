package io.brachu.pig.javac;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import io.brachu.pig.FilePathResolver;

public final class SunFilePathResolver implements FilePathResolver {

    private static final AtomicBoolean ACCESS_PERMITTED = new AtomicBoolean();

    @Override
    public boolean canResolve(TypeElement type) {
        return type.getClass().getCanonicalName().equals("com.sun.tools.javac.code.Symbol.ClassSymbol");
    }

    @Override
    public Path resolve(TypeElement type) {
        if (ACCESS_PERMITTED.compareAndSet(false, true)) {
            JpmsUtils.addOpensForPig();
        }
        ClassSymbol symbol = (ClassSymbol) type;
        JavaFileObject sourceFile = symbol.sourcefile;
        return Paths.get(sourceFile.toUri());
    }

}
