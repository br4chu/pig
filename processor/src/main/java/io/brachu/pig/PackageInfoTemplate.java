package io.brachu.pig;

import java.io.IOException;
import java.io.Writer;

interface PackageInfoTemplate {

    void write(Writer writer, String packageName) throws IOException;

}
