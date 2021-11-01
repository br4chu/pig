package io.brachu.pig.test

import javax.tools.JavaCompiler
import javax.tools.ToolProvider

class JavacCompiler extends CompilerUnderTest {

    @Override
    JavaCompiler implementation() {
        ToolProvider.systemJavaCompiler
    }

}
