package org.dreamtinker.dreamtinker.tools.data;

import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.registries.EnigmaticEnchantments;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
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
import org.dreamtinker.dreamtinker.common.DreamtinkerAttributes;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.library.modifiers.modules.combat.MobEffectsRemoverModule;
import org.dreamtinker.dreamtinker.library.modifiers.modules.combat.SelfMobEffectModule;
import org.dreamtinker.dreamtinker.library.modifiers.modules.harvest.BlockLootMultiplierModule;
import org.dreamtinker.dreamtinker.library.modifiers.modules.harvest.EntityLootMultiplierModule;
import org.dreamtinker.dreamtinker.library.modifiers.modules.weapon.SwappableCircleWeaponAttack;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.dreamtinker.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
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
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.variable.block.BlockVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockLightVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockMiningSpeedVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockTemperatureVariable;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.*;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.MaterialRepairModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.build.*;
import slimeknights.tconstruct.library.modifiers.modules.combat.*;
import slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.MeltingModule;
import slimeknights.tconstruct.tools.modules.armor.DepthProtectionModule;
import slimeknights.tconstruct.tools.modules.combat.FreezingAttackModule;

import static net.minecraft.tags.DamageTypeTags.BYPASSES_ENCHANTMENTS;
import static org.dreamtinker.dreamtinker.common.DreamtinkerCommon.BLOCK_OF_UNDER_GARDEN;
import static org.dreamtinker.dreamtinker.common.DreamtinkerCommon.LIVING_OF_UNDER_GARDEN;
import static org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaRegistry.nova_magic_armor;
import static org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers.*;
import static slimeknights.tconstruct.common.TinkerTags.Items.*;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.*;
import static slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial.ARMOR_SLOTS;

