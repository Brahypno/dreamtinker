package org.dreamtinker.dreamtinker.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.function.Predicate;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ProjLimit;

public class DThelper {
    public static void clearProjectile(ServerLevel level, double px, double pz) {
        int viewDist = level.getServer().getPlayerList().getViewDistance();
        double radius = viewDist * 16.0;
        AABB box = new AABB(px - radius, level.getMinBuildHeight(), pz - radius, px + radius, level.getMaxBuildHeight(), pz + radius);
        Predicate<Projectile> all = p -> true;
        List<Projectile> list = level.getEntitiesOfClass(Projectile.class, box, all);
        if (ProjLimit.get() <= list.size())
            for (Projectile old : list)
                old.remove(Entity.RemovalReason.DISCARDED);
    }

    private static boolean isMatch(ItemStack stack, ModifierNBT target, boolean compareUpgrades) {
        if (stack.isEmpty() || !stack.is(TinkerTags.Items.MODIFIABLE))
            return false;
        ToolStack tool = ToolStack.from(stack);
        ModifierNBT mine = compareUpgrades ? tool.getUpgrades() : tool.getModifiers();
        return mine.equals(target);
    }

    public static ItemStack findItemByModifierNBT(LivingEntity entity, ModifierNBT target, boolean compareUpgrades) {
        ItemStack main = entity.getMainHandItem();
        if (isMatch(main, target, compareUpgrades)){
            return main;
        }
        ItemStack off = entity.getOffhandItem();
        if (isMatch(off, target, compareUpgrades)){
            return off;
        }
        if (entity instanceof Player player)
            for (int i = 0; i < 9; i++) {
                ItemStack hot = player.getInventory().getItem(i);
                if (isMatch(hot, target, compareUpgrades)){
                    return hot;
                }
            }
        return null;
    }

    public static void debugEffects(List<MobEffect> effects) {
        for (MobEffect effect : effects) {
            ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
            String key = effect.getDescriptionId();
            System.out.println("Random effect → {" + id + "} ({" + key + "})");
        }
    }
}
