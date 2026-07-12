package org.brahypno.dreamtinker.tools.data;

import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.sammy.malum.registry.common.AttributeRegistry;
import com.sammy.malum.registry.common.item.EnchantmentRegistry;
import elucent.eidolon.registries.EidolonAttributes;
import elucent.eidolon.registries.EidolonPotions;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidType;
import org.brahypno.dreamtinker.Entity.DreamtinkerEntityTypes;
import org.brahypno.dreamtinker.common.DreamtinkerAttributes;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.library.modifiers.modules.harvest.AutoPureDaisyModule;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.brahypno.esotericismtinker.library.modifiers.modules.armor.FlightModule;
import org.brahypno.esotericismtinker.library.modifiers.modules.armor.RepriseProtectionModule;
import org.brahypno.esotericismtinker.library.modifiers.modules.armor.ResonanceArmorModule;
import org.brahypno.esotericismtinker.library.modifiers.modules.build.AllSlotModule;
import org.brahypno.esotericismtinker.library.modifiers.modules.combat.*;
import org.brahypno.esotericismtinker.library.modifiers.modules.harvest.BlockLootMultiplierModule;
import org.brahypno.esotericismtinker.library.modifiers.modules.harvest.EntityLootMultiplierModule;
import org.brahypno.esotericismtinker.library.modifiers.modules.weapon.SelfDestroyModule;
import org.brahypno.esotericismtinker.library.modifiers.modules.weapon.SwappableCircleWeaponAttack;
import org.brahypno.esotericismtinker.library.tools.EsotericismSlotType;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.block.BlockPropertiesPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.damage.DamageTypePredicate;
import slimeknights.mantle.data.predicate.entity.HasMobEffectPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.entity.MobTypePredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.variable.block.BlockVariable;
import slimeknights.tconstruct.library.json.variable.entity.AttributeEntityVariable;
import slimeknights.tconstruct.library.json.variable.entity.ConditionalEntityVariable;
import slimeknights.tconstruct.library.json.variable.entity.EntityLightVariable;
import slimeknights.tconstruct.library.json.variable.melee.EntityMeleeVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockLightVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockMiningSpeedVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockTemperatureVariable;
import slimeknights.tconstruct.library.json.variable.power.EntityPowerVariable;
import slimeknights.tconstruct.library.json.variable.protection.EntityProtectionVariable;
import slimeknights.tconstruct.library.json.variable.stat.EntityConditionalStatVariable;
import slimeknights.tconstruct.library.json.variable.tool.ToolStatVariable;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.*;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.MaterialRepairModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.build.*;
import slimeknights.tconstruct.library.modifiers.modules.combat.*;
import slimeknights.tconstruct.library.modifiers.modules.display.DurabilityBarColorModule;
import slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.MeltingModule;
import slimeknights.tconstruct.tools.modules.armor.DepthProtectionModule;
import slimeknights.tconstruct.tools.modules.combat.FreezingAttackModule;
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry;

import java.util.List;

import static net.minecraft.tags.DamageTypeTags.BYPASSES_ENCHANTMENTS;
import static org.brahypno.dreamtinker.library.compat.ars_nouveau.NovaRegistry.nova_magic_armor;
import static org.brahypno.dreamtinker.tools.DreamtinkerModifiers.*;
import static org.brahypno.esotericismtinker.tools.EsotericismTinkerModifiers.HORIZONTAL_LOOK_X;
import static org.brahypno.esotericismtinker.tools.EsotericismTinkerModifiers.HORIZONTAL_LOOK_Z;
import static slimeknights.tconstruct.common.TinkerTags.Items.*;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.*;
import static slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial.ARMOR_SLOTS;

public class DreamtinkerModifierProvider extends AbstractModifierProvider implements IConditionBuilder {
    public DreamtinkerModifierProvider(PackOutput packOutput) {
        super(packOutput);
    }

    private static ConditionalStatModule lightEmanation(INumericToolStat<?> stat) {
        return ConditionalStatModule.stat(stat)
                                    .customVariable("block_light",
                                                    new EntityConditionalStatVariable(new EntityLightVariable(LightLayer.BLOCK), 7.0f))
                                    .formula()
                                    .variable(VALUE)
                                    .variable(LEVEL)
                                    .constant(1.0f)
                                    .customVariable("block_light")
                                    .constant(7.0f).subtract()
                                    .constant(0.03125f).multiply()
                                    .add()
                                    .multiply()
                                    .add()
                                    .build();
    }

    private void addELModifiers() {
        // Moved to src/main/resources so data generation does not directly require Enigmatic Legacy classes.
        //        buildModifier(Ids.el_by_pass_worthy, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
        //                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        //        buildModifier(Ids.el_nemesis_curse, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
        //                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
        //                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.NEMESIS).level(1).constant());
        //        buildModifier(Ids.el_sorrow, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
        //                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
        //                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.SORROW).level(1).constant());
        //        buildModifier(Ids.el_eternal_binding, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
        //                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
        //                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.ETERNAL_BINDING).level(1).constant());
        //        buildModifier(Ids.el_etherium, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
        //                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
        //                .addModule(SetStatModule.set(ToolStats.HARVEST_TIER).value(EnigmaticMaterials.ETHERIUM));
        //
        //        buildModifier(Ids.blighted_sigil, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
        //                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
        //                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
        //                .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).eachLevel(1));
    }

    private static final float UNDERPLATE_MAX = 10.0f;

    private void addEidolonModifiers() {
        buildModifier(Ids.eidolon_vulnerable, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(MobEffectModule.builder(EidolonPotions.VULNERABLE_EFFECT.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 3, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.eidolon_warlock, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(AttributeModule.builder(EidolonAttributes.MAGIC_POWER, AttributeModifier.Operation.MULTIPLY_BASE)
                                          .eachLevel(0.25f))
                .addModule(new EffectImmunityModule(MobEffects.MOVEMENT_SLOWDOWN))
                .addModule(ProtectionModule.builder()
                                           .sources(DamageSourcePredicate.ANY,
                                                    DamageSourcePredicate.or(DamageSourcePredicate.tag(Registry.FORGE_MAGIC),
                                                                             DamageSourcePredicate.tag(Registry.FORGE_WITHER)))
                                           .eachLevel(3.0f));
        buildModifier(Ids.eidolon_soul_hearts, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(AttributeModule.builder(EidolonAttributes.PERSISTENT_SOUL_HEARTS, AttributeModifier.Operation.ADDITION)
                                          .toolItem(ItemPredicate.tag(TinkerTags.Items.CHESTPLATES).inverted()).eachLevel(10f))
                .addModule(AttributeModule.builder(EidolonAttributes.PERSISTENT_SOUL_HEARTS, AttributeModifier.Operation.ADDITION)
                                          .toolItem(ItemPredicate.tag(TinkerTags.Items.CHESTPLATES)).eachLevel(20f))
                .addModule(new EffectImmunityModule(MobEffects.POISON))
                .addModule(new EffectImmunityModule(MobEffects.WITHER));
        buildModifier(Ids.eidolon_paladin_bone, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(MaterialRepairModule.material(DreamtinkerMaterialIds.PaladinBoneTool).constant(200))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.eidolon_bone_chill, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(MobEffectModule.builder(EidolonPotions.CHILLED_EFFECT.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 3, 10))

                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        buildModifier(Ids.ashen_soul, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));
    }

    private void addBICModifiers() {

        buildModifier(Ids.bic_dark_armor_plate, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new RarityModule(Rarity.RARE))
                .addModule(StatBoostModule.multiplyBase(ToolStats.DURABILITY).flat(0.3f))
                // armor
                .addModule(StatBoostModule.add(ToolStats.ARMOR_TOUGHNESS).flat(2))
                .addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).flat(0.05f))
                // melee harvest
                .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_DAMAGE).flat(0.25f))
                .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_SPEED).flat(-0.10f))
                .addModule(StatBoostModule.multiplyBase(ToolStats.MINING_SPEED).flat(-0.25f))
                .addModule(ModifierRequirementsModule.builder().requireModifier(ModifierIds.netherite, 1)
                                                     .modifierKey(Ids.bic_dark_armor_plate).build());
