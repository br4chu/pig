package io.brachu.packageinfo.generator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
            Map<String, PackageElement> packages = new HashMap<>();

            for (Element element : roundEnv.getRootElements()) {
                if (element instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) element;
                    if (typeElement.getNestingKind() == NestingKind.TOP_LEVEL) {
                        PackageElement pkg = (PackageElement) typeElement.getEnclosingElement();
                        String pkgName = pkg.getQualifiedName().toString();
                        if (!ignoredPackages.contains(pkgName)) {
                            packages.put(pkgName, pkg);
                        }
                    }
                }
                if (element instanceof PackageElement) {
                    PackageElement pkg = (PackageElement) element;
                    String pkgName = pkg.getQualifiedName().toString();
                    ignoredPackages.add(pkgName);
                    packages.remove(pkgName);
                }
            }
            generatePackageInfos(packages);
        }
        return false;
    }

    private void generatePackageInfos(Map<String, PackageElement> packages) {
        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        for (Entry<String, PackageElement> entry : packages.entrySet()) {
            String pkgName = entry.getKey();
            PackageElement pkg = entry.getValue();
            try {
                JavaFileObject fileObject = filer.createSourceFile(pkgName + ".package-info", pkg);
                try (PrintWriter out = new PrintWriter(new OutputStreamWriter(fileObject.openOutputStream()))) {
                    out.println("@Generated(\"" + getClass().getCanonicalName() + "\")");
                    out.println("@NonNullApi");
                    out.println("package " + pkgName + ";");
                    out.println();
                    out.println("import javax.annotation.processing.Generated;");
                    out.println();
                    out.println("import org.springframework.lang.NonNullApi;");
                    out.flush();
                }
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "I/O Exception during creation of package-info.java file: " + e.getMessage(), pkg);
            }
        }
    }

}
