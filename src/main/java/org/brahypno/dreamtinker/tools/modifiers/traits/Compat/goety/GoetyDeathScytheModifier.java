package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.LeftClickHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Reproduces Death Scythe's fully-charged slash for a modifiable scythe.
 */
public final class GoetyDeathScytheModifier extends NoLevelsModifier implements LeftClickHook, MeleeHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, EsotericismTinkerHook.LEFT_CLICK, ModifierHooks.MELEE_HIT);
        super.registerHooks(hookBuilder);
    }

    /**
     * Exact Sapped refresh/amplification behavior used by Goety's Death Scythe.
     */
    public static void applySapped(LivingEntity target) {
        int configuredSeconds = ItemConfig.DeathScytheSappedDuration.get();
        if (configuredSeconds <= 0)
            return;
        int duration = MathHelper.secondsToTicks(configuredSeconds);
        MobEffect sapped = GoetyEffects.SAPPED.get();
        MobEffectInstance current = target.getEffect(sapped);
        if (current == null){
            target.addEffect(new MobEffectInstance(sapped, duration));
            target.playSound(SoundEvents.SHIELD_BREAK, 2.0f, 1.0f);
        }else if (ItemConfig.DeathScytheSappedChance.get() > 0
                  && target.getRandom().nextFloat() < ItemConfig.DeathScytheSappedChance.get() / 100.0f){
            EffectsUtil.amplifyEffect(target, sapped, duration, 4, false, true);
            target.playSound(SoundEvents.SHIELD_BREAK, 2.0f, 1.0f);
        }else {
            EffectsUtil.resetDuration(target, sapped, duration);
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (target != null && !target.level().isClientSide)
            applySapped(target);
    }

    private static void strike(IToolStackView tool, Player player, Level level) {
        if (level.isClientSide || tool.isBroken() || player.isSpectator()
            || player.getAttackStrengthScale(0.5f) <= 0.9f){
            return;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 2.0f,
                        0.4f / (level.random.nextFloat() * 0.4f + 0.8f));
        Vec3 view = player.getViewVector(1.0f);
        ScytheSlash slash = new ScytheSlash(
                player.getMainHandItem(), level,
                player.getX() + view.x / 2.0, player.getEyeY() - 0.2, player.getZ() + view.z / 2.0,
                view.x, view.y, view.z);
        slash.setOwner(player);
        slash.setDamage((float) player.getAttributeValue(Attributes.ATTACK_DAMAGE));
        slash.setTotalLife(300);
        level.addFreshEntity(slash);
    }

    @Override
    public void onLeftClickEmpty(
            IToolStackView tool, ModifierEntry entry, Player player,
            Level level, EquipmentSlot equipmentSlot) {
        strike(tool, player, level);
    }

    @Override
    public void onLeftClickEntity(
            AttackEntityEvent event, IToolStackView tool, ModifierEntry entry,
            Player player, Level level, EquipmentSlot equipmentSlot,
            net.minecraft.world.entity.Entity target) {
        strike(tool, player, level);
    }
}
