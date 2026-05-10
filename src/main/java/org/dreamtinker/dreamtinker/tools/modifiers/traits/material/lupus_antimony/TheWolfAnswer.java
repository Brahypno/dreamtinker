package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.utils.LootHelper.DTLoots;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.TheWolfWasDevoter;

public class TheWolfAnswer extends BattleModifier {
    /**
     * Projectile persistent data key for the stat multiplier
     */
    private static final ResourceLocation AMMO_MULTIPLIER = ToolStats.PROJECTILE_DAMAGE.getName().withSuffix("_ammo_multiplier");
    /**
     * Projectile persistent data key for the stat multiplier
     */
    private static final ResourceLocation BOW_MULTIPLIER = ToolStats.PROJECTILE_DAMAGE.getName().withSuffix("_bow_multiplier");

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        LivingEntity target = context.getLivingTarget();
        if (target == null || target.level().isClientSide)
            return;
        float curHP = target.getHealth();
        if (damageAttempted < curHP){
            target.setHealth(curHP - damageAttempted);
            if (target.getHealth() < curHP){
                if (context.getAttacker() instanceof Player player)
                    target.setLastHurtByPlayer(player);
                else
                    target.setLastHurtByMob(context.getAttacker());
            }
        }else {
            DamageSource dam;
            if (context.getAttacker() instanceof Player player)
                dam = context.getAttacker().level()
                             .damageSources()
                             .playerAttack(player);
            else
                dam = context.getAttacker().level()
                             .damageSources()
                             .mobAttack(context.getAttacker());
            target.setHealth(0);
            target.die(dam);
            DTLoots.dropAllDeathLootVanilla(target, dam);
        }
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        int types = 0;
        if (null != context.getLivingTarget())
            types += context.getLivingTarget().getActiveEffects().size();
        return damage * (1 + types * TheWolfWasDevoter.get().floatValue() * tool.getMultiplier(ToolStats.ATTACK_DAMAGE));
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        ResourceLocation key = modifier.getId();
        if (null != target && !target.level().isClientSide && !persistentData.getBoolean(key)){
            persistentData.putBoolean(key, true);
            float multiplier = 1;
            if (persistentData.contains(AMMO_MULTIPLIER, Tag.TAG_ANY_NUMERIC)){
                multiplier *= persistentData.getFloat(AMMO_MULTIPLIER);
            }
            if (persistentData.contains(BOW_MULTIPLIER, Tag.TAG_ANY_NUMERIC)){
                multiplier *= persistentData.getFloat(BOW_MULTIPLIER);
            }
            int types = target.getActiveEffects().size();
            if (projectile instanceof AbstractArrow arrow){
                arrow.setBaseDamage(arrow.getBaseDamage() * (1 + types * TheWolfWasDevoter.get().floatValue() * multiplier));
            }else if (projectile instanceof ProjectileWithPower withPower){
                withPower.setPower(withPower.getPower() * (1 + types * TheWolfWasDevoter.get().floatValue() * multiplier));
            }else
                projectile.setDeltaMovement(projectile.getDeltaMovement().scale((1 + types * TheWolfWasDevoter.get())));
            target.invulnerableTime = 0;
        }
        return false;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (null != context.getLivingTarget() && !context.getLivingTarget().level().isClientSide){
            context.getLivingTarget().invulnerableTime = 0;
        }
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description",
                                                    String.format("%.0f%%", TheWolfWasDevoter.get() * 100))
                                      .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getPriority() {
        return -1000;
    }

}
