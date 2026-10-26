package org.brahypno.dreamtinker.utils.CompatUtils.bloodmagic;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import wayoftime.bloodmagic.core.data.SoulNetwork;
import wayoftime.bloodmagic.core.data.SoulTicket;
import wayoftime.bloodmagic.util.helper.NetworkHelper;

/** Optional Blood Magic facade. The historical name is retained for existing callers. */
public final class BloodMagicSoulNetworkReflect {
    private BloodMagicSoulNetworkReflect() {}

    public static boolean isBloodMagicLoaded() {
        return ModList.get().isLoaded("bloodmagic");
    }

    public static boolean isAvailable() {
        return isBloodMagicLoaded();
    }

    public static int getOrbTier(Player player) {
        return canUse(player) ? Loaded.getOrbTier(player) : 0;
    }

    public static int getCurrentLP(Player player) {
        return canUse(player) ? Loaded.getCurrentLP(player) : 0;
    }

    public static int getMaxLP(Player player) {
        return canUse(player) ? Loaded.getMaxLP(player) : 0;
    }

    public static boolean hasLP(Player player, int amount) {
        return amount <= 0 || getCurrentLP(player) >= amount;
    }

    public static boolean consumeLP(Player player, int amount) {
        return amount <= 0 || canUse(player) && Loaded.consumeLP(player, amount);
    }

    public static boolean consumeLPOrDamage(Player player, int amount) {
        return amount <= 0 || canUse(player) && Loaded.consumeLPOrDamage(player, amount);
    }

    public static int addLP(Player player, int amount) {
        return amount > 0 && canUse(player) ? Loaded.addLP(player, amount) : 0;
    }

    public static int addLP(Player player, int amount, int maximum) {
        return amount > 0 && maximum > 0 && canUse(player) ? Loaded.addLP(player, amount, maximum) : 0;
    }

    public static boolean tryConsumeLP(Player player, int amount) {
        return consumeLP(player, amount);
    }

    public static boolean tryConsumeLPOrDamage(Player player, int amount) {
        return consumeLPOrDamage(player, amount);
    }

    private static boolean canUse(Player player) {
        return player != null && isAvailable();
    }

    // Keep Blood Magic types out of the facade's fields and method signatures.
    private static final class Loaded {
        private static int getOrbTier(Player player) {
            SoulNetwork network = NetworkHelper.getSoulNetwork(player);
            return network != null ? network.getOrbTier() : 0;
        }

        private static int getCurrentLP(Player player) {
            SoulNetwork network = NetworkHelper.getSoulNetwork(player);
            return network != null ? network.getCurrentEssence() : 0;
        }

        private static int getMaxLP(Player player) {
            SoulNetwork network = NetworkHelper.getSoulNetwork(player);
            return network != null ? NetworkHelper.getMaximumForTier(network.getOrbTier()) : 0;
        }

        private static boolean consumeLP(Player player, int amount) {
            SoulNetwork network = NetworkHelper.getSoulNetwork(player);
            return network != null && network.syphon(new SoulTicket(amount)) >= amount;
        }

        private static boolean consumeLPOrDamage(Player player, int amount) {
            SoulNetwork network = NetworkHelper.getSoulNetwork(player);
            return network != null && network.syphonAndDamage(player, new SoulTicket(amount)).isSuccess();
        }

        private static int addLP(Player player, int amount) {
            SoulNetwork network = NetworkHelper.getSoulNetwork(player);
            return network != null ? network.add(new SoulTicket(amount), NetworkHelper.getMaximumForTier(network.getOrbTier())) : 0;
        }

        private static int addLP(Player player, int amount, int maximum) {
            SoulNetwork network = NetworkHelper.getSoulNetwork(player);
            return network != null ? network.add(new SoulTicket(amount), maximum) : 0;
        }
    }
}
