package io.brachu.pig;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

final class SimplePackageInfoTemplate implements PackageInfoTemplate {

    private static final UnaryOperator<String> COMMON_TEMPLATE_INSTANTIATOR = template -> {
        GeneratedAnnotationNameProvider provider = GeneratedAnnotationNameProvider.create();
        String generatedAnnotationCanonicalName = provider.getGeneratedAnnotationCanonicalName();
        String generatedAnnotationShortName = provider.getGeneratedAnnotationShortName();
        String generatedAnnotationUsage = "@" + generatedAnnotationCanonicalName + "(\"" + PIG_CANONICAL_NAME + "\")";
        String generatedAnnotationShortUsage = "@" + generatedAnnotationShortName + "(\"" + PIG_CANONICAL_NAME + "\")";
        return template
                .replace("${GENERATED}", generatedAnnotationUsage)
                .replace("${GENERATED_SHORT}", generatedAnnotationShortUsage)
                .replace("${GENERATED_ANNOTATION_CANONICAL_NAME}", generatedAnnotationCanonicalName);
    };

    private final String templateContent;

    SimplePackageInfoTemplate(Path templateFilePath) throws IOException {
        templateContent = COMMON_TEMPLATE_INSTANTIATOR.apply(readContent(templateFilePath));
    }

    @Override
    public void write(Writer writer, String packageName) throws IOException {
        writer.write(instantiateTemplate(packageName));
    }

    private String readContent(Path file) throws IOException {
        return Files.readAllLines(file)
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String instantiateTemplate(String packageName) {
        return templateContent.replace("${PACKAGE_NAME}", packageName);
    }

}
