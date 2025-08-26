package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerEffect;
import org.dreamtinker.dreamtinker.register.DreamtinkerFluid;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider;
import slimeknights.tconstruct.library.json.predicate.HarvestTierPredicate;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;
import slimeknights.tconstruct.library.modifiers.fluid.block.BreakBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.MobEffectCloudFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.PlaceBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ScalingFluidEffect;
import slimeknights.tconstruct.shared.TinkerEffects;

public class DreamtinkerFluidEffectProvider extends AbstractFluidEffectProvider {
    public DreamtinkerFluidEffectProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    protected void addFluids() {
        addFluid(DreamtinkerFluid.molten_echo_shard, 50)
                .addDamage(2.5f, new DamageFluidEffect.DamageTypePair(TinkerDamageTypes.BLEEDING, TinkerDamageTypes.BLEEDING))
                .addEntityEffects(FluidMobEffect.builder().effect(DreamtinkerEffect.RealDarkness.get(), 100, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DARKNESS, 100, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(DreamtinkerEffect.RealDarkness.get(), 100, 1).buildCloud().effects()))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.DARKNESS, 100, 1).buildCloud().effects()));

        addFluid(DreamtinkerFluid.molten_nigrescence_antimony, 50)
                .spikeDamage(6.0f);

        addFluid(DreamtinkerFluid.molten_albedo_stibium, 50)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.NIGHT_VISION, 100, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.NIGHT_VISION, 100, 1).buildCloud().effects()));

        addFluid(DreamtinkerFluid.molten_lupi_antimony, 100)
                .addDamage(1.0f, new DamageFluidEffect.DamageTypePair(TinkerDamageTypes.SELF_DESTRUCT, TinkerDamageTypes.SELF_DESTRUCT))
                .addEntityEffects(FluidMobEffect.builder().effect(TinkerEffects.selfDestructing.get(), 100, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(TinkerEffects.selfDestructing.get(), 100, 1).buildCloud().effects()));

        addFluid(DreamtinkerFluid.molten_ascending_antimony, 50)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 100, 3).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 100, 3).buildCloud().effects())).
                addBlockEffect(
                        new HarvestTierPredicate(Tiers.NETHERITE), new BreakBlockFluidEffect(50, Enchantments.BLOCK_FORTUNE, 3));

        addFluid(DreamtinkerFluid.liquid_smoky_antimony, 50)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.HEAL, 100, 2).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.REGENERATION, 100, 2).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.HEAL, 100, 2).buildCloud().effects()))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.REGENERATION, 100, 2).buildCloud().effects()));

        addGlass(DreamtinkerFluid.molten_crying_obsidian)
                .spikeDamage(4.0f)
                .addBlockEffect(ScalingFluidEffect.blocks()
                                                  .effect(4, new PlaceBlockFluidEffect(Blocks.CRYING_OBSIDIAN))
                                                  .build());

    }

    @Override
    public String getName() {
        return "Dreamtinker Fluid Effect Provider.";
    }
}
