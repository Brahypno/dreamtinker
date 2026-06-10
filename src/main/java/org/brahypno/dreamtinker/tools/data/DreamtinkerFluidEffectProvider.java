package org.brahypno.dreamtinker.tools.data;

import com.sammy.malum.registry.common.MobEffectRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
import org.brahypno.dreamtinker.library.modifiers.fluid.block.AutoTagCycleBlockFluidEffect;
import org.brahypno.dreamtinker.library.modifiers.fluid.entity.ConditionalDamageFluidEffect;
import org.brahypno.dreamtinker.library.modifiers.fluid.entity.DespairScalingDamageFluidEffect;
import org.brahypno.dreamtinker.library.modifiers.fluid.entity.DrainLifeFluidEffect;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.entity.HasMobEffectPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.entity.MobTypePredicate;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.predicate.HarvestTierPredicate;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;
import slimeknights.tconstruct.library.modifiers.fluid.block.*;
import slimeknights.tconstruct.library.modifiers.fluid.entity.*;
import slimeknights.tconstruct.library.modifiers.fluid.general.ScalingFluidEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.TinkerEffects;

public class DreamtinkerFluidEffectProvider extends AbstractFluidEffectProvider {
    public DreamtinkerFluidEffectProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    protected void addFluids() {
        addGem(DreamtinkerFluids.molten_echo_shard)
                .addDamage(2.5f, new DamageFluidEffect.DamageTypePair(TinkerDamageTypes.BLEEDING, TinkerDamageTypes.BLEEDING))
                .addEntityEffects(FluidMobEffect.builder().effect(DreamtinkerEffects.RealDarkness.get(), 100, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DARKNESS, 100, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(DreamtinkerEffects.RealDarkness.get(), 100, 1).buildCloud().effects()))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffects.DARKNESS, 100, 1).buildCloud().effects()));
        addGem(DreamtinkerFluids.molten_echo_alloy)
                .addDamage(3.5f, new DamageFluidEffect.DamageTypePair(TinkerDamageTypes.BLEEDING, TinkerDamageTypes.BLEEDING))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.GLOWING, 120, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 120, 2).buildEntity(TimeAction.ADD))
                .addBlockEffect(new HarvestTierPredicate(Tiers.DIAMOND), new BreakBlockFluidEffect(50, Enchantments.SILK_TOUCH, 1));

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
                        new HarvestTierPredicate(Tiers.NETHERITE), new BreakBlockFluidEffect(50, Enchantments.BLOCK_FORTUNE, 5));

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
        addFluid(DreamtinkerFluids.liquid_trist, FluidValues.SIP)
                .magicDamage(1)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 120, 2).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DIG_SLOWDOWN, 120, 2).buildEntity(TimeAction.ADD))
                .addBlockEffect(new HarvestTierPredicate(Tiers.STONE), new BreakBlockFluidEffect(15));
        addFluid(DreamtinkerFluids.liquid_pure_soul, FluidValues.SIP)
                .addEntityEffect(new CureEffectsFluidEffect(new ItemStack(Items.MILK_BUCKET)))
                .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.WITHER))
                .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.POISON))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.REGENERATION, 100, 4).buildEntity(TimeAction.ADD))
                .addBlockEffect(new MobEffectCloudFluidEffect(
                        FluidMobEffect.builder().effect(MobEffects.REGENERATION, 100, 4).buildCloud().effects()
                ));
        addMetal(DreamtinkerFluids.unstable_liquid_aether)
                .addDamage(8.0f, new DamageFluidEffect.DamageTypePair(DamageTypes.EXPLOSION, DamageTypes.EXPLOSION));
        addMetal(DreamtinkerFluids.molten_soul_aether)
                .addCondition(modLoaded("enigmaticlegacy"))
                .magicDamage(10)
                .addEntityEffect(new PushEntityFluidEffect(5, 5))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.SLOW_FALLING, 140, 3).buildEntity(TimeAction.ADD))
                .addBlockEffect(MoveBlocksFluidEffect.push(SoundEvents.SOUL_ESCAPE));
        addSlime(DreamtinkerFluids.molten_void)
                .addDamage(4.0f, new DamageFluidEffect.DamageTypePair(DamageTypes.FELL_OUT_OF_WORLD, DamageTypes.FELL_OUT_OF_WORLD));
        addSlime(DreamtinkerFluids.reversed_shadow)
                .magicDamage(6)
                .addEntityEffect(new PushEntityFluidEffect(2, 2))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.CONFUSION, 120, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(MoveBlocksFluidEffect.pull(SoundEvents.SOUL_SAND_PLACE));
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
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.HEAL, 100, 2).buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_soul_stained_steel)
                .addCondition(modLoaded("malum"))
                .magicDamage(2)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffectRegistry.ECHOING_ARCANA.get(), 120, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_RESISTANCE, 120, 1).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.liquid_concentrated_gluttony, FluidValues.SIP)
                .addCondition(modLoaded("malum"))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffectRegistry.GLUTTONY.get(), 10 * 20, 4).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(MobEffectRegistry.GLUTTONY.get(), 10 * 20, 4).buildCloud().effects()));
        addMetal(DreamtinkerFluids.molten_malignant_pewter)
                .addCondition(modLoaded("malum"))
                .spikeDamage(5)
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffectRegistry.IMMINENT_DELIVERANCE.get(), 160, 3)
                                                .buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffectRegistry.WICKED_INTENT.get(), 120, 1)
                                                .buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffectRegistry.SILENCED.get(), 160, 1)
                                                .buildEntity(TimeAction.ADD));

        addMetal(DreamtinkerFluids.molten_malignant_gluttony)
                .addCondition(modLoaded("malum"))
                .magicDamage(3)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffectRegistry.GLUTTONY.get(), 160, 3).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(DreamtinkerEffects.thirsty.get(), 160, 3).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.HUNGER, 160, 2).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.mercury, FluidValues.SIP)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_RESISTANCE, 100, 2).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 100, 4).buildEntity(TimeAction.ADD));
        addGem(DreamtinkerFluids.liquid_amber)
                .addBlockEffect(new PlaceBlockFluidEffect(Blocks.HONEY_BLOCK))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 180, 4).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DIG_SLOWDOWN, 180, 3).buildEntity(TimeAction.ADD));
        addGem(DreamtinkerFluids.molten_desire)
                .magicDamage(1)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SPEED, 120, 4).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 120, 3).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.CONFUSION, 120, 2).buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_soul_steel)
                .magicDamage(2)
                .addEntityEffect(new DrainLifeFluidEffect(2.0f, 0.5f, new DamageFluidEffect.DamageTypePair(DamageTypes.MAGIC, DamageTypes.MAGIC)))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_RESISTANCE, 100, 2).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.half_festering_blood, FluidValues.BOTTLE / 5)
                .addEntityEffect(new ConditionalDamageFluidEffect(
                        new MobTypePredicate(MobType.UNDEAD),
                        3.0f, // 对亡灵：魔法/腐败冲击
                        1.5f, // 对非亡灵：基础腐血伤害
                        new DamageFluidEffect.DamageTypePair(DamageTypes.MAGIC, DamageTypes.MAGIC)
                ))
                .addEntityEffects(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 100, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.POISON, 80, 2).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.festering_blood, FluidValues.BOTTLE / 5)
                .addDamage(1.0f, new DamageFluidEffect.DamageTypePair(TinkerDamageTypes.BLEEDING, TinkerDamageTypes.BLEEDING))
                .addEntityEffects(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 100, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.POISON, 80, 2).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.rainbow_honey, FluidValues.BOTTLE / 5)
                .addEntityEffect(new RestoreHungerFluidEffect(15, 10, true, ItemOutput.fromItem(DreamtinkerCommon.rainbow_honey.get())))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.ABSORPTION, 120, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.REGENERATION, 80, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 80, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(new PlaceBlockFluidEffect(Blocks.HONEY_BLOCK));
        addGem(DreamtinkerFluids.molten_bee_gem)
                .spikeDamage(2)
                .addBlockEffect(new HarvestTierPredicate(Tiers.STONE), new BreakBlockFluidEffect(25, Enchantments.BLOCK_FORTUNE, 1))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.POISON, 100, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.REGENERATION, 60, 1).buildEntity(TimeAction.ADD));
        addGem(DreamtinkerFluids.molten_black_sapphire)
                .spikeDamage(3)
                .addBlockEffect(new HarvestTierPredicate(Tiers.DIAMOND), new BreakBlockFluidEffect(65, Enchantments.SILK_TOUCH, 1))
                .addEntityEffect(new FreezeFluidEffect(TimeAction.ADD, 30));
        addGem(DreamtinkerFluids.molten_scolecite)
                .magicDamage(5)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.SLOW_FALLING, 160, 2).buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_cold_iron)
                .addEntityEffect(new ConditionalDamageFluidEffect(
                        LivingEntityPredicate.WATER_SENSITIVE,
                        5.0f,
                        2.0f,
                        new DamageFluidEffect.DamageTypePair(DamageTypes.FREEZE, DamageTypes.FREEZE)
                ))
                .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.DAMAGE_BOOST))
                .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.REGENERATION))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.WEAKNESS, 140, 2).buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_orichalcum)
                .addEntityEffect(new ConditionalDamageFluidEffect(
                        new HasMobEffectPredicate(MobEffects.LEVITATION),
                        5.0f,
                        3.0f,
                        new DamageFluidEffect.DamageTypePair(DamageTypes.MAGIC, DamageTypes.MAGIC)
                ))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(TinkerEffects.antigravity.get(), 120, 1)
                                                .buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffects.MOVEMENT_SLOWDOWN, 100, 1)
                                                .buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_transmutation_gold)
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffects.LUCK, 200, 3)
                                                .buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffects.GLOWING, 120, 1)
                                                .buildEntity(TimeAction.ADD))
                .addBlockEffect(new AutoTagCycleBlockFluidEffect(
                        AutoTagCycleBlockFluidEffect.defaultIgnoredTags(),
                        AutoTagCycleBlockFluidEffect.Mode.RANDOM,
                        true,
                        0.35f,
                        2,
                        64
                ));
        compatMetal(DreamtinkerFluids.molten_shadow_silver, "silver")
                .magicDamage(0.5f)
                .addEntityEffect(new RandomTeleportFluidEffect(LevelingInt.eachLevel(3), LevelingInt.eachLevel(1)))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.INVISIBILITY, 100, 3).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SPEED, 100, 1).buildEntity(TimeAction.ADD));
        compatMetal(DreamtinkerFluids.molten_arcane_gold, "molten_arcane_gold")
                .magicDamage(2)
                .addBlockEffect(BlockInteractFluidEffect.INSTANCE)
                .addBlockEffect(new HarvestTierPredicate(Tiers.DIAMOND), new BreakBlockFluidEffect(50, Enchantments.BLOCK_FORTUNE, 1))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.GLOWING, 120, 1).buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_dark_metal)
                .addCondition(modLoaded("born_in_chaos_v1"))
                .magicDamage(3)
                .addEntityEffect(new FireFluidEffect(TimeAction.ADD, 5))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.WITHER, 120, 1).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.molten_ender_ash, FluidValues.BRICK)
                .addDamage(2.0f, new DamageFluidEffect.DamageTypePair(DamageTypes.FELL_OUT_OF_WORLD, DamageTypes.FELL_OUT_OF_WORLD))
                .addEntityEffect(new RandomTeleportFluidEffect(LevelingInt.eachLevel(6), LevelingInt.eachLevel(5)))
                .addBlockEffect(BlockInteractFluidEffect.INSTANCE);
        addMetal(DreamtinkerFluids.molten_utherium)
                .addCondition(modLoaded("undergarden"))
                .magicDamage(3)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 100, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.CONFUSION, 100, 2).buildEntity(TimeAction.ADD))
                .addBlockEffect(new HarvestTierPredicate(Tiers.DIAMOND), new BreakBlockFluidEffect(55));
        addMetal(DreamtinkerFluids.molten_cloggrum)
                .addCondition(modLoaded("undergarden"))
                .impactDamage(3)
                .addBlockEffect(MoveBlocksFluidEffect.push(SoundEvents.LEVER_CLICK))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DIG_SLOWDOWN, 120, 1).buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_froststeel)
                .addCondition(modLoaded("undergarden"))
                .addDamage(2.5f, new DamageFluidEffect.DamageTypePair(DamageTypes.FREEZE, DamageTypes.FREEZE))
                .addEntityEffect(new FreezeFluidEffect(TimeAction.ADD, 40))
                .addBlockEffect(new PlaceBlockFluidEffect(Blocks.FROSTED_ICE))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 120, 2).buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_iesnium)
                .addCondition(modLoaded("occultism1"))
                .addEntityEffect(new DrainLifeFluidEffect(2.5f, 0.4f, new DamageFluidEffect.DamageTypePair(DamageTypes.MAGIC, DamageTypes.MAGIC)))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.WITHER, 120, 1).buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_regalium)
                .addCondition(modLoaded("undergarden"))
                .impactDamage(2)
                .addBlockEffect(MoveBlocksFluidEffect.push(SoundEvents.ALLAY_HURT))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_RESISTANCE, 120, 1).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 120, 1).buildEntity(TimeAction.ADD))
                .addBlockEffect(
                        new HarvestTierPredicate(Tiers.NETHERITE), new BreakBlockFluidEffect(50, Enchantments.BLOCK_FORTUNE, 5));
        addMetal(DreamtinkerFluids.molten_forgotten_metal)
                .addCondition(modLoaded("undergarden"))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(DreamtinkerEffects.cursed.get(), 160, 1)
                                                .buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffects.WEAKNESS, 160, 4)
                                                .buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffects.MOVEMENT_SLOWDOWN, 120, 20)
                                                .buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_iron_heart)
                .impactDamage(3)
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.DAMAGE_RESISTANCE, 160, 2).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 100, 1).buildEntity(TimeAction.ADD));
        compatMetal(DreamtinkerFluids.molten_atonement_silver, "silver")
                .addEntityEffect(new ConditionalDamageFluidEffect(new MobTypePredicate(MobType.UNDEAD), 5.0f, 2.0f,
                                                                  new DamageFluidEffect.DamageTypePair(DamageTypes.MAGIC, DamageTypes.MAGIC)))
                .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.WITHER))
                .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.POISON))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.GLOWING, 160, 1).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.snake_essence, FluidValues.SIP)
                .addEntityEffect(new ConditionalDamageFluidEffect(
                        new HasMobEffectPredicate(MobEffects.POISON),
                        30.0f,
                        10.0f,
                        new DamageFluidEffect.DamageTypePair(DamageTypes.MAGIC, DamageTypes.MAGIC)
                ))
                .addEntityEffect(new AddBreathFluidEffect(-300))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.POISON, 160, 2).buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SPEED, 100, 2).buildEntity(TimeAction.ADD))
                .addBlockEffect(new MobEffectCloudFluidEffect(
                        FluidMobEffect.builder().effect(MobEffects.POISON, 160, 5).buildCloud().effects()
                ));
        addFluid(DreamtinkerFluids.unmelting_teardrop, FluidValues.SIP)
                .addDamage(2.0f, new DamageFluidEffect.DamageTypePair(DamageTypes.FREEZE, DamageTypes.FREEZE))
                .addEntityEffect(new FreezeFluidEffect(TimeAction.ADD, 200))
                .addEntityEffect(FluidEffect.EXTINGUISH_FIRE)
                .addBlockEffect(new PlaceBlockFluidEffect(Blocks.FROSTED_ICE))
                .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 120, 2).buildEntity(TimeAction.ADD));
        addFluid(DreamtinkerFluids.despair_essence, FluidValues.SIP)
                .addEntityEffect(new DespairScalingDamageFluidEffect(
                        20.0f,
                        1.5f,
                        3f,
                        100.0f,
                        new DamageFluidEffect.DamageTypePair(DreamtinkerDamageTypes.NULL_VOID, DreamtinkerDamageTypes.NULL_VOID)
                ))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffects.WEAKNESS, 180, 2)
                                                .buildEntity(TimeAction.ADD))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(MobEffects.MOVEMENT_SLOWDOWN, 180, 2)
                                                .buildEntity(TimeAction.ADD));
        addMetal(DreamtinkerFluids.molten_enderitium)
                .addCondition(modLoaded("legendary_monsters"))
                .magicDamage(3);
        /*
        compatFluid("undergarden", FluidTags.create(new ResourceLocation("undergarden", "virulent")), FluidValues.SIP)
                .addCondition(modLoaded("undergarden"))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("undergarden", "virulence")), 10 * 20, 1)
                                                .buildEntity(TimeAction.ADD))
                .addBlockEffect(new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(ForgeRegistries.MOB_EFFECTS.getValue(
                        new ResourceLocation("undergarden", "virulence")), 10 * 20, 3).buildCloud().effects()));
        compatFluid("undergarden", DreamtinkerFluids.gooey_slime.getTag(), FluidValues.SIP)
                .addCondition(modLoaded("undergarden"))
                .addEntityEffects(FluidMobEffect.builder()
                                                .effect(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("undergarden", "gooey")), 10 * 20, 1)
                                                .buildEntity(TimeAction.ADD))
                .addBlockEffect(new MobEffectCloudFluidEffect(FluidMobEffect.builder().effect(ForgeRegistries.MOB_EFFECTS.getValue(
                        new ResourceLocation("undergarden", "gooey")), 10 * 20, 3).buildCloud().effects()));

         */
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Fluid Effect Provider.";
    }

    public static ICondition modLoaded(String modId) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new ModLoadedCondition(modId));
    }
}
