package org.dreamtinker.dreamtinker.tools.modifiers.tools.ritual_blade;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.module.weapon.MeleeHitToolHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;
import static slimeknights.tconstruct.library.tools.helper.ToolAttackUtil.*;

public class SelfSacrifice extends BattleModifier {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addModule(ToolTankHelper.TANK_HANDLER);
        builder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
    }

    @Override
    public int getPriority() {
        return 27000; // my custom splt, so should be earlier enough
    }

    @Override
    public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return BlockingModifier.blockWhileCharging(tool, UseAnim.BOW);
    }

    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (isAttackable(player, player)){
            selfAttack(tool, ToolAttackContext.attacker(player).target(player).defaultCooldown().applyStats(tool).build());
        }
        return InteractionResult.PASS;
    }

    private void meltTarget(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity target, float damageDealt) {
        // must have done damage, and must be fully charged
        if (damageDealt > 0){
            if (target != null){
                FluidStack output = new FluidStack(DreamtinkerFluids.blood_soul.get(), FluidValues.GLASS_PANE / 5);
                int damagePerOutput;
                damagePerOutput = 2;
                FluidStack fluid = TANK_HELPER.getFluid(tool);
                if (fluid.isEmpty() || fluid.isFluidEqual(output)){
                    // recipe amount determines how much we get per hit, up to twice the recipe damage
                    int fluidAmount;
                    if (damageDealt < damagePerOutput * 2){
                        fluidAmount = (int) (output.getAmount() * damageDealt / damagePerOutput);
                    }else {
                        fluidAmount = output.getAmount() * 2;
                    }

                    // fluid must match that which is stored in the tank
                    if (fluid.isEmpty()){
                        output.setAmount(fluidAmount);
                        fluid = output;
                    }else {
                        fluid.grow(fluidAmount);
                    }
                    TANK_HELPER.setFluid(tool, fluid);
                }
            }
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.isFullyCharged() && context.getAttacker().equals(context.getLivingTarget())){
            meltTarget(tool, modifier, context.getLivingTarget(), damageDealt);
            if (!context.getAttacker().level().isClientSide){
                context.getAttacker().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, (int) (20 * damageDealt * 4), 1));
                context.getAttacker().addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, (int) (20 * damageDealt * 4), 1));
                context.getAttacker().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, (int) (20 * damageDealt * 2), 0));
            }
        }
    }

    public static boolean selfAttack(IToolStackView tool, ToolAttackContext context) {

        // calculate conditional damage from modifiers
        float baseDamage = context.getBaseDamage();
        float damage = baseDamage;
        List<ModifierEntry> modifiers = tool.getModifierList();
        for (ModifierEntry entry : modifiers) {
            damage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, entry, context, baseDamage, damage);
        }
        // no damage? do nothing
        if (damage <= 0){
            return false;
        }
        // checked immediately in case anything else changes damage
        boolean isMagic = damage > baseDamage;

        // knockback moved lower

        // apply critical damage boost
        float criticalModifier = context.getCriticalModifier();
        if (criticalModifier != 1){
            damage += baseDamage * (criticalModifier - 1);
        }

        // removed: sword check hook, replaced by weapon callback
        // removed: fire aspect check, replaced by before damage lower

        // apply cutoff and cooldown, store if damage was above base for magic particles
        float cooldown = context.getCooldown();
        if (cooldown < 1){
            damage *= (0.2f + cooldown * cooldown * 0.8f);
        }

        // track original health and motion before attack
        // Vec3 originalTargetMotion = targetEntity.getDeltaMovement();
        float oldHealth = 0.0F;
        LivingEntity targetLiving = context.getLivingTarget();
        if (targetLiving != null){
            oldHealth = targetLiving.getHealth();
        }

        AttributeInstance knockbackModifier = disableKnockback(targetLiving);

        ///////////////////
        // actual attack //
        ///////////////////

        // removed: sword special attack check and logic, replaced by this
        boolean didHit;
        Entity targetEntity = context.getTarget();
        boolean isExtraAttack = context.isExtraAttack();
        if (isExtraAttack){
            didHit = targetEntity.hurt(context.makeDamageSource(), damage);
        }else {
            didHit = MeleeHitToolHook.dealDamage(tool, context, damage);
        }

        // reset knockback if needed
        enableKnockback(knockbackModifier);

        LivingEntity attackerLiving = context.getAttacker();
        EquipmentSlot sourceSlot = context.getSlotType();
        // if we failed to hit, fire failure hooks
        Level level = context.getLevel();
        if (!didHit){
            if (!isExtraAttack){
                level.playSound(null, attackerLiving.getX(), attackerLiving.getY(), attackerLiving.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE,
                                attackerLiving.getSoundSource(), 1.0F, 1.0F);
            }
            // alert modifiers nothing was hit, mainly used for fiery
            for (ModifierEntry entry : modifiers) {
                entry.getHook(ModifierHooks.MELEE_HIT).failedMeleeHit(tool, entry, context, damage);
            }
            return false;
        }

        // determine damage actually dealt
        float damageDealt = damage;
        if (targetLiving != null){
            damageDealt = oldHealth - targetLiving.getHealth();
        }
        // apply velocity change to players if needed
        if (targetEntity.hurtMarked && targetEntity instanceof ServerPlayer serverPlayer){
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(targetEntity));
            targetEntity.hurtMarked = false;
        }

        // play sound effects and particles

        Player attackerPlayer = context.getPlayerAttacker();
        if (attackerPlayer != null){
            // particles
            if (criticalModifier > 1){
                attackerPlayer.crit(targetEntity);
            }
            if (isMagic){
                attackerPlayer.magicCrit(targetEntity);
            }
            // sounds
            level.playSound(null, attackerLiving.getX(), attackerLiving.getY(), attackerLiving.getZ(), context.getSound(), attackerLiving.getSoundSource(),
                            1.0F, 1.0F);
        }
        if (damageDealt > 2.0F && level instanceof ServerLevel server){
            int particleCount = (int) (damageDealt * 0.5f);
            server.sendParticles(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getX(), targetEntity.getY(0.5), targetEntity.getZ(), particleCount, 0.1, 0, 0.1,
                                 0.2);
        }

        // deal attacker thorns damage
        attackerLiving.setLastHurtMob(targetEntity);
        if (targetLiving != null){
            EnchantmentHelper.doPostHurtEffects(targetLiving, attackerLiving);
        }

        ModifierEntry entry = tool.getModifier(DreamtinkerModifiers.self_sacrifice.get());
        entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, entry, context, damageDealt);

        // hurt resistance adjustment for high speed weapons
        float speed = tool.getStats().get(ToolStats.ATTACK_SPEED);
        int time = Math.round(20f / speed);
        if (time < targetEntity.invulnerableTime){
            targetEntity.invulnerableTime = (targetEntity.invulnerableTime + time) / 2;
        }

        // final attack hooks
        if (attackerPlayer != null){
            if (targetLiving != null){
                if (!level.isClientSide && !isExtraAttack){
                    ItemStack held = attackerLiving.getItemBySlot(sourceSlot);
                    if (!held.isEmpty()){
                        held.hurtEnemy(targetLiving, attackerPlayer);
                    }
                }
                attackerPlayer.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));
            }
            // add usage stat
            if (!isExtraAttack){
                attackerPlayer.awardStat(Stats.ITEM_USED.get(tool.getItem()));
            }
        }

        // damage the tool
        if (!tool.hasTag(TinkerTags.Items.UNARMED)){
            int durabilityLost = targetLiving != null ? 1 : 0;
            if (!tool.hasTag(TinkerTags.Items.MELEE_PRIMARY)){
                durabilityLost *= 2;
            }
            ToolDamageUtil.damageAnimated(tool, durabilityLost, attackerLiving, sourceSlot);
        }

        return true;
    }
}
