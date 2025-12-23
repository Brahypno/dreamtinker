package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.bic;

import net.mcreator.borninchaosv.entity.*;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.data.ModifierIds;

import javax.annotation.Nullable;
import java.util.List;

public class darkBlade extends BattleModifier {
    private static final ResourceLocation barbed_attack = new ResourceLocation("born_in_chaos_v1", "barbedattack");
    private static final ResourceLocation light_rampage = new ResourceLocation("born_in_chaos_v1", "light_rampage");
    private static final ResourceLocation medium_rampage = new ResourceLocation("born_in_chaos_v1", "medium_rampage");
    private static final ResourceLocation strong_rampage = new ResourceLocation("born_in_chaos_v1", "strong_rampage");
    private static final ResourceLocation furious_rampage = new ResourceLocation("born_in_chaos_v1", "furious_rampage");
    private static final ResourceLocation rampant_rampage = new ResourceLocation("born_in_chaos_v1", "rampant_rampage");
    private static final ResourceLocation overly_heavy_weapon = new ResourceLocation("born_in_chaos_v1", "overly_heavy_weapon");
    private static final ResourceLocation bone_fracture = new ResourceLocation("born_in_chaos_v1", "bone_fracture");
    private static final ResourceLocation block_break = new ResourceLocation("born_in_chaos_v1", "block_break");
    private static final ResourceLocation sacrifice = new ResourceLocation("born_in_chaos_v1", "sacrifice");
    private static final ResourceLocation gaze_of_terror = new ResourceLocation("born_in_chaos_v1", "gaze_of_terror");
    private static final ResourceLocation soul_stratification = new ResourceLocation("born_in_chaos_v1", "soul_stratification");
    private static final ResourceLocation stun = new ResourceLocation("born_in_chaos_v1", "stun");
    private static final ResourceLocation infernal_flame = new ResourceLocation("born_in_chaos_v1", "infernal_flame");

    @Nullable
    private static MobEffect BARBED_ATTACK;
    @Nullable
    private static MobEffect LIGHT_RAMPAGE;
    @Nullable
    private static MobEffect MEDIUM_RAMPAGE;
    @Nullable
    private static MobEffect STRONG_RAMPAGE;
    @Nullable
    private static MobEffect FURIOUS_RAMPAGE;
    @Nullable
    private static MobEffect RAMPANT_RAMPAGE;
    @Nullable
    private static MobEffect OVERLY_HEAVY_WEAPON;
    @Nullable
    private static MobEffect BONE_FRACTURE;
    @Nullable
    private static MobEffect BLOCK_BREAK;
    @Nullable
    private static MobEffect SACRIFICE;
    @Nullable
    private static MobEffect GAZE_OF_TERROR;
    @Nullable
    private static MobEffect SOUL_STRATIFICATION;
    @Nullable
    private static MobEffect STUN;
    @Nullable
    private static MobEffect INFERNAL_FLAME;


    public darkBlade() {
        if (null == BARBED_ATTACK)
            BARBED_ATTACK = ForgeRegistries.MOB_EFFECTS.getValue(barbed_attack);
        if (null == LIGHT_RAMPAGE)
            LIGHT_RAMPAGE = ForgeRegistries.MOB_EFFECTS.getValue(light_rampage);
        if (null == MEDIUM_RAMPAGE)
            MEDIUM_RAMPAGE = ForgeRegistries.MOB_EFFECTS.getValue(medium_rampage);
        if (null == STRONG_RAMPAGE)
            STRONG_RAMPAGE = ForgeRegistries.MOB_EFFECTS.getValue(strong_rampage);
        if (null == FURIOUS_RAMPAGE)
            FURIOUS_RAMPAGE = ForgeRegistries.MOB_EFFECTS.getValue(furious_rampage);
        if (null == RAMPANT_RAMPAGE)
            RAMPANT_RAMPAGE = ForgeRegistries.MOB_EFFECTS.getValue(rampant_rampage);
        if (null == OVERLY_HEAVY_WEAPON)
            OVERLY_HEAVY_WEAPON = ForgeRegistries.MOB_EFFECTS.getValue(overly_heavy_weapon);
        if (null == BONE_FRACTURE)
            BONE_FRACTURE = ForgeRegistries.MOB_EFFECTS.getValue(bone_fracture);
        if (null == BLOCK_BREAK)
            BLOCK_BREAK = ForgeRegistries.MOB_EFFECTS.getValue(block_break);
        if (null == SACRIFICE)
            SACRIFICE = ForgeRegistries.MOB_EFFECTS.getValue(sacrifice);
        if (null == GAZE_OF_TERROR)
            GAZE_OF_TERROR = ForgeRegistries.MOB_EFFECTS.getValue(gaze_of_terror);
        if (null == SOUL_STRATIFICATION)
            SOUL_STRATIFICATION = ForgeRegistries.MOB_EFFECTS.getValue(soul_stratification);
        if (null == STUN)
            STUN = ForgeRegistries.MOB_EFFECTS.getValue(stun);
        if (null == INFERNAL_FLAME)
            INFERNAL_FLAME = ForgeRegistries.MOB_EFFECTS.getValue(infernal_flame);
    }

    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        LivingEntity attacker = context.getAttacker();
        Level level = attacker.level();
        if (null == target || level.isClientSide)
            return knockback;
        boolean rest = false;

