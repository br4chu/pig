package io.brachu.pig;

final class StandardGeneratedAnnotationNameProvider implements GeneratedAnnotationNameProvider {

    static final StandardGeneratedAnnotationNameProvider INSTANCE = new StandardGeneratedAnnotationNameProvider();

    @Override
    public String getGeneratedAnnotationCanonicalName() {
        return "javax.annotation.processing.Generated";
    }

    @Override
    public String getGeneratedAnnotationShortName() {
        return "Generated";
    }

}
