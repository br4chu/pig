package io.brachu.pig;

import java.io.IOException;
import java.io.Writer;

interface PackageInfoTemplate {

    String PIG_CANONICAL_NAME = PackageInfoGenerator.class.getCanonicalName();

    void write(Writer writer, String packageName) throws IOException;

}
