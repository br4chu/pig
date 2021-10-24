package io.brachu.pig;

import java.nio.file.Path;

import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.sun.tools.javac.code.Symbol.ClassSymbol;

final class SunFilePathResolver {

    boolean canResolve(TypeElement type) {
        return type.getClass().getCanonicalName().equals("com.sun.tools.javac.code.Symbol.ClassSymbol");
    }

    Path resolve(TypeElement type) {
        ClassSymbol symbol = (ClassSymbol) type;
        JavaFileObject sourceFile = symbol.sourcefile;
        return Path.of(sourceFile.toUri());
    }

}
