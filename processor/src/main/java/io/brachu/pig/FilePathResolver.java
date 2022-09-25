package io.brachu.pig;

import java.nio.file.Path;

import javax.lang.model.element.TypeElement;

public interface FilePathResolver {

    boolean canResolve(TypeElement type);

    Path resolve(TypeElement type);

}
