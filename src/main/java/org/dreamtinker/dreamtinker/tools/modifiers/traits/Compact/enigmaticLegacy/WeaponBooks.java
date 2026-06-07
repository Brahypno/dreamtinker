package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.CompactUtils.EnigmaticLegacyCompact;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.EldritchPan.getBloodlust;

public class WeaponBooks extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, ValidateModifierHook {

    public static final ResourceLocation TAG_SEC = Dreamtinker.getLocation("deeper_curse_actived");

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT,
                            ModifierHooks.VALIDATE);
        hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.cursed_ring_bound.getId(), 1, false));
        super.registerHooks(hookBuilder);
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        if (2 <= modifier.getLevel()){
            tool.getPersistentData().putInt(CursedRingBound.TAG_DEEP_CURSE, 1);
            tool.getPersistentData().putBoolean(TAG_SEC, true);
        }else if (tool.getPersistentData().getBoolean(TAG_SEC)){
            tool.getPersistentData().putInt(CursedRingBound.TAG_DEEP_CURSE, Math.max(0, tool.getPersistentData().getInt(CursedRingBound.TAG_DEEP_CURSE) - 1));
            tool.getPersistentData().remove(TAG_SEC);
        }
        return null;
    }

    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        String book_key_1 = "item.enigmaticlegacy.the_twist";
        String book_key_2 = "item.enigmaticlegacy.the_infinitum";
        if (1 == level)
            return Component.translatable(book_key_1);
        else
            return Component.translatable(book_key_2);

    }


    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        if (1 == level)
            return Arrays.asList(Component.translatable("tooltip.enigmaticlegacy.theTwist1").withStyle(ChatFormatting.ITALIC).append(
                                         Component.translatable("tooltip.enigmaticlegacy.theTwist2").withStyle(ChatFormatting.GRAY)),
                                 Component.translatable(getTranslationKey() + ".description").withStyle(ChatFormatting.GRAY));
        else
            return Arrays.asList(Component.translatable("tooltip.enigmaticlegacy.theInfinitum1").withStyle(ChatFormatting.ITALIC).append(
                                         Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly2").withStyle(ChatFormatting.GRAY)),
                                 Component.translatable(getTranslationKey() + ".description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        float knockbackPower = 1F;
        if (context.getAttacker() instanceof Player player && !player.level().isClientSide){
            if (0 < tool.getModifierLevel(this.getId()) && EnigmaticLegacyCompact.isTheCursedOne(player)){
                context.getTarget().setSecondsOnFire(20);
            }
            if (1 == tool.getModifierLevel(this.getId()) && EnigmaticLegacyCompact.isTheCursedOne(player)){
                knockbackPower += EnigmaticLegacyCompact.twistKnockbackBonusModifier();
            }else if (2 <= tool.getModifierLevel(this.getId()) && EnigmaticLegacyCompact.isTheWorthyOne(player)){
                if (3 <= tool.getModifierLevel(this.getId()) && EnigmaticLegacyCompact.isTheWorthyOne(player))
                    knockbackPower += EnigmaticLegacyCompact.twistKnockbackBonusModifier();
                knockbackPower += EnigmaticLegacyCompact.infinitumKnockbackBonusModifier();
            }
        }
        return knockback * knockbackPower;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        float bonus = 0;
        if (context.getAttacker() instanceof Player player){
            if (EnigmaticLegacyCompact.isTheCursedOne(player) || player instanceof ServerPlayer sp && sp.getAbilities().instabuild){
                LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
                if (null != target && EnigmaticLegacyCompact.isBossOrPlayer(target))
                    if (1 == tool.getModifierLevel(this.getId())){
                        bonus += damage * EnigmaticLegacyCompact.twistBossDamageBonusModifier();
                    }else if (2 <= tool.getModifierLevel(this.getId())){
                        if (3 <= tool.getModifierLevel(this.getId()))
                            bonus += damage * EnigmaticLegacyCompact.twistBossDamageBonusModifier();
                        bonus += damage * EnigmaticLegacyCompact.infinitumBossDamageBonusModifier();
                    }
            }else {
                damage = 0;
                if (2 <= tool.getModifierLevel(this.getId())){
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 160, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 500, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 3, false, true));
                }
            }
        }
        return damage + bonus;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (2 <= tool.getModifierLevel(this.getId()))
            if (context.getAttacker() instanceof Player player && !player.level().isClientSide
                && (EnigmaticLegacyCompact.isTheWorthyOne(player) || player instanceof ServerPlayer sp && sp.getAbilities().instabuild)){
                float lifesteal = damageDealt * 0.1F;

                if (null != getBloodlust() && player.hasEffect(getBloodlust())){
                    int amplifier = 1 + player.getEffect(getBloodlust()).getAmplifier();
                    lifesteal += (float) (damageDealt * (EnigmaticLegacyCompact.bloodlustLifestealBoost() * amplifier));
                }
                player.heal(lifesteal);
                LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
                if (null != target){
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, 160, 3, false, true));
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 500, 3, false, true));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 3, false, true));
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 3, false, true));
                    target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 3, false, true));
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 3, false, true));
                }
            }
    }

    private void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !event.getEntity().level().isClientSide){
            if (2 <= DTModifierCheck.getMainhandModifierLevel(player, this.getId()) &&
                Math.random() <= EnigmaticLegacyCompact.infinitumUndeadProbabilityMultiplier()){
                event.setCanceled(true);
                player.setHealth(1.0F);
            }
        }
    }
}
