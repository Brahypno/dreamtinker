package org.dreamtinker.dreamtinker.tools.data;

import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.registries.EnigmaticEnchantments;
import com.sammy.malum.registry.common.AttributeRegistry;
import com.sammy.malum.registry.common.item.EnchantmentRegistry;
import elucent.eidolon.registries.EidolonAttributes;
import elucent.eidolon.registries.EidolonPotions;
import elucent.eidolon.registries.Registry;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidType;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.library.modifiers.modules.combat.MobEffectsRemoverModule;
import org.dreamtinker.dreamtinker.library.modifiers.modules.weapon.SwappableCircleWeaponAttack;
import org.dreamtinker.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPropertiesPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.damage.DamageTypePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.entity.MobTypePredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.variable.mining.BlockLightVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockTemperatureVariable;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.*;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.MaterialRepairModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.build.*;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.LootingModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MobEffectModule;
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

import static net.minecraft.tags.DamageTypeTags.BYPASSES_ENCHANTMENTS;
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
        buildModifier(Ids.antimony_usage).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                         .addModule(StatBoostModule.add(ToolStats.DURABILITY).eachLevel(0.1f))
                                         .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).eachLevel(0.1f))
                                         .addModule(StatBoostModule.add(ToolStats.ATTACK_SPEED).eachLevel(0.05f))
                                         .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.1f))
                                         .addModule(StatBoostModule.add(ToolStats.DRAW_SPEED).eachLevel(-0.1f))
                                         .addModule(StatBoostModule.add(ToolStats.ARMOR).eachLevel(0.1f))
                                         .addModule(StatBoostModule.add(ToolStats.ARMOR_TOUGHNESS).eachLevel(0.1f))
                                         .addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).eachLevel(0.1f))
                                         .addModule(StatBoostModule.add(ToolStats.BLOCK_AMOUNT).eachLevel(0.1f));
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
        // note chestplates will have both modules, but will get ignored due to setting the looting slot
        // the air check on weapon looting is for projectiles which use an item of air in their tool context
        LootingModule WEAPON_LOOTING = LootingModule.builder().toolItem(ItemPredicate.or(ItemPredicate.set(Items.AIR), ItemPredicate.tag(MELEE))).weapon();
        LootingModule ARMOR_LOOTING = LootingModule.builder().toolItem(armor).armor(ARMOR_SLOTS);
        buildModifier(Ids.with_tears)
                .addModules(CONSTANT_FORTUNE, ARMOR_FORTUNE, WEAPON_LOOTING, ARMOR_LOOTING)
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
        buildModifier(Ids.continuous_explode).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.soul_core)
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.soul_core).build());
        buildModifier(Ids.icy_memory)
                .levelDisplay(new ModifierLevelDisplay.UniqueForLevels(3))
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.icy_memory).build());
        buildModifier(Ids.hate_memory)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.hate_memory).build());
        buildModifier(Ids.huge_ego).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                   .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                   .addModules(ModifierSlotModule.slot(SlotType.UPGRADE).eachLevel(1))
                                   .addModule(StatBoostModule.multiplyAll(ToolStats.DURABILITY).flat(-0.25f));
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
                .addModule(StatBoostModule.add(ToolStats.ARMOR_TOUGHNESS).eachLevel(-1f));
        buildModifier(Ids.homunculusLifeCurse).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.homunculusGift).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.ophelia)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.peaches_in_memory)
                .addModule(AttributeModule.builder(TinkerAttributes.BAD_EFFECT_DURATION, AttributeModifier.Operation.MULTIPLY_TOTAL).amount(0.1f, 0.1f))
                .addModule(AttributeModule.builder(TinkerAttributes.EXPERIENCE_MULTIPLIER, AttributeModifier.Operation.MULTIPLY_TOTAL).amount(0.1f, 0.1f))
                .addModule(ReduceToolDamageModule.builder().maxLevel(5).formula()
                                                 .constant(0.025f).variable(LEVEL).multiply() // 0.025 * level
                                                 .constant(13).variable(LEVEL).subtract()     // 11 - level
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
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.the_romantic)
                .addModule(StatBoostModule.add(ToolStats.ATTACK_SPEED).eachLevel(0.2f))
                .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).eachLevel(-0.4f));
        buildModifier(Ids.all_slayer)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ConditionalMeleeDamageModule.builder().target(LivingEntityPredicate.ANY).eachLevel(1.5f));
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
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.lunarProtection)
                .addModule(DepthProtectionModule.builder().baselineHeight(40).neutralRange(0).eachLevel(-2.5f))
                .addModule(AttributeModule.builder(ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_BASE)
                                          .tooltipStyle(AttributeModule.TooltipStyle.PERCENT).flat(-0.05f));
        buildModifier(Ids.lunarAttractive)
                .addModule(MobEffectModule.builder(MobEffects.LEVITATION)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(ConditionalMeleeDamageModule.builder().target(TinkerPredicate.AIRBORNE.inverted()).eachLevel(-2f))
                .addModule(ConditionalMiningSpeedModule.builder().holder(LivingEntityPredicate.ON_GROUND.inverted()).percent().allowIneffective().flat(1),
                           ModifierHooks.BREAK_SPEED);
        buildModifier(Ids.lunarRejection)
                .addModule(ConditionalMeleeDamageModule.builder().target(TinkerPredicate.AIRBORNE).eachLevel(2f))
                .addModule(ConditionalMiningSpeedModule.builder().holder(LivingEntityPredicate.ON_GROUND.inverted()).percent().allowIneffective().flat(1),
                           ModifierHooks.BREAK_SPEED);
        buildModifier(Ids.requiem);
        buildModifier(Ids.slowness)
                .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 5))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);

        buildModifier(Ids.soul_unchanged)
                .addModules(ModifierSlotModule.slot(SlotType.SOUL).eachLevel(1))
                .addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
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

        IJsonPredicate<net.minecraft.world.entity.LivingEntity> ender = LivingEntityPredicate.tag(DreamtinkerTagKeys.EntityTypes.ENDER_ENTITY);
        buildModifier(Ids.ender_slayer, not(DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy")))
                .addModule(ConditionalMeleeDamageModule.builder().target(ender).eachLevel(2.0f))
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS)
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(ender).build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.heavy_arrow)
                .addModule(StatBoostModule.add(ToolStats.ACCURACY).eachLevel(-0.25f))
                .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.5f));
        buildModifier(Ids.light_arrow)
                .addModule(StatBoostModule.add(ToolStats.VELOCITY).eachLevel(0.5f))
                .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(-0.25f));
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
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT).levelDisplay(ModifierLevelDisplay.NO_LEVELS);

        addELModifiers();
        addMalumModifiers();
        addEidolonModifiers();
        addBICModifiers();
        addNovaModifiers();

    }

    private void addELModifiers() {

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


    }

    private void addEidolonModifiers() {
        buildModifier(Ids.eidolon_vulnerable, DreamtinkerMaterialDataProvider.modLoaded("eidolon"))
                .addModule(MobEffectModule.builder(EidolonPotions.VULNERABLE_EFFECT.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
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
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
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

        buildModifier(Ids.bic_frostbitten, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.BONE_CHILLING.get())
                                          .level(RandomLevelingValue.perLevel(0, 2))
                                          .time(RandomLevelingValue.random(180, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_intoxicating, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.INTOXICATION.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(110, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.POISON)
                                          .level(RandomLevelingValue.perLevel(0, 2))
                                          .time(RandomLevelingValue.random(60, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_life_stealer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.LIFESTEAL.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.STUN.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.WITHER)
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_krampus_horn, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.BONE_CHILLING.get())
                                          .level(RandomLevelingValue.flat(6))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_nightmare_claw, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MobEffectModule.builder(MobEffects.BLINDNESS)
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.DARKNESS)
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.WITHER)
                                          .level(RandomLevelingValue.flat(3))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.GAZE_OF_TERROR.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.MAGIC_DEPLETION.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);

        buildModifier(Ids.bic_infernal_ember, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.INFERNAL_FLAME.get())
                                          .level(RandomLevelingValue.perLevel(1, 1))
                                          .time(RandomLevelingValue.random(30, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        buildModifier(Ids.bic_hound_fang, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(MobEffectModule.builder(BornInChaosV1ModMobEffects.BONE_FRACTURE.get())
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.WEAKNESS)
                                          .level(RandomLevelingValue.flat(1))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT)
                .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN)
                                          .level(RandomLevelingValue.flat(2))
                                          .time(RandomLevelingValue.random(20, 10))
                                          .target(LivingEntityPredicate.ANY)
                                          .build(),
                           ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
    }

    private void addNovaModifiers() {
        buildModifier(Ids.nova_spell_tiers, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau")).levelDisplay(new ModifierLevelDisplay.UniqueForLevels(3));
        buildModifier(Ids.nova_creative_tiers, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau")).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Provider";
    }

}
