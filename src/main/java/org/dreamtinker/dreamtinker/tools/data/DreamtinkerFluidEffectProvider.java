package org.dreamtinker.dreamtinker.tools.data;

import com.sammy.malum.registry.common.MobEffectRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider;
import slimeknights.tconstruct.library.json.predicate.HarvestTierPredicate;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;
import slimeknights.tconstruct.library.modifiers.fluid.block.BreakBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.MobEffectCloudFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.PlaceBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ScalingFluidEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.TinkerEffects;

public class DreamtinkerFluidEffectProvider extends AbstractFluidEffectProvider {
    public DreamtinkerFluidEffectProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    protected void addFluids() {
        addFluid(DreamtinkerFluids.molten_echo_shard, 50)
                .addDamage(2.5f, new DamageFluidEffect.DamageTypePair(TinkerDamageTypes.BLEEDING, TinkerDamageTypes.BLEEDING))
                .addEntityEffects(FluidMobEffect.builder().effect(DreamtinkerEffects.RealDarkness.get(), 100, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DARKNESS, 100, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(DreamtinkerEffects.RealDarkness.get(), 100, 1).buildCloud().effects()))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.DARKNESS, 100, 1).buildCloud().effects()));

        addFluid(DreamtinkerFluids.molten_nigrescence_antimony, 50)
                .spikeDamage(6.0f);

        addFluid(DreamtinkerFluids.molten_albedo_stibium, 50)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.NIGHT_VISION, 100, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.NIGHT_VISION, 100, 1).buildCloud().effects()));

        addFluid(DreamtinkerFluids.molten_lupi_antimony, 100)
                .addDamage(1.0f, new DamageFluidEffect.DamageTypePair(TinkerDamageTypes.SELF_DESTRUCT, TinkerDamageTypes.SELF_DESTRUCT))
                .addEntityEffects(FluidMobEffect.builder().effect(TinkerEffects.selfDestructing.get(), 100, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(TinkerEffects.selfDestructing.get(), 100, 1).buildCloud().effects()));

        addFluid(DreamtinkerFluids.molten_ascending_antimony, 50)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 100, 3).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 100, 3).buildCloud().effects())).
                addBlockEffect(
                        new HarvestTierPredicate(Tiers.NETHERITE), new BreakBlockFluidEffect(50, Enchantments.BLOCK_FORTUNE, 3));

        addFluid(DreamtinkerFluids.liquid_smoky_antimony, 50)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.HEAL, 100, 2).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.REGENERATION, 100, 2).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.HEAL, 100, 2).buildCloud().effects()))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.REGENERATION, 100, 2).buildCloud().effects()));

        addGlass(DreamtinkerFluids.molten_crying_obsidian)
                .spikeDamage(3.0f)
                .addBlockEffect(ScalingFluidEffect.blocks()
                                                  .effect(1, new PlaceBlockFluidEffect(DreamtinkerCommon.crying_obsidian_plane.get()))
                                                  .effect(4, new PlaceBlockFluidEffect(Blocks.CRYING_OBSIDIAN))
                                                  .build());
        addMetal(DreamtinkerFluids.unstable_liquid_aether)
                .addDamage(8.0f, new DamageFluidEffect.DamageTypePair(DamageTypes.EXPLOSION, DamageTypes.EXPLOSION));
        addSlime(DreamtinkerFluids.molten_void)
                .addDamage(4.0f, new DamageFluidEffect.DamageTypePair(DamageTypes.FELL_OUT_OF_WORLD, DamageTypes.FELL_OUT_OF_WORLD));
        addFluid(DreamtinkerFluids.unholy_water, FluidValues.SIP)
                .addCondition(modLoaded("enigmaticlegacy"))
                .addEntityEffects(FluidMobEffect.builder().effect(DreamtinkerEffects.unholy.get(), 100, 2).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(DreamtinkerEffects.unholy.get(), 100, 2).buildCloud().effects()));
        addGem(DreamtinkerFluids.molten_nefariousness)
                .addCondition(modLoaded("enigmaticlegacy"))
                .magicDamage(2)
                .addEntityEffects(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 100, 2).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.WITHER, 100, 2).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.WITHER, 100, 2).buildCloud().effects()))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 100, 2).buildCloud().effects()));
        addMetal(DreamtinkerFluids.molten_evil)
                .addCondition(modLoaded("enigmaticlegacy"))
                .magicDamage(4)
                .addEntityEffects(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 100, 3).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.WITHER, 100, 3).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.WITHER, 100, 3).buildCloud().effects()))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 100, 3).buildCloud().effects()));

        addFluid(DreamtinkerFluids.blood_soul, 20)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.HEAL, 100, 1).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.liquid_concentrated_gluttony, FluidValues.SIP)
                .addCondition(modLoaded("malum"))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffectRegistry.GLUTTONY.get(), 10 * 20, 4).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffectRegistry.GLUTTONY.get(), 10 * 20, 4).buildCloud().effects()));
        addFluid(DreamtinkerFluids.liquid_arcana_juice, FluidValues.SIP)
                .addCondition(modLoaded("malum"))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffectRegistry.ECHOING_ARCANA.get(), 10 * 20, 3).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(
                                FluidMobEffect.builder().effect(MobEffectRegistry.ECHOING_ARCANA.get(), 10 * 20, 3).buildCloud().effects()));
        addFluid(DreamtinkerFluids.mercury, FluidValues.SIP)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_RESISTANCE, 100, 2).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 100, 4).buildEntity(TimeAction.ADD));
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Fluid Effect Provider.";
    }

    public static ICondition modLoaded(String modId) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new ModLoadedCondition(modId));
    }
}
