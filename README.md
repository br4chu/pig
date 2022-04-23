# PIG

PIG, also known as Package Info Generator, is a Java annotation processor that generates package-info.java file for every Java package in a project, based on a
predefined template file.

# Usage

## Step 1

Add "pig.template" file to your project's root directory. You can set the content to the example below:

```
${GENERATED}
@javax.annotation.ParametersAreNonnullByDefault
package ${PACKAGE_NAME};
```

For the list of all available placeholders, see the "Placeholders" section below.

## Step 2

Add PIG to your classpath as an annotation processor.

### Maven

There are two ways you can do this in your Maven project.

The preferred way is to add it as an annotation processor directly to your maven-compiler-plugin configuration:

```xml

<build>
    ...
    <plugins>
        ...
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>io.brachu</groupId>
                        <artifactId>pig</artifactId>
                        <version>0.1.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
        ...
    </plugins>
    ...
</build>
```

Alternatively you can add PIG to your project's dependencies:

```xml

<dependencies>
    ...
    <dependency>
        <groupId>io.brachu</groupId>
        <artifactId>pig</artifactId>
        <version>0.1.0</version>
        <optional>true</optional>
    </dependency>
    ...
</dependencies>
```

The compiler plugin should then pick it up automatically, assuming no annotation processors are defined explicitly via its `annotationProcessorPaths` tag.

### Gradle

It's a bit easier in Gradle. You can just add PIG as an annotation processor to your dependencies.

```groovy
dependencies {
    ...
    annotationProcessor 'io.brachu:pig:0.1.0'
    ...
}
```

## Step 3

Just compile your project. All generated package-info.java files should be generated in your "target" directory as a part of generated sources.

# Placeholders

Below is the list of all available placeholders you can use in your `pig.template` file:

| Placeholder                              | Replaced with
| ---------------------------------------- | -------------------------------------------------------------------
| `${GENERATED}`                           | `@javax.annotation.Generated("io.brachu.pig.PackageInfoGenerator")`
| `${GENERATED_SHORT}`                     | `@Generated("io.brachu.pig.PackageInfoGenerator")`
| `${GENERATED_ANNOTATION_CANONICAL_NAME}` | `javax.annotation.Generated`
| `${PACKAGE_NAME}`                        | Name of a package that generated package-info.java file will be placed in

Use of `@Generated` annotation is purely optional, but it's a good practice to annotate generated sources with it.

Note: When compiling project with JDK9+, the `Generated` annotation will actually come from `javax.annotation.processing` package.

# Advanced usage

You can create multiple `pig.template` files in your project. For every package that contains at least one compilation unit, PIG will try to locate closest
`pig.template` file starting from directory representing such package and going up, until it finds one or reaches the root folder. This allows you, for example,
to create a catch-all `pig.template` file in the root directory of your project and a more specific `pig.template` file in `src/main/java/foo/bar` so
package `foo.bar` and all its subpackages will have `package-info.java` files with a different content.
