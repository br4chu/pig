package io.brachu.pig.javac;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.brachu.pig.PackageInfoGenerator;

final class JpmsUtils {

    private JpmsUtils() {
    }

    static void addOpensForPig() {
        Class<?> moduleClazz;
        try {
            moduleClazz = Class.forName("java.lang.Module");
        } catch (ClassNotFoundException e) {
            // we are running JDK8 or earlier. We don't need to do this.
            return;
        }

        Object jdkCompilerModule = getJdkCompilerModule();
        Object ownModule = getOwnModule();
        String[] packagesToOpen = { "com.sun.tools.javac.code" };

        try {
            Method method = Permit.getMethod(moduleClazz, "implAddOpens", String.class, moduleClazz);
            for (String p : packagesToOpen) {
                method.invoke(jdkCompilerModule, p, ownModule);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object getJdkCompilerModule() {
        try {
            Class<?> moduleLayer = Class.forName("java.lang.ModuleLayer");
            Method bootMethod = moduleLayer.getDeclaredMethod("boot");
            Object bootLayer = bootMethod.invoke(null);
            Class<?> optional = Class.forName("java.util.Optional");
            Method findModule = moduleLayer.getDeclaredMethod("findModule", String.class);
            Object compiler = findModule.invoke(bootLayer, "jdk.compiler");
            return optional.getDeclaredMethod("get").invoke(compiler);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not find jdk.compiler module due to unexpected error", ex);
        }
    }

    private static Object getOwnModule() {
        try {
            Method method = Permit.getMethod(Class.class, "getModule");
            return method.invoke(PackageInfoGenerator.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not find PIG's own module due to unexpected error", ex);
        }
    }

}
