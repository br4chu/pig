package io.brachu.pig;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Generated;

final class StandardPackageInfoTemplate implements PackageInfoTemplate {

    private static final String GENERATED_SIMPLE_NAME = Generated.class.getSimpleName();
    private static final String GENERATED_CANONICAL_NAME = Generated.class.getCanonicalName();
    private static final String PIG_CANONICAL_NAME = PackageInfoGenerator.class.getCanonicalName();

    private final String templateContent;

    StandardPackageInfoTemplate(String templateContent) {
        this.templateContent = templateContent
                .replace("${GENERATED}", String.format("@%s(\"%s\")", GENERATED_CANONICAL_NAME, PIG_CANONICAL_NAME))
                .replace("${GENERATED_SHORT}", String.format("@%s(\"%s\")", GENERATED_SIMPLE_NAME, PIG_CANONICAL_NAME))
                .replace("${GENERATED_ANNOTATION_CANONICAL_NAME}", GENERATED_CANONICAL_NAME);
    }

    @Override
    public void write(Writer writer, String packageName) throws IOException {
        writer.write(instantiateTemplate(packageName));
    }

    private String instantiateTemplate(String packageName) {
        return templateContent.replace("${PACKAGE_NAME}", packageName);
    }

}
