package io.brachu.pig.javac;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import sun.misc.Unsafe;

/**
 * A subset of permit-reflect library functionality.
 *
 * @see <a href="https://github.com/nqzero/permit-reflect">Permit-reflect github page</a>
 */
final class Permit {

    private static final long ACCESSIBLE_OVERRIDE_FIELD_OFFSET;
    private static final Unsafe UNSAFE = getUnsafe();

    private static class Fake {

        boolean override;

    }

    static {
        long offset;
        try {
            offset = getOverrideFieldOffset();
        } catch (Throwable t) {
            offset = -1L;
        }
        ACCESSIBLE_OVERRIDE_FIELD_OFFSET = offset;
    }

    private Permit() {
    }

    static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        return setAccessible(method);
    }

    static <T extends AccessibleObject> T setAccessible(T accessor) {
        if (ACCESSIBLE_OVERRIDE_FIELD_OFFSET != -1) {
            UNSAFE.putBoolean(accessor, ACCESSIBLE_OVERRIDE_FIELD_OFFSET, true);
        } else {
            accessor.setAccessible(true);
        }
        return accessor;
    }

    private static long getOverrideFieldOffset() throws Exception {
        Field field = null;
        Exception saved = null;
        try {
            field = AccessibleObject.class.getDeclaredField("override");
        } catch (Exception ex) {
            saved = ex;
        }

        if (field != null) {
            return UNSAFE.objectFieldOffset(field);
        }
        /*
            The below seems very risky, but for all AccessibleObjects in java today it does work, and starting with JDK12,
            making the field accessible is no longer possible.
         */
        try {
            return UNSAFE.objectFieldOffset(Fake.class.getDeclaredField("override"));
        } catch (Exception ex) {
            throw saved;
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not retrieve Unsafe object due to unexpected error", ex);
        }
    }

}
