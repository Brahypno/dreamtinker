package org.brahypno.dreamtinker.utils;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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

    private static MethodHandle LIVING_HURT_SPECIAL;

    public static boolean invokeLivingHurt(LivingEntity entity, DamageSource source, float amount) {
        try {
            MethodHandle mh = LIVING_HURT_SPECIAL;
            if (mh == null){
                mh = findSpecial(
                        LivingEntity.class,
                        new String[]{"hurt", "m_6469_"},
                        MethodType.methodType(boolean.class, DamageSource.class, float.class)
                );
                LIVING_HURT_SPECIAL = mh;
            }
            return (boolean) mh.invokeExact(entity, source, amount);
        }
        catch (Throwable e) {
            throw new RuntimeException("Failed to invoke LivingEntity#hurt via invoke special", e);
        }
    }

    public static MethodHandle findSpecial(Class<?> owner, String[] names, MethodType type) throws ReflectiveOperationException {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(owner, MethodHandles.lookup());

        for (String name : names) {
            try {
                return lookup.findSpecial(owner, name, type, owner);
            }
            catch (NoSuchMethodException ignored) {}
        }

        throw new NoSuchMethodException(owner.getName() + "#" + String.join("/", names) + type);
    }
}
