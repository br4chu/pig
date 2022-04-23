package io.brachu.pig;

import java.io.IOException;
import java.io.Writer;

final class StandardPackageInfoTemplate implements PackageInfoTemplate {

    private static final String PIG_CANONICAL_NAME = PackageInfoGenerator.class.getCanonicalName();

    private final String templateContent;

    StandardPackageInfoTemplate(String templateContent, GeneratedAnnotationNameProvider generatedAnnotationNameProvider) {
        String generatedAnnotationCanonicalName = generatedAnnotationNameProvider.getGeneratedAnnotationCanonicalName();
        String generatedAnnotationShortName = generatedAnnotationNameProvider.getGeneratedAnnotationShortName();
        String generatedAnnotationUsage = "@" + generatedAnnotationCanonicalName + "(\"" + PIG_CANONICAL_NAME + "\")";
        String generatedAnnotationShortUsage = "@" + generatedAnnotationShortName + "(\"" + PIG_CANONICAL_NAME + "\")";
        this.templateContent = templateContent
                .replace("${GENERATED}", generatedAnnotationUsage)
                .replace("${GENERATED_SHORT}", generatedAnnotationShortUsage)
                .replace("${GENERATED_ANNOTATION_CANONICAL_NAME}", generatedAnnotationCanonicalName);
    }

    @Override
    public void write(Writer writer, String packageName) throws IOException {
        writer.write(instantiateTemplate(packageName));
    }

    private String instantiateTemplate(String packageName) {
        return templateContent.replace("${PACKAGE_NAME}", packageName);
    }

}
