package io.brachu.pig;

interface GeneratedAnnotationNameProvider {

    static GeneratedAnnotationNameProvider create() {
        String specVersion = System.getProperty("java.specification.version");
        if (Float.parseFloat(specVersion) >= 9) {
            return StandardGeneratedAnnotationNameProvider.INSTANCE;
        } else {
            return LegacyGeneratedAnnotationNameProvider.INSTANCE;
        }
    }

    String getGeneratedAnnotationCanonicalName();

    String getGeneratedAnnotationShortName();

}
