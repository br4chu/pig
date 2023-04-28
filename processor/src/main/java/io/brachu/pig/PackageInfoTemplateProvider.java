package io.brachu.pig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
                PackageInfoTemplate template = findTemplate(path);
                if (template == null) {
                    template = resolveTemplate(path.getParent());
                }
                templateCache.put(path, template);
                return template;
            }
        }
    }

    private PackageInfoTemplate findTemplate(Path path) throws IOException {
        Optional<PackageInfoTemplate> template = findSimpleTemplate(path);
        if (!template.isPresent()) {
            template = findFreemarkerTemplate(path);
        }
        return template.orElse(null);
    }

    private Optional<PackageInfoTemplate> findSimpleTemplate(Path path) throws IOException {
        Path file = path.resolve("pig.template");
        if (Files.isRegularFile(file)) {
            return Optional.of(new SimplePackageInfoTemplate(file));
        } else {
            return Optional.empty();
        }
    }

    private Optional<PackageInfoTemplate> findFreemarkerTemplate(Path path) throws IOException {
        Path file = path.resolve("pig.ftl");
        if (Files.isRegularFile(file)) {
            return Optional.of(new FreemarkerPackageInfoTemplate(file));
        } else {
            return Optional.empty();
        }
    }

}