        if (tool.hasTag(TinkerTags.Items.PARRY)){//spiritual divider
            rest = true;
            if (target.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:spirit")))){
                target.hurt(DreamtinkerDamageTypes.source(level.registryAccess(), DamageTypes.GENERIC, null, attacker), 15.0f);
                if (null != BARBED_ATTACK){
                    target.addEffect(new MobEffectInstance(BARBED_ATTACK, 10, 0, false, false));
                }
                ((ServerLevel) level).sendParticles((SimpleParticleType) BornInChaosV1ModParticleTypes.SRIRST_PART.get(), target.getX(),
                                                    target.getY() + (double) 0.5F, target.getZ(), 8, 0.6, 0.6, 0.6, 0.2);

            }

        }
        if (!tool.hasTag(TinkerTags.Items.BROAD_TOOLS) &&
            (tool.hasTag(TinkerTags.Items.SWORD) || tool.hasTag(Dreamtinker.mcItemTag("swords")))){//sharpened DarkMetal Sword
            rest = true;
            if (target.getMobType() == MobType.UNDEAD){
                target.hurt(DreamtinkerDamageTypes.source(level.registryAccess(), DamageTypes.GENERIC, null, attacker),
                            0 < tool.getModifier(ModifierIds.smite).getLevel() ? 20.0F : 14.0f);
                if (null != BARBED_ATTACK && !target.hasEffect(BARBED_ATTACK)){
                    target.addEffect(new MobEffectInstance(BARBED_ATTACK, 10, 0, false, false));
                }
            }
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
        }
        if (tool.hasTag(TinkerTags.Items.BROAD_TOOLS)){
            if (tool.hasTag(TinkerTags.Items.SWORD) || tool.hasTag(Dreamtinker.mcItemTag("swords"))){
                rest = true;
                if (!attacker.hasEffect(MobEffects.DAMAGE_BOOST) && lacksEffect(attacker, LIGHT_RAMPAGE) && lacksEffect(attacker, MEDIUM_RAMPAGE) &&
                    lacksEffect(attacker, STRONG_RAMPAGE) && lacksEffect(attacker, FURIOUS_RAMPAGE) && lacksEffect(attacker, RAMPANT_RAMPAGE))
                    if (null != OVERLY_HEAVY_WEAPON)
                        attacker.addEffect(new MobEffectInstance(OVERLY_HEAVY_WEAPON, 10, 0, false, false));

                if (null != BONE_FRACTURE && !target.hasEffect(BONE_FRACTURE) && lacksEffect(attacker, OVERLY_HEAVY_WEAPON)){
                    target.removeEffect(MobEffects.REGENERATION);
                    level.playSound((Player) null, target.blockPosition(),
                                    (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("born_in_chaos_v1:dark_warlblade_atak")),
                                    SoundSource.NEUTRAL, 1.3F, 0.9F);
                    ((ServerLevel) level).sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + (double) 1.0F, target.getZ(), 9, 0.3, 0.3, 0.3, 0.2);
                    ((ServerLevel) level).sendParticles((SimpleParticleType) BornInChaosV1ModParticleTypes.STUNSTARS.get(), target.getX(),
                                                        target.getY() + (double) 1.0F, target.getZ(), 9, 0.3,
                                                        0.3, 0.3, 0.2);
                    target.addEffect(new MobEffectInstance(BONE_FRACTURE, 200, 0, false, false));
                    if (null != BLOCK_BREAK &&
                        (target instanceof DoorKnightEntity || target instanceof DoorKnightNotDespawnEntity || target instanceof SkeletonThrasherEntity ||
                         target instanceof SkeletonThrasherNotDespawnEntity)){
                        target.addEffect(new MobEffectInstance(BLOCK_BREAK, 360, 0, false, false));
                    }
                }
                if (null != RAMPANT_RAMPAGE && target instanceof CorpseFlyEntity && attacker instanceof ServerPlayer sp && sp.hasEffect(RAMPANT_RAMPAGE)){
                    Advancement _adv = sp.server.getAdvancements().getAdvancement(new ResourceLocation("born_in_chaos_v1:excessive_fly_swatter"));
                    AdvancementProgress _ap = null;
                    if (_adv != null){
                        _ap = sp.getAdvancements().getOrStartProgress(_adv);
                    }
                    if (_ap != null && !_ap.isDone()){
                        for (String criteria : _ap.getRemainingCriteria()) {
                            sp.getAdvancements().award(_adv, criteria);
                        }
                    }
                }
            }
        }
        if (tool.hasTag(DreamtinkerTagKeys.Items.dt_scythe)){
            rest = true;
            if (null != GAZE_OF_TERROR)
                target.addEffect(new MobEffectInstance(GAZE_OF_TERROR, 100, 0));
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
            attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 2, false, false));
            if (!(target instanceof LordPumpkinheadEntity) && !(target instanceof PumpkinheadEntity) && !(target instanceof SirPumpkinheadEntity) &&
                !(target instanceof SirPumpkinheadWithoutHorseEntity) && !(target instanceof SirTheHeadlessEntity) && !(target instanceof FelsteedEntity) &&
                !(target instanceof LordPumpkinheadHeadEntity) && !(target instanceof LordPumpkinheadWithoutaHorseEntity) &&
                !(target instanceof LordTheHeadlessEntity) && !(target instanceof LordsFelsteedEntity) && target instanceof LivingEntity){
                if (SOUL_STRATIFICATION != null){
                    target.addEffect(new MobEffectInstance(SOUL_STRATIFICATION, 120 / modifier.getLevel(), 0));
                }
            }
        }
        if (tool.hasTag(DreamtinkerTagKeys.Items.dt_hammer)){
            rest = true;
            if (TinkerPredicate.AIRBORNE.matches(attacker) && !attacker.isInWater() && !attacker.isInLava()){
                if (null != STUN && !target.hasEffect(STUN) && !target.isBlocking()){
                    target.addEffect(new MobEffectInstance(STUN, 25, 0, false, false));
                    ((ServerLevel) level).playSound((Player) null, target.blockPosition(), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(
                            new ResourceLocation("born_in_chaos_v1:skeleton_trasher_attack")), SoundSource.NEUTRAL, 0.6F, 1.0F);
                    target.setDeltaMovement(
                            new Vec3(target.getDeltaMovement().x() * (double) 5.0F, (double) 0.0F, target.getDeltaMovement().z() * (double) 5.0F));

                }
            }

        }
        if (!rest && !tool.hasTag(Dreamtinker.mcItemTag("axes"))){
            if (null != INFERNAL_FLAME)
                target.addEffect(new MobEffectInstance(INFERNAL_FLAME, 4 * 20, 0));
        }
        return knockback;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            boolean rest = false;
            if (tool.hasTag(TinkerTags.Items.PARRY)){
                rest = true;
                tooltip.add(
                        Component.translatable("modifier.dreamtinker.tooltip.dagger")
                                 .withStyle(this.getDisplayName().getStyle()));
            }
            if (!tool.hasTag(TinkerTags.Items.BROAD_TOOLS) &&
                (tool.hasTag(TinkerTags.Items.SWORD) || tool.hasTag(Dreamtinker.mcItemTag("swords")))){//sharpened DarkMetal Sword
                rest = true;
                tooltip.add(
                        Component.translatable("modifier.dreamtinker.tooltip.bic_dark_blade_" + (0 < tool.getModifier(ModifierIds.smite).getLevel() ? 1 : 0))
                                 .withStyle(this.getDisplayName().getStyle()));
            }
            if (tool.hasTag(TinkerTags.Items.BROAD_TOOLS)){
                if (tool.hasTag(TinkerTags.Items.SWORD) || tool.hasTag(Dreamtinker.mcItemTag("swords"))){
                    rest = true;
                    tooltip.add(Component.translatable(
                                                 "modifier.dreamtinker.tooltip.broad_sword")
                                         .withStyle(this.getDisplayName().getStyle()));
                }
            }
            if (tool.hasTag(DreamtinkerTagKeys.Items.dt_scythe)){
                rest = true;
                tooltip.add(
                        Component.translatable("modifier.dreamtinker.tooltip.scythe")
                                 .withStyle(this.getDisplayName().getStyle()));
            }
            if (tool.hasTag(Dreamtinker.mcItemTag("axes"))){
                rest = true;
                tooltip.add(
                        Component.translatable("modifier.dreamtinker.tooltip.axe")
                                 .withStyle(this.getDisplayName().getStyle()));
            }
            if (tool.hasTag(DreamtinkerTagKeys.Items.dt_hammer)){
                rest = true;
                tooltip.add(
                        Component.translatable("modifier.dreamtinker.tooltip.hammer")
                                 .withStyle(this.getDisplayName().getStyle()));
            }
            if (!rest){
                tooltip.add(
                        Component.translatable("modifier.dreamtinker.tooltip.rest")
                                 .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }

    @Override
    public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
        Level level = target.level();
        if (level instanceof ServerLevel sl)
            if (tool.hasTag(TinkerTags.Items.PARRY)){
                if (InteractionHand.MAIN_HAND == hand && InteractionSource.RIGHT_CLICK == source && null != SACRIFICE && !player.hasEffect(SACRIFICE)){
                    if (target instanceof ControlledBabySkeletonEntity || target instanceof MrPumpkinControlledEntity ||
                        target instanceof ControlledSpiritualAssistantEntity || (target instanceof Animal && ((Animal) target).getHealth() < 15f)){
                        target.hurt(DreamtinkerDamageTypes.source(level.registryAccess(), DamageTypes.GENERIC, null, player), 100f);
                        player.addEffect(
                                new MobEffectInstance(MobEffects.REGENERATION, (target instanceof Animal && ((Animal) target).getHealth() < 15f) ? 1000 : 1500,
                                                      1));
                        player.addEffect(
                                new MobEffectInstance(MobEffects.DAMAGE_BOOST, (target instanceof Animal && ((Animal) target).getHealth() < 15f) ? 1000 : 1500,
                                                      1));
                        player.addEffect(new MobEffectInstance(SACRIFICE, 30, 0, false, false));
                        sl.sendParticles((SimpleParticleType) BornInChaosV1ModParticleTypes.RITUAL.get(), target.getX() + (double) 0.5F,
                                         target.getY() + (double) 0.5F, target.getZ() + (double) 0.5F, 15, 0.3, 0.3, 0.3, 0.3);
                        sl.sendParticles((SimpleParticleType) BornInChaosV1ModParticleTypes.SWAP.get(), target.getX() + (double) 0.5F,
                                         target.getY() + (double) 0.5F, target.getZ() + (double) 0.5F, 1, 0.1, 0.1, 0.1, 0.1);
                        sl.playSound((Player) null, target.blockPosition(),
                                     (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.damage")), SoundSource.NEUTRAL,
                                     1.0F, 1.0F);
                        player.swing(InteractionHand.MAIN_HAND, true);
                        ToolDamageUtil.damage(tool, 1, player, null);
                        if (tool.isBroken())
                            tool.setDamage(0);
                        player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), 30);
                    }
                }
            }
        return InteractionResult.PASS;
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        if (context.hasTag(TinkerTags.Items.BROAD_TOOLS)){
            ToolStats.ATTACK_DAMAGE.multiply(builder, 1 + 0.15f * modifier.getLevel());
        }
        if (context.hasTag(DreamtinkerTagKeys.Items.dt_scythe)){
            ToolStats.ATTACK_DAMAGE.multiply(builder, 1 + 0.15f * modifier.getLevel());
        }
    }


    private static boolean lacksEffect(LivingEntity e, @org.jetbrains.annotations.Nullable MobEffect eff) {
        return eff == null || !e.hasEffect(eff);
    }

    public void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled())
            return;
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide)
            return;
        Entity killer = event.getSource().getEntity();
        if (killer instanceof LivingEntity attacker){
            if (0 < DTModifierCheck.getMainhandModifierLevel(attacker, DreamtinkerModifiers.bic_dark_blade.getId()) &&
                attacker.getMainHandItem().is(Dreamtinker.mcItemTag("axes"))){
                if (null != RAMPANT_RAMPAGE && attacker.hasEffect(RAMPANT_RAMPAGE)){
                    attacker.addEffect(new MobEffectInstance(RAMPANT_RAMPAGE, 9 * 20, 0));
                    attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 0));
                }else if (null != FURIOUS_RAMPAGE && null != RAMPANT_RAMPAGE && attacker.hasEffect(FURIOUS_RAMPAGE))
                    attacker.addEffect(new MobEffectInstance(RAMPANT_RAMPAGE, 4 * 20, 0));
                else if (null != STRONG_RAMPAGE && null != FURIOUS_RAMPAGE && attacker.hasEffect(STRONG_RAMPAGE))
                    attacker.addEffect(new MobEffectInstance(FURIOUS_RAMPAGE, 4 * 20, 0));
                else if (null != MEDIUM_RAMPAGE && null != STRONG_RAMPAGE && attacker.hasEffect(MEDIUM_RAMPAGE))
                    attacker.addEffect(new MobEffectInstance(STRONG_RAMPAGE, 4 * 20, 0));
                else if (null != LIGHT_RAMPAGE && null != MEDIUM_RAMPAGE && attacker.hasEffect(LIGHT_RAMPAGE))
                    attacker.addEffect(new MobEffectInstance(MEDIUM_RAMPAGE, 4 * 20, 0));
                else if (null != LIGHT_RAMPAGE && !attacker.hasEffect(LIGHT_RAMPAGE))
                    attacker.addEffect(new MobEffectInstance(LIGHT_RAMPAGE, 4 * 20, 0));
            }
        }
    }
}
