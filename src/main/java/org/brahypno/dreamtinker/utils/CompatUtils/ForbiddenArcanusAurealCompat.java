package org.brahypno.dreamtinker.utils.CompatUtils;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public final class ForbiddenArcanusAurealCompat {
    private static final String MODID = "forbidden_arcanus";

    private static final String AUREAL_PROVIDER =
            "com.stal111.forbidden_arcanus.common.aureal.capability.AurealProvider";

    private static final String IAUREAL =
            "com.stal111.forbidden_arcanus.common.aureal.capability.IAureal";

    private static boolean resolved = false;
    private static Capability<?> aurealCapability;
    private static Method getAurealMethod;
    private static Method getCorruptionMethod;

    public static Values getValues(Player player) {
        if (player == null || !ModList.get().isLoaded(MODID)){
            return Values.ZERO;
        }

        if (!resolve()){
            return Values.ZERO;
        }

        try {
            LazyOptional<?> optional = player.getCapability(aurealCapability);
            Optional<?> resolved = optional.resolve();

            if (resolved.isEmpty()){
                return Values.ZERO;
            }

            Object aureal = resolved.get();

            int aurealValue = (int) getAurealMethod.invoke(aureal);
            int corruptionValue = (int) getCorruptionMethod.invoke(aureal);

            return new Values(aurealValue, corruptionValue);
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            return Values.ZERO;
        }
    }

    public static int getAureal(Player player) {
        return getValues(player).aureal();
    }

    public static int getCorruption(Player player) {
        return getValues(player).corruption();
    }

    private static boolean resolve() {
        if (resolved){
            return aurealCapability != null;
        }

        resolved = true;

        try {
            Class<?> providerClass = Class.forName(AUREAL_PROVIDER);
            Class<?> aurealInterface = Class.forName(IAUREAL);

            Field capabilityField = providerClass.getField("CAPABILITY");
            aurealCapability = (Capability<?>) capabilityField.get(null);

            getAurealMethod = aurealInterface.getMethod("getAureal");
            getCorruptionMethod = aurealInterface.getMethod("getCorruption");

            return true;
        }
        catch (ReflectiveOperationException | LinkageError e) {
            aurealCapability = null;
            getAurealMethod = null;
            getCorruptionMethod = null;
            return false;
        }
    }

    public record Values(int aureal, int corruption) {
        public static final Values ZERO = new Values(0, 0);
    }
}
