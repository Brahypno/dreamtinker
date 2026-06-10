package org.brahypno.dreamtinker.tools.modifiers.traits.material.OathSteel;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.Entity.WingSlashProjectile;
import org.brahypno.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

import static org.brahypno.dreamtinker.tools.modifiers.events.OathGuardPaleSteelEvents.oathBrokenSteelId;

public class BrokenOath extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, ModifyDamageModifierHook, TooltipModifierHook {
    private static final float MAX_DESPAIR = 100.0F;

    private static String formatDespair(float value) {
        return value == (int) value ? Integer.toString((int) value) : String.format(Locale.ROOT, "%.1f", value);
    }

    private static float getDespair(ModDataNBT data) {
        return data.getFloat(oathBrokenSteelId);
    }

    private static void addDespair(ModDataNBT data, float amount) {
        if (amount <= 0.0F){
            return;
        }

        data.putFloat(oathBrokenSteelId, Mth.clamp(getDespair(data) + amount, 0.0F, MAX_DESPAIR));
    }

    private static boolean isDespairFull(ModDataNBT data) {
        return getDespair(data) >= MAX_DESPAIR;
    }

    private static void consumeDespair(ModDataNBT data) {
        data.putFloat(oathBrokenSteelId, 0.0F);
    }

    private static void tryReleaseDespairRain(ModDataNBT data, Player player, Level world, float damageBasis, int modifierLevel) {
        if (world.isClientSide || damageBasis <= 0.0F || !isDespairFull(data)){
            return;
        }

        consumeDespair(data);
        shootDespairRain(player, world, damageBasis, modifierLevel);
    }

    private static void shootDespairRain(Player player, Level world, float damageBasis, int modifierLevel) {
        if (world.isClientSide || player.getAttackStrengthScale(0) <= 0.6F){
            return;
        }

        int count = 3 + modifierLevel;
        int maxLife = 35 + 8 * modifierLevel;
        float totalPower = damageBasis * (0.70F + 0.20F * modifierLevel);
        float power = totalPower / count;
        byte pierce = (byte) (modifierLevel + 1);
        double speed = 1.35D + 0.15D * modifierLevel;
        float spread = 48.0F + 8.0F * modifierLevel;

        for (int i = 0; i < count; ++i) {
            float yawOffset = count == 1 ? 0.0F : -spread * 0.5F + spread * i / (count - 1);
            float pitchOffset = -4.0F + 2.0F * Math.abs(i - (count - 1) * 0.5F);
            Vec3 direction = Vec3.directionFromRotation(player.getXRot() + pitchOffset, player.getYRot() + yawOffset);

            WingSlashProjectile.shootFrom(
                    world,
                    player,
                    direction,
                    power,
                    maxLife,
                    pierce,
                    speed,
                    0xBFD5FF,
                    230,
                    1.35F,
                    0.85F,
                    18.0F + 6.0F * i
            );
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!(context.getAttacker() instanceof ServerPlayer player) || !(context.getLevel() instanceof ServerLevel level) || damageDealt <= 0.0F){
            return;
        }

        ModDataNBT data = tool.getPersistentData();
        addDespair(data, damageDealt * (0.30F + 0.05F * modifier.getLevel()));
        tryReleaseDespairRain(data, player, level, damageDealt, modifier.getLevel());
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (!notBlocked || !(projectile.level() instanceof ServerLevel level) || !(attacker instanceof ServerPlayer player) || null == target){
            return false;
        }

        float damage = DTModifierCheck.getDamage(projectile);
        if (damage <= 0.0F){
            damage = (float) projectile.getDeltaMovement().length();
        }

        addDespair(persistentData, damage * (0.30F + 0.05F * modifier.getLevel()));
        tryReleaseDespairRain(persistentData, player, level, damage, modifier.getLevel());

        return false;
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof ServerPlayer player) || player.level().isClientSide() || amount <= 0.0F){
            return amount;
        }

        addDespair(tool.getPersistentData(), amount * (0.45F + 0.10F * modifier.getLevel()));
        return amount;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        float despair = tool.getPersistentData().getFloat(oathBrokenSteelId);

        tooltip.add(Component.translatable("tooltip.dreamtinker.forlorn_steel.despair", formatDespair(despair), formatDespair(MAX_DESPAIR))
                             .withStyle(ChatFormatting.DARK_AQUA));

        if (despair >= MAX_DESPAIR){
            tooltip.add(Component.translatable("tooltip.dreamtinker.forlorn_steel.despair.full").withStyle(ChatFormatting.DARK_PURPLE));
        }else if (despair >= 60.0F){
            tooltip.add(Component.translatable("tooltip.dreamtinker.forlorn_steel.despair.middle").withStyle(ChatFormatting.BLUE));
        }else if (despair > 0.0F){
            tooltip.add(Component.translatable("tooltip.dreamtinker.forlorn_steel.despair.low").withStyle(ChatFormatting.GRAY));
        }
    }
}
