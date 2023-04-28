package io.brachu.pig;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;

@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
public final class PackageInfoGenerator extends AbstractProcessor {

    private final Set<String> ignoredPackages = new HashSet<>();

    public PackageInfoGenerator() {
        ignoredPackages.add("");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            Map<String, TypeElement> packagesToProcess = new HashMap<>();
            for (Element element : roundEnv.getRootElements()) {
                if (element instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) element;
                    if (typeElement.getNestingKind() == NestingKind.TOP_LEVEL) {
                        processTopLevelType(packagesToProcess, typeElement);
                    }
                } else if (element instanceof PackageElement) {
                    processPackage(packagesToProcess, (PackageElement) element);
                }
            }
            generatePackageInfos(packagesToProcess);
        }
        return false;
    }

    private void processTopLevelType(Map<String, TypeElement> packagesToProcess, TypeElement type) {
        PackageElement pkg = (PackageElement) type.getEnclosingElement();
        String pkgName = pkg.getQualifiedName().toString();
        if (!packagesToProcess.containsKey(pkgName) && !ignoredPackages.contains(pkgName)) {
            packagesToProcess.put(pkgName, type);
        }
    }

    private void processPackage(Map<String, TypeElement> packagesToProcess, PackageElement pkg) {
        String pkgName = pkg.getQualifiedName().toString();
        ignoredPackages.add(pkgName);
        packagesToProcess.remove(pkgName);
    }

    private void generatePackageInfos(Map<String, TypeElement> packagesToProcess) {
        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        PackageInfoTemplateProvider templateProvider = new PackageInfoTemplateProvider(processingEnv);

        for (Entry<String, TypeElement> entry : packagesToProcess.entrySet()) {
            String pkgName = entry.getKey();
            TypeElement type = entry.getValue();
            try {
                generatePackageInfo(pkgName, type, filer, templateProvider);
            } catch (IOException ex) {
                Element pkg = type.getEnclosingElement();
                messager.printMessage(Diagnostic.Kind.ERROR, "[PIG] I/O Exception during creation of package-info.java file: " + ex.getMessage(), pkg);
            } catch (Exception ex) {
                Element pkg = type.getEnclosingElement();
                messager.printMessage(Diagnostic.Kind.ERROR, "[PIG] Exception during creation of package-info.java file: " + ex.getMessage(), pkg);
            }
        }
    }

    private void generatePackageInfo(String pkgName, TypeElement type, Filer filer, PackageInfoTemplateProvider templateProvider) throws IOException {
        JavaFileObject fileObject = filer.createSourceFile(pkgName + ".package-info", type.getEnclosingElement());
        try (Writer writer = openWriter(fileObject)) {
            templateProvider.provideFor(type).write(writer, pkgName);
            writer.flush();
        }
    }

    private BufferedWriter openWriter(JavaFileObject fileObject) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(fileObject.openOutputStream(), StandardCharsets.UTF_8));
    }

}
