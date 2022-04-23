package io.brachu.pig;

final class LegacyGeneratedAnnotationNameProvider implements GeneratedAnnotationNameProvider {

    static final LegacyGeneratedAnnotationNameProvider INSTANCE = new LegacyGeneratedAnnotationNameProvider();

    @Override
    public String getGeneratedAnnotationCanonicalName() {
        return "javax.annotation.Generated";
    }

    @Override
    public String getGeneratedAnnotationShortName() {
        return "Generated";
    }

}