public class DreamtinkerModifierProvider extends AbstractModifierProvider implements IConditionBuilder {
    public DreamtinkerModifierProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addModifiers() {
        buildModifier(Ids.long_tool)
                .addModule(AttributeModule.builder(ForgeMod.BLOCK_REACH.get(), AttributeModifier.Operation.ADDITION).slots(EquipmentSlot.MAINHAND).eachLevel(1))
                .addModule(
                        AttributeModule.builder(ForgeMod.ENTITY_REACH.get(), AttributeModifier.Operation.ADDITION).slots(EquipmentSlot.MAINHAND).eachLevel(1));
        buildModifier(Ids.strong_explode)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().toolItem(ItemPredicate.set(DreamtinkerTools.tntarrow.asItem())).percent()
                                                       .formula()
                                                       .variable(LEVEL).constant(4f).multiply()
                                                       .variable(MULTIPLIER).multiply()
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
                .addModule(ProtectionModule.builder().eachLevel(-5f));
        buildModifier(Ids.soul_form).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                    .addModules(ModifierSlotModule.slot(SlotType.ABILITY).flat(1),
                                                ModifierSlotModule.slot(SlotType.DEFENSE).flat(1),
                                                ModifierSlotModule.slot(SlotType.SOUL).flat(1),
                                                ModifierSlotModule.slot(SlotType.UPGRADE).flat(1));
        buildModifier(Ids.wither_body).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                      .addModule(new EffectImmunityModule(MobEffects.POISON))
                                      .addModule(new EffectImmunityModule(MobEffects.WITHER))
                                      .addModule(new EffectImmunityModule(MobEffects.REGENERATION));
        buildModifier(Ids.soul_upgrade).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                       .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                       .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));
        buildModifier(Ids.abyss_inside).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                       .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                       .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));
        buildModifier(Ids.meta_morphosis).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                         .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                         .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));
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
                .addModule(new FreezingAttackModule(new LevelingValue(8, 4)))
                .addModule(MobEffectModule.builder(MobEffects.POISON)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.REGENERATION)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.flat(13 * 20))
                                          .chance(LevelingValue.eachLevel(0.99f))
                                          .build())
                .addModule(MobEffectModule.builder(MobEffects.WITHER)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.flat(13 * 20))
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
        buildModifier(Ids.why_i_cry);
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
        buildModifier(Ids.AsSand).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.FragileButBright)
                .addModule(StatBoostModule.add(ToolStats.ARMOR).eachLevel(-1.5f))
                .addModule(StatBoostModule.add(ToolStats.ARMOR_TOUGHNESS).eachLevel(-1f))
                .addModule(AttributeModule.builder(DreamtinkerAttributes.FATE_VEIL.get(), AttributeModifier.Operation.ADDITION)
                                          .tooltipStyle(AttributeModule.TooltipStyle.PERCENT).eachLevel(0.03f));
        buildModifier(Ids.homunculusLifeCurse).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.homunculusGift).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.peaches_in_memory)
                .addModule(AttributeModule.builder(TinkerAttributes.BAD_EFFECT_DURATION, AttributeModifier.Operation.MULTIPLY_TOTAL).amount(0.1f, 0.1f))
                .addModule(AttributeModule.builder(TinkerAttributes.EXPERIENCE_MULTIPLIER, AttributeModifier.Operation.MULTIPLY_TOTAL).amount(0.1f, 0.1f))
                .addModule(ReduceToolDamageModule.builder().maxLevel(5).formula()
                                                 .constant(0.025f).variable(LEVEL).multiply() // 0.025 * level
                                                 .constant(13).variable(LEVEL).subtract()     // 13 - level
                                                 .variable(MULTIPLIER).multiply()
                                                 .multiply().build());
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
                                          .tooltipStyle(AttributeModule.TooltipStyle.PERCENT).flat(0.05f));
        buildModifier(Ids.silver_name_bee)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(EntityLootMultiplierModule.builder().chance(LevelingValue.flat(1)).build());

        buildModifier(Ids.the_romantic)
                .addModule(StatBoostModule.add(ToolStats.ATTACK_SPEED).eachLevel(0.3f))
                .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).eachLevel(-0.2f));
        buildModifier(Ids.all_slayer)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().eachLevel(1.5f));
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
        buildModifier(Ids.fiber_glass_fragments)
                .addModule(MobEffectModule.builder(TinkerEffects.bleeding.get())
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.lunarProtection)
                .addModule(DepthProtectionModule.builder().baselineHeight(40).neutralRange(0).eachLevel(-2.5f))
                .addModule(AttributeModule.builder(ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_BASE)
                                          .tooltipStyle(AttributeModule.TooltipStyle.PERCENT).flat(-0.05f));

        buildModifier(Ids.lunarRejection)
                .addModule(MobEffectModule.builder(MobEffects.LEVITATION)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .applyBeforeMelee(true)
                                          .build())
                .addModule(ConditionalMeleeDamageModule.builder().target(TinkerPredicate.AIRBORNE).eachLevel(2f))
                .addModule(ConditionalPowerModule.builder().target(TinkerPredicate.AIRBORNE).eachLevel(2f))
                .addModule(ConditionalMiningSpeedModule.builder().holder(LivingEntityPredicate.ON_GROUND.inverted()).percent().allowIneffective().flat(1),
                           ModifierHooks.BREAK_SPEED);
        buildModifier(Ids.slowness)
                .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.perLevel(20 * 10, 20 * 5))

                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);

        buildModifier(Ids.soul_unchanged)
                .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1))
                .addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        buildModifier(Ids.sun_changed)
                .addModule(BlockLootMultiplierModule.builder()
                                                    .blocks(BlockPredicate.tag(Tags.Blocks.ORES)).items(ItemPredicate.tag(Tags.Items.ORES))
                                                    .times(RandomLevelingValue.perLevel(0.5f, 0.5f))
                                                    .chance(LevelingValue.flat(1)).build());
        buildModifier(Ids.force_to_explosion)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.aggressiveFoxUsage)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        IJsonPredicate<IToolContext> ancientTool = ToolContextPredicate.tag(TinkerTags.Items.ANCIENT_TOOLS);
        buildModifier(Ids.five_creations)
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new SwappableSlotModule(1))
                .addModule(new SwappableSlotModule(null, 1, ModifierCondition.ANY_CONTEXT.with(ancientTool)), ModifierHooks.VOLATILE_DATA)
                .addModule(new SwappableSlotModule.BonusSlot(null, SlotType.SOUL, SlotType.SOUL, 1, ModifierCondition.ANY_CONTEXT))
                .addModule(new SwappableToolTraitsModule(null, "traits", ToolHooks.REBALANCED_TRAIT))
                .addModule(new SwappableCircleWeaponAttack(null, "designs", 6));
        buildModifier(Ids.golden_face);
        buildModifier(Ids.arcane_hit);
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
                .addModule(ConditionalMeleeDamageModule.builder().target(deep_water).eachLevel(2.0f))
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS).level(RandomLevelingValue.flat(4)).time(RandomLevelingValue.random(20, 10))
                                          .target(deep_water).build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.sun_shine)
                .addModule(MobEffectsRemoverModule.builder().level(RandomLevelingValue.perLevel(0, 1)).build())
                .addModule(MobEffectsRemoverModule.builder().level(RandomLevelingValue.perLevel(0, 1)).category(MobEffectCategory.HARMFUL).build())
                .addModule(MobEffectsRemoverModule.builder().level(RandomLevelingValue.perLevel(0, 1)).category(MobEffectCategory.NEUTRAL).build());

        buildModifier(Ids.four_warning)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
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
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().target(wrath).eachLevel(2.5f))
                .addModule(ConditionalPowerModule.builder().target(wrath).eachLevel(2.5f))
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.torrent, 1).inverted())
                                                     .modifierKey(Ids.wrath).build());
        buildModifier(Ids.wrath)
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
                .addModule(MobEffectModule.builder(DreamtinkerEffects.SoulFire)
                                          .level(RandomLevelingValue.perLevel(0, 1))
                                          .time(RandomLevelingValue.random(20 * 4, 10))
                                          .build())
                .addModule(MobEffectModule.builder(DreamtinkerEffects.cursed)
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

        addELModifiers();
        addMalumModifiers();
        addEidolonModifiers();
        addBICModifiers();
        addNovaModifiers();
        addUGModifiers();
        addOCCModifiers();

    }

    private void addELModifiers() {
        buildModifier(Ids.el_by_pass_worthy, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.el_nemesis_curse, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.NEMESIS).level(1).constant());
        buildModifier(Ids.el_sorrow, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.SORROW).level(1).constant());
        buildModifier(Ids.el_eternal_binding, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.ETERNAL_BINDING).level(1).constant());
        buildModifier(Ids.el_etherium, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(SetStatModule.set(ToolStats.HARVEST_TIER).value(EnigmaticMaterials.ETHERIUM));

        buildModifier(Ids.blighted_sigil, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));
    }

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
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(StatBoostModule.multiplyAll(ToolStats.ATTACK_DAMAGE).flat(-0.4f));
        buildModifier(Ids.malum_world_of_weight, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.malum_edge_of_deliverance, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.malum_sol_tiferet, not(DreamtinkerMaterialDataProvider.modLoaded("malum")))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        buildModifier(Ids.many_us, DreamtinkerMaterialDataProvider.modLoaded("malum"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));


    }

    private void addEidolonModifiers() {
        buildModifier(Ids.eidolon_vulnerable, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(MobEffectModule.builder(EidolonPotions.VULNERABLE_EFFECT.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))

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
                                          .eachLevel(10f))
                .addModule(new EffectImmunityModule(MobEffects.POISON))
                .addModule(new EffectImmunityModule(MobEffects.WITHER));
        buildModifier(Ids.eidolon_paladin_bone, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(MaterialRepairModule.material(DreamtinkerMaterialIds.PaladinBoneTool).constant(200))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.eidolon_bone_chill, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(MobEffectModule.builder(EidolonPotions.CHILLED_EFFECT.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))

                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        buildModifier(Ids.ashen_soul, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));
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
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));
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
                .addModule(ConditionalMeleeDamageModule.builder().target(fire_blast).eachLevel(2f))
                .addModule(ConditionalPowerModule.builder().target(fire_blast).eachLevel(0.1f))
                .addModule(MobEffectModule.builder(ModPotions.BLAST_EFFECT.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .chance(LevelingValue.eachLevel(0.3f))
                                          .target(LivingEntityPredicate.FIRE_IMMUNE.inverted())
                                          .build());
        buildModifier(Ids.nova_manipulation_essence, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"))
                .addModule(ConditionalMeleeDamageModule.builder().target(new HasMobEffectPredicate(ModPotions.GRAVITY_EFFECT.get())).eachLevel(2f))
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
                .addModule(ConditionalMeleeDamageModule.builder().target(cold_snap).eachLevel(2f))
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
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));
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
                .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1));
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Provider";
    }

}
