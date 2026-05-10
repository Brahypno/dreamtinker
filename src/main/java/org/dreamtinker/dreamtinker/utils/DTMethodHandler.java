package org.dreamtinker.dreamtinker.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class DTMethodHandler {
    public static Method findMethod(
            Class<?> owner,
            String primaryName,
            String alias,
            Class<?>[] paramTypes,
            Class<?> returnType,
            boolean mustBeInstance
    ) throws NoSuchMethodException {

        // 1) primary name
        try {
            return owner.getDeclaredMethod(primaryName, paramTypes);
        }
        catch (NoSuchMethodException ignored) {}

        // 2) aliases (SRG/obf/旧名)
        try {
            return owner.getDeclaredMethod(alias, paramTypes);
        }
        catch (NoSuchMethodException ignored) {}

        // 3) signature scan fallback (1.20+ 最实用)
        for (Method m : owner.getDeclaredMethods()) {
            if (m.getParameterCount() != paramTypes.length)
                continue;
            if (!Arrays.equals(m.getParameterTypes(), paramTypes))
                continue;
            if (returnType != null && m.getReturnType() != returnType)
                continue;
            if (mustBeInstance && Modifier.isStatic(m.getModifiers()))
                continue;
            return m;
        }

        throw new NoSuchMethodException(owner.getName() + " method not found: " + primaryName
                                        + " / alias=" + alias
                                        + " params=" + Arrays.toString(paramTypes));
    }

}