/*
        buildModifier(Ids.bic_frostbitten, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.BONE_CHILLING.get())
                                          .level(RandomLevelingValue.perLevel(0, 2))
                                          .time(RandomLevelingValue.perLevel(180, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_intoxicating, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.INTOXICATION.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(110, 20))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.POISON)
                                          .level(RandomLevelingValue.perLevel(0, 2))
                                          .time(RandomLevelingValue.perLevel(60, 20))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_life_stealer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.LIFESTEAL.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 20))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.STUN.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.WITHER)
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_krampus_horn, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.BONE_CHILLING.get())
                                          .level(RandomLevelingValue.flat(6))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_nightmare_claw, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MobEffectModule.builder(MobEffects.BLINDNESS)
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.DARKNESS)
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.WITHER)
                                          .level(RandomLevelingValue.flat(3))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.GAZE_OF_TERROR.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.MAGIC_DEPLETION.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);

        buildModifier(Ids.bic_infernal_ember, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.INFERNAL_FLAME.get())
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.perLevel(3 * 20, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_hound_fang, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.BONE_FRACTURE.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS)
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN)
                                          .level(RandomLevelingValue.flat(2))
                                          .time(RandomLevelingValue.perLevel(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
*/
        buildModifier(Ids.naughty_chaos, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));
    }

    private void addNovaModifiers() {
        buildModifier(Ids.nova_spell_tiers, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau")).levelDisplay(new ModifierLevelDisplay.UniqueForLevels(3));
        buildModifier(Ids.nova_creative_tiers, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau")).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.nova_spell_slots, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"));
        buildModifier(nova_magic_armor.getId(), not(DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.nova_abjuration_essence, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))
                .addModule(StatBoostModule.add(ToolStats.ARMOR_TOUGHNESS).eachLevel(1));
        buildModifier(Ids.nova_air_essence, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))
                .addModule(StatBoostModule.add(ToolStats.VELOCITY).eachLevel(0.02f))
                .addModule(StatBoostModule.add(ToolStats.DRAW_SPEED).eachLevel(0.02f));
        buildModifier(Ids.nova_earth_essence, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))
                .addModule(StatBoostModule.add(ToolStats.ARMOR).eachLevel(3));
        IJsonPredicate<LivingEntity> fire_blast =
                LivingEntityPredicate.or(new HasMobEffectPredicate(ModPotions.BLAST_EFFECT.get()), LivingEntityPredicate.ON_FIRE);
        buildModifier(Ids.nova_fire_essence, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))
                .addModule(ConditionalMeleeDamageModule.builder().target(fire_blast).eachLevel(2.5f))
                .addModule(ConditionalPowerModule.builder().target(fire_blast).eachLevel(0.1f))
                .addModule(MobEffectModule.builder(ModPotions.BLAST_EFFECT.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .chance(LevelingValue.eachLevel(0.3f))
                                          .target(LivingEntityPredicate.FIRE_IMMUNE.inverted())
                                          .build());
        buildModifier(Ids.nova_manipulation_essence, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))
                .addModule(ConditionalMeleeDamageModule.builder().target(new HasMobEffectPredicate(ModPotions.GRAVITY_EFFECT.get())).eachLevel(2.5f))
                .addModule(ConditionalPowerModule.builder().target(new HasMobEffectPredicate(ModPotions.GRAVITY_EFFECT.get())).eachLevel(0.1f))
                .addModule(MobEffectModule.builder(ModPotions.GRAVITY_EFFECT.get())
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .chance(LevelingValue.eachLevel(0.5f))
                                          .build());
        IJsonPredicate<LivingEntity> cold_snap =
                LivingEntityPredicate.or(new HasMobEffectPredicate(MobEffects.MOVEMENT_SLOWDOWN), LivingEntityPredicate.IS_FREEZING,
                                         LivingEntityPredicate.FEET_IN_WATER, LivingEntityPredicate.RAINING);
        buildModifier(Ids.nova_water_essence, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))
                .addModule(ConditionalMeleeDamageModule.builder().target(cold_snap).eachLevel(2.5f))
                .addModule(ConditionalPowerModule.builder().target(cold_snap).eachLevel(0.1f))
                .addModule(MobEffectModule.builder(ModPotions.SNARE_EFFECT.get())
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .chance(LevelingValue.eachLevel(0.3f))
                                          .build());

        buildModifier(Ids.nova_mana_reduce, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"));
        buildModifier(Ids.nova_ashen_resolve, not(DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau")))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        buildModifier(Ids.cosmogony_tetrad, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));

    }

    private void addUGModifiers() {
        buildModifier(Ids.undergarden_rot_killer, DreamtinkerMaterialDataProvider.modLoaded("undergarden"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().target(LivingEntityPredicate.tag(DreamtinkerTagKeys.EntityTypes.ROTSPAWN)).percent()
                                                       .formula()
                                                       .variable(LEVEL).constant(0.5f).multiply()
                                                       .constant(1f).add()
                                                       .variable(MULTIPLIER).multiply()
                                                       .variable(VALUE).multiply().build())
                .addModule(ConditionalPowerModule.builder().target(LivingEntityPredicate.tag(DreamtinkerTagKeys.EntityTypes.ROTSPAWN)).percent()
                                                 .formula()
                                                 .variable(LEVEL).constant(0.5f).multiply()
                                                 .constant(1f).add()
                                                 .variable(MULTIPLIER).multiply()
                                                 .variable(VALUE).multiply().build());

        buildModifier(Ids.undergarden_rot_protection, DreamtinkerMaterialDataProvider.modLoaded("undergarden"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ProtectionModule.builder().attacker(LivingEntityPredicate.tag(DreamtinkerTagKeys.EntityTypes.ROTSPAWN)).eachLevel(1.75f));

        buildModifier(Ids.undergarden_killer, DreamtinkerMaterialDataProvider.modLoaded("undergarden"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().target(LIVING_OF_UNDER_GARDEN).percent()
                                                       .formula()
                                                       .variable(LEVEL).constant(0.5f).multiply()
                                                       .constant(1f).add()
                                                       .variable(MULTIPLIER).multiply()
                                                       .variable(VALUE).multiply().build())
                .addModule(ConditionalPowerModule.builder().target(LIVING_OF_UNDER_GARDEN).percent()
                                                 .formula()
                                                 .variable(LEVEL).constant(0.5f).multiply()
                                                 .constant(1f).add()
                                                 .variable(MULTIPLIER).multiply()
                                                 .variable(VALUE).multiply().build());

        buildModifier(Ids.undergarden_protection, DreamtinkerMaterialDataProvider.modLoaded("undergarden"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ProtectionModule.builder().attacker(LIVING_OF_UNDER_GARDEN).eachLevel(1.75f));

        buildModifier(Ids.undergarden_miner, DreamtinkerMaterialDataProvider.modLoaded("undergarden"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMiningSpeedModule.builder().allowIneffective().blocks(BLOCK_OF_UNDER_GARDEN).percent()
                                                       .formula()
                                                       .variable(LEVEL).constant(0.5f).multiply()
                                                       .constant(1f).add()
                                                       .variable(MULTIPLIER).multiply()
                                                       .variable(VALUE).multiply().build());
    }

    private void addOCCModifiers() {
        buildModifier(Ids.otherworld_precious, DreamtinkerMaterialDataProvider.modLoaded("occultism1"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));
    }

    private void addBOTANIAModifiers() {
        buildModifier(Ids.botania_pure_smeltery, DreamtinkerMaterialDataProvider.modLoaded("botania"))
                .priority(110) // want to be higher than bonking and alike
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(InventoryMenuModule.SHIFT)
                .addModule(new AutoPureDaisyModule(20, InventoryModule.builder().slotsPerLevel(2)));

    }

    private void addLMModifiers() {
        IJsonPredicate<LivingEntity> ender = LivingEntityPredicate.tag(DreamtinkerTagKeys.EntityTypes.ENDER_ENTITY);
        buildModifier(Ids.not_end_er, DreamtinkerMaterialDataProvider.modLoaded("legendary_monsters"))
                .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).amount(-1f, -1f));
        buildModifier(Ids.ender_end, DreamtinkerMaterialDataProvider.modLoaded("legendary_monsters"))
                .addModule(ConditionalMeleeDamageModule.builder().target(ender).percent()
                                                       .formula()
                                                       .variable(LEVEL).constant(.2f).multiply()
                                                       .variable(MULTIPLIER).multiply()
                                                       .constant(1).add()
                                                       .variable(VALUE).multiply().build())
                .addModule(ConditionalPowerModule.builder().target(ender).percent()
                                                 .formula()
                                                 .variable(LEVEL).constant(.2f).multiply()
                                                 .variable(MULTIPLIER).multiply()
                                                 .constant(1).add()
                                                 .variable(VALUE).multiply().build())
                .addModule(KnockbackModule.builder().entity(ender)
                                          .formula()
                                          .variable(LEVEL).constant(0.2f).multiply()
                                          .constant(1).add()
                                          .variable(VALUE).multiply().build())
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20 * 2, 10))
                                          .target(ender).build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.ender_protection, DreamtinkerMaterialDataProvider.modLoaded("legendary_monsters"))
                .addModule(ProtectionModule.builder().attacker(ender).eachLevel(4f));
    }

    private static final float UNDERPLATE_ARMOR_FACTOR = 0.8f;

    private void addMalumModifiers() {
        buildModifier(Ids.malum_rebound, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.REBOUND.get()).level(1).constant())
                .addModule(ModifierRequirementsModule.builder().requireModifier(malum_base.getId(), 1)
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.malum_ascension, 1).inverted())
                                                     .modifierKey(Ids.malum_rebound).build());
        buildModifier(Ids.malum_ascension, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.ASCENSION.get()).level(1).constant())
                .addModule(ModifierRequirementsModule.builder().requireModifier(malum_base.getId(), 1)
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.malum_rebound, 1).inverted())
                                                     .modifierKey(Ids.malum_ascension).build());
        buildModifier(Ids.malum_animated, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.ANIMATED.get()).level(2).constant())
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.malum_haunted, 1).inverted())
                                                     .modifierKey(Ids.malum_animated).build());
        buildModifier(Ids.malum_haunted, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.HAUNTED.get()).level(2).constant())
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.malum_animated, 1).inverted())
                                                     .modifierKey(Ids.malum_haunted).build());
        buildModifier(Ids.malum_spirit_plunder, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(AttributeModule.builder(AttributeRegistry.SPIRIT_SPOILS, AttributeModifier.Operation.ADDITION).eachLevel(2.0f));

        buildModifier(Ids.malum_tyrving, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.malum_world_of_weight, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.malum_edge_of_deliverance, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.malum_sol_tiferet, not(DreamtinkerMaterialDataProvider.modLoaded("malum")))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        buildModifier(Ids.many_us, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));
        buildModifier(Ids.spiritual_weapon_transformation, not(DreamtinkerMaterialDataProvider.modLoaded("malum")))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(ModifierRequirementsModule.builder().requireModifier(weapon_transformation.getId(), 1)
                                                     .modifierKey(Ids.spiritual_weapon_transformation).build())
                .addModule(underplateAttribute(LodestoneAttributeRegistry.MAGIC_DAMAGE.get(), true, EquipmentSlot.CHEST))
                .addModule(underplateAttribute(LodestoneAttributeRegistry.MAGIC_RESISTANCE.get(), false, EquipmentSlot.CHEST, 0.25f, 0.4f))
                .addModule(underplateAttribute(AttributeRegistry.ARCANE_RESONANCE.get(), true, EquipmentSlot.LEGS))
                .addModule(underplateAttribute(AttributeRegistry.MALIGNANT_CONVERSION.get(), false, EquipmentSlot.LEGS))
                .addModule(underplateAttribute(AttributeRegistry.SOUL_WARD_INTEGRITY.get(), true, EquipmentSlot.FEET))
                .addModule(underplateAttribute(AttributeRegistry.SOUL_WARD_RECOVERY_RATE.get(), false, EquipmentSlot.FEET, 0.5f, Float.MAX_VALUE))
                .addModule(underplateAttribute(AttributeRegistry.SCYTHE_PROFICIENCY.get(), true, EquipmentSlot.HEAD))
                .addModule(underplateAttribute(AttributeRegistry.SOUL_WARD_CAP.get(), false, EquipmentSlot.HEAD, 0.5f, Float.MAX_VALUE));


    }

    private static final float UNDERPLATE_TOUGHNESS_FACTOR = 0.8f;


    //below are custom methods
    private static final float UNDERPLATE_K1 = 20.0f;
    private static final float UNDERPLATE_P1 = -0.08f;
    private static final float UNDERPLATE_K2 = 400.0f;
    private static final float UNDERPLATE_P2 = -0.17f;

    private static AttributeModule underplateAttribute(Attribute attribute, boolean positive, EquipmentSlot slot) {
        return underplateAttribute(attribute, positive, slot, 1.0f, Float.MAX_VALUE);
    }

    private static AttributeModule underplateAttribute(
            Attribute attribute,
            boolean positive,
            EquipmentSlot slot,
            float scale,
            float cap
    ) {
        AttributeModule.Builder builder = AttributeModule.builder(attribute, AttributeModifier.Operation.MULTIPLY_TOTAL)
                                                         .slots(slot)
                                                         .tooltipStyle(AttributeModule.TooltipStyle.ATTRIBUTE)
                                                         .customVariable("armor", new ToolStatVariable(ToolStats.ARMOR))
                                                         .customVariable("toughness", new ToolStatVariable(ToolStats.ARMOR_TOUGHNESS));

        var formula = builder.formula();

        appendUnderplateSoftcapFormula(formula);

        formula
                .constant(scale).multiply()
                .constant(cap).min();

        if (!positive){
            formula.constant(-1.0f).multiply();
        }

        return formula.build();
    }

    private static void appendUnderplateSoftcapFormula(AttributeModule.Builder.FormulaVariableBuilder formula) {
        appendUnderplateU(formula);
        formula
                .constant(UNDERPLATE_K1).divide()
                .constant(1.0f).add()
                .constant(UNDERPLATE_P1).power();

        appendUnderplateU(formula);
        formula
                .constant(UNDERPLATE_K2).divide()
                .constant(1.0f).add()
                .constant(UNDERPLATE_P2).power();

        formula
                .multiply()
                .constant(-1.0f).multiply()
                .constant(1.0f).add()
                .constant(UNDERPLATE_MAX).multiply();
    }

    private static void appendUnderplateU(AttributeModule.Builder.FormulaVariableBuilder formula) {
        formula
                .customVariable("armor")
                .constant(UNDERPLATE_ARMOR_FACTOR).multiply()
                .customVariable("toughness")
                .constant(UNDERPLATE_TOUGHNESS_FACTOR).multiply()
                .add()
                .constant(0.0f).max();
    }

    @Override
    protected void addModifiers() {
        buildModifier(Ids.fly)
                .addModule(new FlightModule());
        buildModifier(Ids.long_tool)
                .addModule(AttributeModule.builder(ForgeMod.BLOCK_REACH.get(), AttributeModifier.Operation.ADDITION).slots(EquipmentSlot.MAINHAND).eachLevel(1))
                .addModule(
                        AttributeModule.builder(ForgeMod.ENTITY_REACH.get(), AttributeModifier.Operation.ADDITION).slots(EquipmentSlot.MAINHAND).eachLevel(1));
        buildModifier(Ids.soul_form)
                .addModule(AllSlotModule.builder().amount(1, 0));
        buildModifier(Ids.strong_explode)
                .priority(-100)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().toolItem(ItemPredicate.set(DreamtinkerTools.tntarrow.asItem())).percent()
                                                       .formula()
                                                       .variable(LEVEL).constant(4f).multiply()
                                                       .variable(MULTIPLIER).multiply()
                                                       .constant(1f).add()
                                                       .variable(VALUE).multiply().build());
        buildModifier(Ids.antimony_usage).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.DURABILITY).eachLevel(0.1f))
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_DAMAGE).eachLevel(0.1f))
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_SPEED).eachLevel(0.05f))
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.1f))
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.DRAW_SPEED).eachLevel(-0.1f))
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.ARMOR).eachLevel(0.1f))
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.ARMOR_TOUGHNESS).eachLevel(0.1f))
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.KNOCKBACK_RESISTANCE).eachLevel(0.1f))
                                         .addModule(StatBoostModule.multiplyBase(ToolStats.BLOCK_AMOUNT).eachLevel(0.1f));
        buildModifier(Ids.the_wolf_answer)
                .priority(-1000)
                .addModule(ConditionalMeleeDamageModule.builder().percent()
                                                       .customVariable("wolf_effects",
                                                                       new EntityMeleeVariable(WOLF_EFFECTS, EntityMeleeVariable.WhichEntity.TARGET, 0.0f))
                                                       .formula()
                                                       .customVariable("wolf_effects")
                                                       .constant(6.66f).multiply()
                                                       .constant(1.0f).add()
                                                       .variable(VALUE).multiply()
                                                       .build())
                .addModule(ConditionalPowerModule.builder().percent()
                                                 .customVariable("wolf_effects",
                                                                 new EntityPowerVariable(WOLF_EFFECTS, EntityMeleeVariable.WhichEntity.TARGET, 0.0f))
                                                 .formula()
                                                 .customVariable("wolf_effects")
                                                 .constant(6.66f).multiply()
                                                 .constant(1.0f).add()
                                                 .variable(VALUE).multiply()
                                                 .build());
        buildModifier(Ids.in_rain)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(BlockDamageSourceModule.source(new DamageTypePredicate(DamageTypes.HOT_FLOOR)).build())
                .addModule(ReplaceBlockWalkerModule.builder().replaceAlways(
                        BlockPropertiesPredicate.block(Blocks.FIRE).range(FireBlock.AGE, 0, FireBlock.MAX_AGE).build(),
                        Blocks.AIR.defaultBlockState()).amount(2, 1))
                .addModule(ReplaceBlockWalkerModule.builder().replaceAlways(BlockPropertiesPredicate.block(Blocks.LAVA).matches(LiquidBlock.LEVEL, 0).build(),
                                                                            Blocks.OBSIDIAN.defaultBlockState()).amount(2, 1))
                .addModule(ReplaceBlockWalkerModule.builder().replaceAlways(BlockPropertiesPredicate.block(Blocks.LAVA).range(LiquidBlock.LEVEL, 1, 15).build(),
                                                                            Blocks.STONE.defaultBlockState()).amount(2, 1))
                .addModule(ReplaceBlockWalkerModule.builder().replaceAlways(
                        BlockPropertiesPredicate.block(Blocks.FARMLAND).range(FarmBlock.MOISTURE, 0, FarmBlock.MAX_MOISTURE).build(),
                        Blocks.FARMLAND.defaultBlockState()
                                       .setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE)).amount(2, 1));
        IJsonPredicate<Item> harvest = ItemPredicate.tag(HARVEST);
        IJsonPredicate<Item> armor = ItemPredicate.tag(WORN_ARMOR);
        EnchantmentModule CONSTANT_FORTUNE = EnchantmentModule.builder(Enchantments.BLOCK_FORTUNE).toolItem(harvest).constant();
        EnchantmentModule ARMOR_FORTUNE = EnchantmentModule.builder(Enchantments.BLOCK_FORTUNE).toolItem(armor).armorHarvest(ARMOR_SLOTS);
        AttributeModule ARMOR_LUCK =
                AttributeModule.builder(Attributes.LUCK, AttributeModifier.Operation.ADDITION).toolTag(TinkerTags.Items.ARMOR).eachLevel(1);
        StatBoostModule SEA_LUCK = StatBoostModule.add(ToolStats.SEA_LUCK).eachLevel(1);
        LootingModule WEAPON_LOOTING = LootingModule.builder().toolItem(ItemPredicate.or(ItemPredicate.set(Items.AIR), ItemPredicate.tag(MELEE))).weapon();
        LootingModule ARMOR_LOOTING = LootingModule.builder().toolItem(armor).armor(ARMOR_SLOTS);
        buildModifier(Ids.with_tears)
                .addModules(CONSTANT_FORTUNE, ARMOR_FORTUNE, WEAPON_LOOTING, ARMOR_LOOTING, SEA_LUCK, ARMOR_LUCK)
                .addModule(ProtectionModule.builder()
                                           .customVariable("rain", new EntityProtectionVariable(
                                                   new ConditionalEntityVariable(LivingEntityPredicate.RAINING, 2.5f, 0.0f),
                                                   EntityProtectionVariable.WhichEntity.TARGET,
                                                   0.0f))
                                           .formula()
                                           .constant(-1.25f)
                                           .customVariable("rain")
                                           .add()
                                           .variable(LEVEL)
                                           .multiply()
                                           .build());
        buildModifier(Ids.wither_body).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                      .addModule(new EffectImmunityModule(MobEffects.POISON))
                                      .addModule(new EffectImmunityModule(MobEffects.WITHER))
                                      .addModule(new EffectImmunityModule(MobEffects.REGENERATION));
        buildModifier(Ids.wither_shoot).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                       .addModule(ProjectileSpawnModule.builder(EntityType.WITHER_SKULL)
                                                                       .fallbackSpeed(LevelingValue.flat(1.0f))
                                                                       .dangerousHealthRatio(LevelingValue.flat(0.5f))
                                                                       .build())
                                       .addModule(ProjectileCloudOnHitModule.builder()
                                                                            .effects(List.of(
                                                                                    new ProjectileCloudOnHitModule.CloudEffect(MobEffects.WITHER, 60, 0, false,
                                                                                                                               false, true),
                                                                                    new ProjectileCloudOnHitModule.CloudEffect(TinkerEffects.bleeding.get(), 60,
                                                                                                                               0, false, false, true)
                                                                            )).build());
        buildModifier(Ids.soul_upgrade).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                       .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                       .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));
        buildModifier(Ids.abyss_inside).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                       .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                       .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));
        buildModifier(Ids.meta_morphosis).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                         .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                         .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));
        buildModifier(Ids.continuous_explode)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        buildModifier(Ids.soul_core)
                .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).amount(0f, 0.5f))
                .addModule(StatBoostModule.add(ToolStats.DRAW_SPEED).amount(0.5f, 0.5f))
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.soul_core).build());
        buildModifier(Ids.icy_memory)
                .levelDisplay(new ModifierLevelDisplay.UniqueForLevels(3))
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.icy_memory).build());
        buildModifier(Ids.hate_memory)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(new FreezingAttackModule(new LevelingValue(20, 10)))
                .addModule(MobEffectModule.builder(MobEffects.POISON)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .target(new HasMobEffectPredicate(MobEffects.POISON).inverted())
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.REGENERATION)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .target(new HasMobEffectPredicate(MobEffects.REGENERATION).inverted())
                                          .chance(LevelingValue.eachLevel(0.99f))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.WITHER)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .target(new HasMobEffectPredicate(MobEffects.WITHER).inverted())
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.DAMAGE_BOOST)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN)
                                          .level(RandomLevelingValue.flat(3))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SPEED)
                                          .level(RandomLevelingValue.flat(2))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.LUCK)
                                          .level(RandomLevelingValue.flat(3))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.UNLUCK)
                                          .level(RandomLevelingValue.flat(2))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.HARM)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.flat(4 * 20))
                                          .chance(LevelingValue.eachLevel(0.30f))
                                          .build())
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.hate_memory).build());
        buildModifier(Ids.huge_ego).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                   .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                   .addModules(ModifierSlotModule.slot(SlotType.UPGRADE).eachLevel(1))
                                   .addModule(StatBoostModule.multiplyAll(ToolStats.DURABILITY).amount(0, -0.25f));
        buildModifier(Ids.full_concentration).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.thundering_curse).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.MorningLordEULA).addModule(
                ConditionalMiningSpeedModule.builder()
                                            .customVariable("light", new BlockLightVariable(LightLayer.BLOCK, 15))
                                            .formula()
                                            .constant(3)
                                            .customVariable("light").constant(5).subtract()
                                            .constant(6).divide()
                                            .power()
                                            .variable(LEVEL).multiply()
                                            .variable(MULTIPLIER).multiply()
                                            .variable(VALUE).add().build());
        buildModifier(Ids.ykhEULA)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ToolTankHelper.TANK_HANDLER)
                .addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME / 2.0f))
                .addModule(MeltingModule.builder().temperature(new LevelingInt(900, 100)).nuggetsPerMetal(new LevelingInt(10, 2))
                                        .shardsPerGem(new LevelingInt(7, 1)).build());
        buildModifier(Ids.EULA)
                .addModule(ConditionalMiningSpeedModule.builder().formula()
                                                       .customVariable("temperature", new BlockTemperatureVariable(-0.5f))
                                                       .constant(0.9f).subtractFlipped() // (1.5 - temperature)
                                                       .variable(LEVEL).multiply() // * level
                                                       .constant(7.5f / 1.25f).multiply() // * 7.5 / 1.25
                                                       .variable(MULTIPLIER).multiply() // * multiplier
                                                       .variable(VALUE).add() // + baseValue
                                                       .build());
        buildModifier(Ids.FragileButBright)
                .addModule(AttributeModule.builder(DreamtinkerAttributes.FATE_VEIL.get(), AttributeModifier.Operation.ADDITION).eachLevel(8f));

        buildModifier(Ids.homunculus_life_curse).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.homunculusGift).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.peaches_in_memory)
                .addModule(AttributeModule.builder(TinkerAttributes.BAD_EFFECT_DURATION, AttributeModifier.Operation.MULTIPLY_TOTAL).amount(0.1f, 0.1f))
                .addModule(AttributeModule.builder(TinkerAttributes.EXPERIENCE_MULTIPLIER, AttributeModifier.Operation.MULTIPLY_TOTAL).amount(0.1f, 0.1f))
                .addModule(ReduceToolDamageModule.builder().maxLevel(2).reinforcedTooltip().formula()
                                                 .variable(LEVEL).constant(1).subtract()
                                                 .constant(0.5283f).multiply()
                                                 .constant(0.413f).add()
                                                 .variable(MULTIPLIER).multiply()
                                                 .build());
        buildModifier(Ids.weapon_slots)
                .priority(1500)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(InventoryModule.builder().toolItem(ItemPredicate.tag(MELEE))
                                          .filter(ItemPredicate.and(ItemPredicate.tag(MODIFIABLE), ItemPredicate.tag(ARMOR).inverted(),
                                                                    ItemPredicate.tag(DreamtinkerTagKeys.Items.weapon_slot_excluded).inverted())).flatSlots(5))
                .addModule(InventoryMenuModule.ANY);
        buildModifier(Ids.shadow_blessing)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(AttributeModule.builder(TinkerAttributes.PROTECTION_CAP, AttributeModifier.Operation.ADDITION)
                                          .tooltipStyle(AttributeModule.TooltipStyle.ATTRIBUTE).flat(0.05f));
        buildModifier(Ids.silver_name_bee)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(EntityLootMultiplierModule.builder().chance(LevelingValue.flat(1)).build());

        buildModifier(Ids.the_romantic)
                .priority(1000)
                .addModule(StatBoostModule.add(ToolStats.ATTACK_SPEED).eachLevel(0.3f))
                .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).eachLevel(-0.2f));
        buildModifier(Ids.all_slayer)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().eachLevel(2.0f));
        buildModifier(Ids.weapon_dreams_filter)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(weapon_dreams.getId(), 1))
                                                     .modifierKey(Ids.weapon_dreams_filter).build());
        buildModifier(Ids.weapon_dreams_order)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(weapon_dreams.getId(), 1))
                                                     .modifierKey(Ids.weapon_dreams_order).build());
        buildModifier(Ids.light_emanation)
                .addModule(lightEmanation(ToolStats.ACCURACY))
                .addModule(lightEmanation(ToolStats.DRAW_SPEED))
                .addModule(lightEmanation(ToolStats.PROJECTILE_DAMAGE));
        buildModifier(Ids.fiber_glass_fragments)
                .addModule(MobEffectModule.builder(TinkerEffects.bleeding.get())
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.lunarProtection)
                .addModule(DepthProtectionModule.builder().baselineHeight(30).neutralRange(0).eachLevel(-2.5f))
                .addModule(AttributeModule.builder(ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_BASE)
                                          .tooltipStyle(AttributeModule.TooltipStyle.PERCENT).flat(-0.05f));

        buildModifier(Ids.lunarRejection)
                .addModule(MobEffectModule.builder(MobEffects.LEVITATION)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .applyBeforeMelee(true)
                                          .build())
                .addModule(ConditionalMeleeDamageModule.builder().target(TinkerPredicate.AIRBORNE).eachLevel(2.5f))
                .addModule(ConditionalPowerModule.builder().target(TinkerPredicate.AIRBORNE).eachLevel(2.5f))
                .addModule(ConditionalMiningSpeedModule.builder().holder(LivingEntityPredicate.ON_GROUND.inverted()).percent().allowIneffective().flat(2),
                           ModifierHooks.BREAK_SPEED);
        buildModifier(Ids.slowness)
                .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.perLevel(20 * 10, 20 * 5))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);

        buildModifier(Ids.soul_unchanged)
                .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).eachLevel(1))
                .addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        buildModifier(Ids.sun_changed)
                .addModule(BlockLootMultiplierModule.builder()
                                                    .blocks(BlockPredicate.tag(Tags.Blocks.ORES)).items(ItemPredicate.tag(Tags.Items.ORES))
                                                    .times(RandomLevelingValue.perLevel(0.5f, 0.5f))
                                                    .chance(LevelingValue.flat(1)).build());
        buildModifier(Ids.force_to_explosion)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.aggressiveFoxUsage)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new SelfDestroyModule(DreamtinkerEntityTypes.AggressiveFOX.get()));

        buildModifier(Ids.five_creations)
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new SwappableToolTraitsModule(null, "traits", ToolHooks.REBALANCED_TRAIT))
                .addModule(new SwappableCircleWeaponAttack(null, "designs", 6));
        buildModifier(Ids.golden_face);
        buildModifier(Ids.whimsy_face);
        buildModifier(arcane_hit.getId());
        buildModifier(Ids.arcane_protection)
                .addModule(MaxArmorAttributeModule.builder(TinkerAttributes.GOOD_EFFECT_DURATION, AttributeModifier.Operation.MULTIPLY_BASE)
                                                  .heldTag(TinkerTags.Items.HELD)
                                                  .eachLevel(0.05f))
                .addModule(ProtectionModule.builder()
                                           .sources(DamageSourcePredicate.ANY, DamageSourcePredicate.tag(BYPASSES_ENCHANTMENTS))
                                           .eachLevel(3.0f));
        buildModifier(Ids.drinker_magic);
        buildModifier(Ids.monster_blood).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        IJsonPredicate<LivingEntity> deep_water = LivingEntityPredicate.or(LivingEntityPredicate.EYES_IN_WATER, LivingEntityPredicate.FEET_IN_WATER,
                                                                           LivingEntityPredicate.UNDERWATER, LivingEntityPredicate.RAINING,
                                                                           new MobTypePredicate(MobType.WATER));
        buildModifier(Ids.deeper_water_killer)
                .addModule(ConditionalMeleeDamageModule.builder().target(deep_water).eachLevel(2.5f))
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS).level(RandomLevelingValue.flat(4)).time(RandomLevelingValue.random(20, 10))
                                          .target(deep_water).build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.sun_shine)
                .addModule(MobEffectsRemoverModule.builder().level(RandomLevelingValue.perLevel(0, 1)).build())
                .addModule(MobEffectsRemoverModule.builder().level(RandomLevelingValue.perLevel(0, 1)).category(MobEffectCategory.HARMFUL).build())
                .addModule(MobEffectsRemoverModule.builder().level(RandomLevelingValue.perLevel(0, 1)).category(MobEffectCategory.NEUTRAL).build());

        buildModifier(Ids.sweet_death)
                .addModule(StatBoostModule.add(ToolStats.BLOCK_AMOUNT).eachLevel(40.0f))
                .addModule(StatBoostModule.add(ToolStats.BLOCK_ANGLE).eachLevel(90f))
                .addModule(StatBoostModule.add(ToolStats.USE_ITEM_SPEED).eachLevel(-0.1f))
                .addModule(KnockbackModule.builder().formula()
                                          .variable(VALUE)
                                          .constant(3).variable(LEVEL).power() // 3^LEVEL
                                          .divide().build()) // KNOCKBACK / 3^LEVEL;
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.last_kiss, 1).inverted())
                                                     .modifierKey(Ids.sweet_death).build());
        buildModifier(Ids.last_kiss)
                .addModule(StatBoostModule.multiplyBase(ToolStats.BLOCK_AMOUNT).eachLevel(-0.20f))
                .addModule(StatBoostModule.multiplyBase(ToolStats.BLOCK_ANGLE).eachLevel(-0.35f))
                .addModule(StatBoostModule.add(ToolStats.USE_ITEM_SPEED).eachLevel(0.4f))
                .addModule(AttributeModule.builder(TinkerAttributes.USE_ITEM_SPEED, AttributeModifier.Operation.ADDITION).slots(ARMOR_SLOTS).tooltipStyle(
                        AttributeModule.TooltipStyle.PERCENT).toolItem(ItemPredicate.tag(SHIELDS)).eachLevel(0.1f))
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.sweet_death, 1).inverted())
                                                     .modifierKey(Ids.sweet_death).build());


        IJsonPredicate<net.minecraft.world.entity.LivingEntity> ender = LivingEntityPredicate.tag(DreamtinkerTagKeys.EntityTypes.ENDER_ENTITY);
        buildModifier(ender_slayer.getId(), not(DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy")))
                .addModule(ConditionalMeleeDamageModule.builder().target(ender).percent()
                                                       .formula()
                                                       .variable(LEVEL).constant(.5f).multiply()
                                                       .variable(MULTIPLIER).multiply()
                                                       .constant(1).add()
                                                       .variable(VALUE).multiply().build())
                .addModule(KnockbackModule.builder().entity(ender)
                                          .formula()
                                          .variable(LEVEL).constant(5f).multiply()
                                          .variable(VALUE).multiply().build())
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20 * 2, 10))
                                          .target(ender).build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.heavy_arrow)
                .addModule(StatBoostModule.add(ToolStats.ACCURACY).eachLevel(-0.25f))
                .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.5f));
        buildModifier(Ids.light_arrow)
                .addModule(StatBoostModule.add(ToolStats.VELOCITY).eachLevel(0.5f))
                .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(-0.25f));
        buildModifier(Ids.balanced_arrow)
                .addModule(StatBoostModule.add(ToolStats.VELOCITY).eachLevel(0.25f))
                .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.25f));
        buildModifier(Ids.null_void)
                .addModule(MobEffectModule.builder(MobEffects.DARKNESS).level(RandomLevelingValue.flat(1)).time(RandomLevelingValue.random(20, 10)).build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(DreamtinkerEffects.RealDarkness).level(RandomLevelingValue.flat(1)).time(RandomLevelingValue.random(20, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.hidden_shape);
        IJsonPredicate<LivingEntity> wrath = LivingEntityPredicate.or(LivingEntityPredicate.FIRE_IMMUNE, new MobTypePredicate(MobType.WATER));
        buildModifier(Ids.torrent)
                .priority(1000)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().target(wrath).eachLevel(2.5f))
                .addModule(ConditionalPowerModule.builder().target(wrath).eachLevel(2.5f))
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.torrent, 1).inverted())
                                                     .modifierKey(Ids.wrath).build());
        buildModifier(Ids.wrath)
                .priority(1000)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).eachLevel(1.25f))
                .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(1.25f))
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.wrath, 1).inverted())
                                                     .modifierKey(Ids.torrent).build());

        buildModifier(Ids.poison)
                .addModule(MobEffectModule.builder(MobEffects.POISON)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20 * 2, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.weakness)
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20 * 2, 10))
                                          .build());
        buildModifier(Ids.curse_fire)
                .addModule(MobEffectModule.builder(DreamtinkerEffects.SoulFire).applyBeforeMelee(true)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.perLevel(20 * 6, 20))
                                          .build())
                .addModule(MobEffectModule.builder(DreamtinkerEffects.cursed).applyBeforeMelee(true)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.random(20 * 4, 10))
                                          .build())
                .addModule(SelfMobEffectModule.builder(DreamtinkerTagKeys.MobEffects.EDICTS)
                                              .level(RandomLevelingValue.perLevel(0, 1))
                                              .time(RandomLevelingValue.random(20 * 4, 10))
                                              .build())
                .addModule(new EffectImmunityModule(DreamtinkerEffects.SoulFire.get()));
        buildModifier(Ids.falsify_fate)
                .priority(-300)//make sure late enough
                .addModule(ConditionalMiningSpeedModule.builder().allowIneffective()
                                                       .customVariable("hardness", new BlockMiningSpeedVariable(BlockVariable.HARDNESS, 10))
                                                       .percent()
                                                       .formula()
                                                       .constant(24.0f).customVariable("hardness").multiply() // 24*hardness
                                                       .variable(VALUE).divide()//above/current speed
                                                       .constant(1.0f).subtract().constant(0.35f).multiply()//(above-1)*0.35
                                                       //.variable(MULTIPLIER).multiply() // above * multiplier
                                                       .constant(1).add()
                                                       .constant(0.7f).max()
                                                       .constant(1.5f)
                                                       .variable(LEVEL).add().min()
                                                       .variable(MULTIPLIER).multiply()
                                                       .variable(VALUE).multiply()
                                                       .build());
        buildModifier(Ids.frost_steel_shell)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new EffectImmunityModule(MobEffects.MOVEMENT_SLOWDOWN))
                .addModule(AttributeModule.builder(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE).flat(-0.05f));

        buildModifier(Ids.sticky_string)
                .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.5f))
                .addModule(StatBoostModule.add(ToolStats.DRAW_SPEED).eachLevel(-0.2f));
        buildModifier(Ids.pressing_front)
                .addModule(MobEffectModule.builder(DreamtinkerEffects.PressingFront.get())
                                          .applyBeforeMelee(true)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20 * 4, 10))
                                          .build());
        buildModifier(Ids.with_wing_with_scale)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MaterialRepairModule.material(DreamtinkerMaterialIds.scolecite).constant(500));
        buildModifier(Ids.scale_within)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new ResonanceArmorModule.Builder().percentage(LevelingValue.flat(0.4f)).build(), ModifierHooks.MODIFY_HURT)
                .addModule(AttributeModule.builder(DreamtinkerAttributes.BLOOD_IN_SHELL, AttributeModifier.Operation.ADDITION).flat(6f));
        buildModifier(Ids.wing_without)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new RepriseProtectionModule.Builder().percentage(LevelingValue.flat(0.5f)).build(), ModifierHooks.MODIFY_HURT)
                .addModule(ProtectionModule.builder()
                                           .sources(DamageSourcePredicate.ANY, DamageSourcePredicate.ANY)
                                           .flat(3.0f))
                .addModule(AttributeModule.builder(DreamtinkerAttributes.FATE_VEIL, AttributeModifier.Operation.ADDITION).flat(25f));

        buildModifier(Ids.reprise_protection)
                .addModule(new RepriseProtectionModule.Builder().percentage(LevelingValue.eachLevel(0.25f)).build(), ModifierHooks.MODIFY_HURT);


        buildModifier(Ids.carapace_fall)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().percent()
                                                       .formula()
                                                       .customVariable("armor", new EntityMeleeVariable(new AttributeEntityVariable(Attributes.ARMOR),
                                                                                                        EntityMeleeVariable.WhichEntity.ATTACKER, 0.0f))
                                                       .customVariable("armor_toughness",
                                                                       new EntityMeleeVariable(new AttributeEntityVariable(Attributes.ARMOR_TOUGHNESS),
                                                                                               EntityMeleeVariable.WhichEntity.ATTACKER,
                                                                                               0.0f))
                                                       .constant(2).multiply().add()
                                                       .constant(1).max()
                                                       .constant(60).divideFlipped()
                                                       .constant(1).add()
                                                       .constant(1).divideFlipped()
                                                       .variable(LEVEL).multiply()
                                                       .constant(1f).add()
                                                       .variable(VALUE).multiply().build())
                .addModule(ConditionalPowerModule.builder().percent()
                                                 .formula()
                                                 .customVariable("armor", new EntityPowerVariable(new AttributeEntityVariable(Attributes.ARMOR),
                                                                                                  EntityMeleeVariable.WhichEntity.ATTACKER, 0.0f))
                                                 .customVariable("armor_toughness",
                                                                 new EntityPowerVariable(new AttributeEntityVariable(Attributes.ARMOR_TOUGHNESS),
                                                                                         EntityMeleeVariable.WhichEntity.ATTACKER,
                                                                                         0.0f))
                                                 .constant(2).multiply().add()
                                                 .constant(1).max()
                                                 .constant(60).divideFlipped()
                                                 .constant(1).add()
                                                 .constant(1).divideFlipped()
                                                 .variable(LEVEL).multiply()
                                                 .constant(1f).add()
                                                 .variable(MULTIPLIER).multiply()
                                                 .variable(VALUE).multiply().build());

        buildModifier(Ids.huge_explosion)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(ExplosionLikeProjectileDamageModule.builder(LevelingValue.flat(42), LevelingValue.flat(4), DamageTypes.EXPLOSION).build());
        buildModifier(Ids.unbreakable)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS).priority(100)
                .addModule(StatBoostModule.multiplyAll(ToolStats.DURABILITY).flat(10))
                .addModule(new DurabilityBarColorModule(0xffffff))
                .addModule(ReduceToolDamageModule.builder().flat(1.0f));
        buildModifier(Ids.divineMaledictus, modLoaded("forbidden_arcanus"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .addModules(ModifierSlotModule.slot(EsotericismSlotType.DELUSION).flat(1));
        buildModifier(Ids.HuaiPuBaoYu)
                .addModule(new RarityModule(Rarity.RARE))
                // melee harvest
                .addModule(StatBoostModule.multiplyConditional(ToolStats.ATTACK_DAMAGE).eachLevel(0.5f))
                .addModule(StatBoostModule.multiplyConditional(ToolStats.MINING_SPEED).eachLevel(0.5f))
                // ranged
                .addModule(StatBoostModule.multiplyConditional(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.5f))
                .addModule(StatBoostModule.multiplyConditional(ToolStats.ACCURACY).eachLevel(0.2f));

        buildModifier(Ids.side_attack)
                .addModule(ConditionalMeleeDamageModule.builder().percent()
                                                       .customVariable("attacker_x",
                                                                       new EntityMeleeVariable(HORIZONTAL_LOOK_X, EntityMeleeVariable.WhichEntity.ATTACKER,
                                                                                               0.0f))
                                                       .customVariable("attacker_z",
                                                                       new EntityMeleeVariable(HORIZONTAL_LOOK_Z, EntityMeleeVariable.WhichEntity.ATTACKER,
                                                                                               0.0f))
                                                       .customVariable("target_x",
                                                                       new EntityMeleeVariable(HORIZONTAL_LOOK_X, EntityMeleeVariable.WhichEntity.TARGET, 0.0f))
                                                       .customVariable("target_z",
                                                                       new EntityMeleeVariable(HORIZONTAL_LOOK_Z, EntityMeleeVariable.WhichEntity.TARGET, 0.0f))
                                                       .formula()
                                                       // dir = max(attacker · target, 0)
                                                       .customVariable("attacker_x").customVariable("target_x").multiply()
                                                       .customVariable("attacker_z").customVariable("target_z").multiply()
                                                       .add()
                                                       .constant(0.0f).max()

                                                       // 1.4 + (level - 1) * 0.4 = 1 + level * 0.4
                                                       .constant(0.4f).variable(LEVEL).multiply()
                                                       .constant(1.0f).add()
                                                       .multiply()

                                                       .variable(MULTIPLIER).multiply()
                                                       .constant(1.0f).add()
                                                       .variable(VALUE).multiply()
                                                       .build())
                .addModule(ConditionalPowerModule.builder().percent()
                                                 .customVariable("attacker_x",
                                                                 new EntityPowerVariable(HORIZONTAL_LOOK_X, EntityMeleeVariable.WhichEntity.ATTACKER, 0.0f))
                                                 .customVariable("attacker_z",
                                                                 new EntityPowerVariable(HORIZONTAL_LOOK_Z, EntityMeleeVariable.WhichEntity.ATTACKER, 0.0f))
                                                 .customVariable("target_x",
                                                                 new EntityPowerVariable(HORIZONTAL_LOOK_X, EntityMeleeVariable.WhichEntity.TARGET, 0.0f))
                                                 .customVariable("target_z",
                                                                 new EntityPowerVariable(HORIZONTAL_LOOK_Z, EntityMeleeVariable.WhichEntity.TARGET, 0.0f))
                                                 .formula()
                                                 // dir = max(attacker · target, 0)
                                                 .customVariable("attacker_x").customVariable("target_x").multiply()
                                                 .customVariable("attacker_z").customVariable("target_z").multiply()
                                                 .add()
                                                 .constant(0.0f).max()

                                                 // 1.4 + (level - 1) * 0.4 = 1 + level * 0.4
                                                 .constant(0.4f).variable(LEVEL).multiply()
                                                 .constant(1.0f).add()
                                                 .multiply()

                                                 .variable(MULTIPLIER).multiply()
                                                 .constant(1.0f).add()
                                                 .variable(VALUE).multiply()
                                                 .build());

        addELModifiers();
        addMalumModifiers();
        addEidolonModifiers();
        addBICModifiers();
        addNovaModifiers();
        addUGModifiers();
        addOCCModifiers();
        addBOTANIAModifiers();
        addLMModifiers();
        addFAAModifiers();

    }

    private void addFAAModifiers() {
        buildModifier(Ids.faa_aureal_protection, modLoaded("forbidden_arcanus"))
                .addModule(ProtectionModule.builder().source(DamageSourcePredicate.ANY)
                                           .formula()
                                           .customVariable("aureal",
                                                           new EntityProtectionVariable(AUREAL, EntityProtectionVariable.WhichEntity.TARGET, 0.0f))
                                           .constant(50).divide()
                                           .variable(LEVEL).multiply()
                                           .variable(VALUE).add().build());
        buildModifier(Ids.faa_aureal_attack, modLoaded("forbidden_arcanus"))
                .addModule(ConditionalPowerModule.builder().percent().formula()
                                                 .customVariable("aureal",
                                                                 new EntityPowerVariable(AUREAL, EntityMeleeVariable.WhichEntity.ATTACKER, 0.0f))
                                                 .constant(500).divide()
                                                 .variable(LEVEL).multiply()
                                                 .variable(MULTIPLIER).multiply()
                                                 .constant(1).add()
                                                 .variable(VALUE).multiply().build())
                .addModule(ConditionalMeleeDamageModule.builder().percent().formula()
                                                       .customVariable("aureal",
                                                                       new EntityMeleeVariable(AUREAL, EntityMeleeVariable.WhichEntity.ATTACKER, 0.0f))
                                                       .constant(500).divide()
                                                       .variable(LEVEL).multiply()
                                                       .variable(MULTIPLIER).multiply()
                                                       .constant(1).add()
                                                       .variable(VALUE).multiply().build());

        buildModifier(Ids.faa_corruption_attack, modLoaded("forbidden_arcanus"))
                .addModule(ConditionalPowerModule.builder().percent().formula()
                                                 .customVariable("corruption",
                                                                 new EntityPowerVariable(CORRUPTION, EntityMeleeVariable.WhichEntity.ATTACKER, 0.0f))
                                                 .constant(125).divide()
                                                 .variable(LEVEL).multiply()
                                                 .variable(MULTIPLIER).multiply()
                                                 .constant(1).add()
                                                 .variable(VALUE).multiply().build())
                .addModule(ConditionalMeleeDamageModule.builder().percent().formula()
                                                       .customVariable("corruption",
                                                                       new EntityMeleeVariable(CORRUPTION, EntityMeleeVariable.WhichEntity.ATTACKER, 0.0f))
                                                       .constant(125).divide()
                                                       .variable(LEVEL).multiply()
                                                       .variable(MULTIPLIER).multiply()
                                                       .constant(1).add()
                                                       .variable(VALUE).multiply().build());
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Provider";
    }

}
