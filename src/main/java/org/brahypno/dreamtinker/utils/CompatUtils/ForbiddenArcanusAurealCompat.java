package org.brahypno.dreamtinker.utils.CompatUtils;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import com.stal111.forbidden_arcanus.common.aureal.capability.AurealProvider;

public final class ForbiddenArcanusAurealCompat {
    private static final String MODID = "forbidden_arcanus";

    public static Values getValues(Player player) {
        if (player == null || !ModList.get().isLoaded(MODID)){
            return Values.ZERO;
        }

        return Loaded.getValues(player);
    }

    public static int getAureal(Player player) {
        return getValues(player).aureal();
    }

    public static int getCorruption(Player player) {
        return getValues(player).corruption();
    }

    // Only loaded after the mod-presence check; the outer facade has no optional API types.
    private static final class Loaded {
        private static Values getValues(Player player) {
            return player.getCapability(AurealProvider.CAPABILITY)
                         .map(aureal -> new Values(aureal.getAureal(), aureal.getCorruption()))
                         .orElse(Values.ZERO);
        }
    }

    public record Values(int aureal, int corruption) {
        public static final Values ZERO = new Values(0, 0);
    }
}
