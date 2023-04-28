${generatedShort}
package ${packageName};

<#if packageName == 'io.brachu.pig.test.bar'>
    // bar
<#elseif packageName = 'io.brachu.pig.test.baz'>
    // baz
<#else>
    // other
</#if>
import ${generatedAnnotationCanonicalName};
