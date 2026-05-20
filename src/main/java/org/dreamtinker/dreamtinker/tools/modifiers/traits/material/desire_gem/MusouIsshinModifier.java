package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.desire_gem;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.dreamtinker.dreamtinker.Entity.CrescentSlashProjectile;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.LeftClickHook;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import static org.dreamtinker.dreamtinker.tools.modifiers.traits.material.desire_gem.VisionaryWishes.COOLDOWN_DURATION;

public class MusouIsshinModifier extends Modifier implements LeftClickHook {
    private static final double SEARCH_RANGE = 12.0D;
    private static final int BASE_BOOST_DURATION = 20 * 8;
    private static final int BOOST_DURATION_PER_LEVEL = 20 * 2;

    private static LivingEntity findTarget(ServerPlayer player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        AABB box = player.getBoundingBox().inflate(range);

        LivingEntity best = null;
        double bestScore = 0.0D;

        for (LivingEntity entity : player.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e.isAlive() && e != player && !e.isSpectator()
        )) {
            Vec3 to = entity.getBoundingBox().getCenter().subtract(eye);
            double distSqr = to.lengthSqr();

            if (distSqr < 1.0E-6D || distSqr > range * range){
                continue;
            }

            double score = look.dot(to.normalize()) / Math.sqrt(distSqr);

            if (score > bestScore){
                bestScore = score;
                best = entity;
            }
        }

        return best;
    }

    private static void shootCrescent(IToolStackView tool, ModifierEntry modifier, ServerPlayer player, LivingEntity target) {
        ServerLevel level = player.serverLevel();

        float power = 6.0F + (7.62f + 0.4f * (modifier.getLevel() - 1)) * DTModifierCheck.getMeleeDamage(player, target, tool, true);
        int maxLife = 40 + 20 * modifier.getLevel();
        double speed = 1.35D + 0.15D * modifier.getLevel();

        CrescentSlashProjectile.shootDangerousFrom(
                level,
                player,
                power,
                maxLife,
                speed
        );
    }

    @Override
    public void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, DreamtinkerHook.LEFT_CLICK);
        hookBuilder.addModule(new RarityModule(Rarity.RARE));
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        if (player instanceof ServerPlayer sp)
            ReleaseWishes(tool, entry, sp, equipmentSlot);
    }

    @Override
    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        if (player instanceof ServerPlayer sp)
            ReleaseWishes(tool, entry, sp, equipmentSlot);
    }

    public void ReleaseWishes(IToolStackView tool, ModifierEntry modifier, ServerPlayer player, EquipmentSlot equipmentSlot) {
        ItemStack stack = player.getItemBySlot(equipmentSlot);
        if (WishPowerData.updateState(tool, player.serverLevel(), COOLDOWN_DURATION)){
            if (!stack.isEmpty() && stack.is(TinkerTags.Items.MODIFIABLE))
                VisionaryWishes.updateStack(stack, player, false);
        }
        if (!WishPowerData.canRelease(tool, player.level()))
            return;
        if (!stack.isEmpty() && stack.is(TinkerTags.Items.MODIFIABLE))
            VisionaryWishes.updateStack(stack, player, true);
        ToolStack ts = ToolStack.from(stack);

        LivingEntity target = findTarget(player, SEARCH_RANGE);
        if (target == null){
            return;
        }

        shootCrescent(tool, modifier, player, target);

        long boostEnd = player.level().getGameTime()
                        + BASE_BOOST_DURATION
                        + (long) BOOST_DURATION_PER_LEVEL * modifier.getLevel();

        WishPowerData.consumeAll(ts);
        WishPowerData.setBoostUntil(ts, boostEnd);
    }
}
