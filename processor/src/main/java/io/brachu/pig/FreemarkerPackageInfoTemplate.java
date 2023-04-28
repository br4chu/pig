package io.brachu.pig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

final class FreemarkerPackageInfoTemplate implements PackageInfoTemplate {

    private static final Configuration CFG = new Configuration(Configuration.VERSION_2_3_32);
    private static final Map<String, Object> COMMON_MODEL = new HashMap<>();

    static {
        CFG.setDefaultEncoding("UTF-8");
    }

    static {
        GeneratedAnnotationNameProvider provider = GeneratedAnnotationNameProvider.create();
        String generatedAnnotationCanonicalName = provider.getGeneratedAnnotationCanonicalName();
        String generatedAnnotationShortName = provider.getGeneratedAnnotationShortName();
        String generatedAnnotationUsage = "@" + generatedAnnotationCanonicalName + "(\"" + PIG_CANONICAL_NAME + "\")";
        String generatedAnnotationShortUsage = "@" + generatedAnnotationShortName + "(\"" + PIG_CANONICAL_NAME + "\")";
        COMMON_MODEL.put("generated", generatedAnnotationUsage);
        COMMON_MODEL.put("generatedShort", generatedAnnotationShortUsage);
        COMMON_MODEL.put("generatedAnnotationCanonicalName", generatedAnnotationCanonicalName);
    }

    private final Template template;

    FreemarkerPackageInfoTemplate(Path templateFilePath) throws IOException {
        try (Reader reader = new InputStreamReader(Files.newInputStream(templateFilePath), StandardCharsets.UTF_8)) {
            template = new Template(null, reader, CFG);
        }
    }

    @Override
    public void write(Writer writer, String packageName) throws IOException {
        Map<String, Object> model = new HashMap<>(COMMON_MODEL);
        model.put("packageName", packageName);
        try {
            template.process(model, writer);
        } catch (TemplateException ex) {
            throw new IllegalStateException("Failed to process Freemarker template", ex);
        }
    }

}
