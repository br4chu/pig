package io.brachu.pig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import io.brachu.pig.eclipse.EclipseFilePathResolver;
import io.brachu.pig.javac.SunFilePathResolver;

final class PackageInfoTemplateProvider {

    private static final String UNSUPPORTED_OPERATION_MESSAGE = "Package Info Generator does not support this compiler tool. "
            + "Processed element was of type: %s. Feel free to raise a feature request over at github.";

    private final MissingTemplate missingTemplate;
    private final Map<Path, PackageInfoTemplate> templateCache = new HashMap<>();

    private final SunFilePathResolver sunFilePathResolver = new SunFilePathResolver();
    private final EclipseFilePathResolver eclipseFilePathResolver = new EclipseFilePathResolver();

    private final GeneratedAnnotationNameProvider generatedAnnotationNameProvider = GeneratedAnnotationNameProvider.create();

    PackageInfoTemplateProvider(ProcessingEnvironment processingEnv) {
        missingTemplate = new MissingTemplate(processingEnv);
    }

    PackageInfoTemplate provideFor(TypeElement type) throws IOException {
        if (sunFilePathResolver.canResolve(type)) {
            return resolveTemplate(sunFilePathResolver.resolve(type));
        } else if (eclipseFilePathResolver.canResolve(type)) {
            return resolveTemplate(eclipseFilePathResolver.resolve(type));
        } else {
            throw new UnsupportedOperationException(String.format(UNSUPPORTED_OPERATION_MESSAGE, type.getClass().getCanonicalName()));
        }
    }

    private PackageInfoTemplate resolveTemplate(Path path) throws IOException {
        if (path == null) {
            return missingTemplate;
        } else {
            PackageInfoTemplate cachedTemplate = templateCache.get(path);
            if (cachedTemplate != null) {
                return cachedTemplate;
            } else {
                PackageInfoTemplate template = findTemplateIn(path);
                if (template == null) {
                    template = resolveTemplate(path.getParent());
                }
                templateCache.put(path, template);
                return template;
            }
        }
    }

    private PackageInfoTemplate findTemplateIn(Path path) throws IOException {
        Path file = path.resolve("pig.template");
        if (Files.isRegularFile(file)) {
            String content = readContent(file);
            return new StandardPackageInfoTemplate(content, generatedAnnotationNameProvider);
        } else {
            return null;
        }
    }

    private String readContent(Path file) throws IOException {
        return Files.readAllLines(file)
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

}
