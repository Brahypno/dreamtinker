package org.brahypno.dreamtinker.utils.CompatUtils.bloodmagic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import wayoftime.bloodmagic.api.compat.EnumDemonWillType;
import wayoftime.bloodmagic.api.compat.IDemonWill;
import wayoftime.bloodmagic.common.item.BloodMagicItems;
import wayoftime.bloodmagic.will.PlayerDemonWillHandler;

import java.util.List;
import java.util.Locale;

/** Optional Blood Magic facade. The historical name is retained for existing callers. */
public final class BloodMagicWillReflect {
    private BloodMagicWillReflect() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded("bloodmagic");
    }

    public static Object getLargestWillType(Player player) {
        return isLoaded() ? Loaded.getLargestWillType(player) : null;
    }

    public static double getTotalDemonWill(Object type, Player player) {
        return isLoaded() && type != null ? Loaded.getTotalDemonWill(type, player) : 0;
    }

    public static double consumeDemonWill(Object type, Player player, double amount) {
        return isLoaded() && type != null && amount > 0 ? Loaded.consumeDemonWill(type, player, amount) : 0;
    }

    public static void addWillToEntityGemsOrLoot(LivingEntity attacker, ItemStack willStack, List<ItemStack> generatedLoot) {
        if (willStack.isEmpty())
            return;
        if (attacker instanceof Player player)
            addWillToPlayerGemsOrLoot(player, willStack, generatedLoot);
        else
            generatedLoot.add(willStack);
    }

    public static void addWillToPlayerGemsOrLoot(Player player, ItemStack willStack, List<ItemStack> generatedLoot) {
        if (willStack.isEmpty())
            return;
        ItemStack remainder = insertWillIntoSoulGems(player, willStack);
        if (!remainder.isEmpty())
            generatedLoot.add(remainder);
    }

    public static String typeName(Object type) {
        return type == null ? "DEFAULT" : String.valueOf(type);
    }

    public static ItemStack insertWillIntoSoulGems(Player player, ItemStack willStack) {
        return player != null && !willStack.isEmpty() && isLoaded() ? Loaded.insertWill(player, willStack) : willStack;
    }

    public static ItemStack createWillStack(String typeName, double amount) {
        return isLoaded() && amount > 0 ? Loaded.createWillStack(typeName, amount) : ItemStack.EMPTY;
    }

    // Reached only with Blood Magic installed. Opaque types stay behind the facade.
    private static final class Loaded {
        private static Object getLargestWillType(Player player) {
            return PlayerDemonWillHandler.getLargestWillType(player);
        }

        private static double getTotalDemonWill(Object type, Player player) {
            return PlayerDemonWillHandler.getTotalDemonWill((EnumDemonWillType) type, player);
        }

        private static double consumeDemonWill(Object type, Player player, double amount) {
            return PlayerDemonWillHandler.consumeDemonWill((EnumDemonWillType) type, player, amount);
        }

        private static ItemStack insertWill(Player player, ItemStack willStack) {
            return PlayerDemonWillHandler.addDemonWill(player, willStack);
        }

        private static ItemStack createWillStack(String typeName, double amount) {
            String name = typeName == null ? "default" : typeName.toLowerCase(Locale.ROOT);
            Item soulItem = switch (name) {
                case "corrosive" -> BloodMagicItems.MONSTER_SOUL_CORROSIVE.get();
                case "destructive" -> BloodMagicItems.MONSTER_SOUL_DESTRUCTIVE.get();
                case "vengeful" -> BloodMagicItems.MONSTER_SOUL_VENGEFUL.get();
                case "steadfast" -> BloodMagicItems.MONSTER_SOUL_STEADFAST.get();
                default -> BloodMagicItems.MONSTER_SOUL_RAW.get();
            };
            return ((IDemonWill) soulItem).createWill(amount);
        }
    }
}
