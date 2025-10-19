package org.dreamtinker.dreamtinker.tools.data;

import com.aizistral.enigmaticlegacy.registries.EnigmaticEnchantments;
import com.sammy.malum.registry.common.item.EnchantmentRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.variable.mining.BlockLightVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockTemperatureVariable;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modules.MeltingModule;

import static org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers.*;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.*;

public class DreamtinkerModifierProvider extends AbstractModifierProvider implements IConditionBuilder {
    public DreamtinkerModifierProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addModifiers() {
        buildModifier(Ids.soul_form).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                    .addModules(ModifierSlotModule.slot(SlotType.ABILITY).flat(1),
                                                ModifierSlotModule.slot(SlotType.DEFENSE).flat(1),
                                                ModifierSlotModule.slot(SlotType.SOUL).flat(1),
                                                ModifierSlotModule.slot(SlotType.UPGRADE).flat(1));
        buildModifier(Ids.wither_body).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.soul_upgrade).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                       .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                       .addModules(ModifierSlotModule.slot(SlotType.SOUL).flat(1));
        buildModifier(Ids.continuous_explode).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.moonlight_ice_info).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.soul_core)
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.soul_core).build());
        buildModifier(Ids.icy_memory)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.icy_memory).build());
        buildModifier(Ids.hate_memory)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                     .modifierKey(Ids.hate_memory).build());
        buildModifier(Ids.huge_ego).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                   .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                   .addModules(ModifierSlotModule.slot(SlotType.UPGRADE).eachLevel(1));
        buildModifier(Ids.full_concentration).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.thundering_curse).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(Ids.why_i_cry).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
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

        addELModifiers();
        addMalumModifiers();

    }

    private void addELModifiers() {

        buildModifier(Ids.el_nemesis_curse, modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.NEMESIS).level(1).constant());
        buildModifier(Ids.el_sorrow, modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.SORROW).level(1).constant());
        buildModifier(Ids.el_eternal_binding, modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.ETERNAL_BINDING).level(1).constant());
        buildModifier(Ids.el_slayer, modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.SLAYER).level(1).constant());
        buildModifier(Ids.el_wrath, modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.WRATH).level(1).constant())
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.el_torrent, 1).inverted())
                                                     .modifierKey(Ids.el_wrath).build());
        buildModifier(Ids.el_torrent, modLoaded("enigmaticlegacy"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnigmaticEnchantments.TORRENT).level(1).constant())
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.el_wrath, 1).inverted())
                                                     .modifierKey(Ids.el_torrent).build());
    }

    private void addMalumModifiers() {
        buildModifier(Ids.malum_rebound, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.REBOUND.get()).level(1).constant())
                .addModule(ModifierRequirementsModule.builder().requireModifier(malum_base.getId(), 1)
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.malum_ascension, 1).inverted())
                                                     .modifierKey(Ids.malum_rebound).build());
        buildModifier(Ids.malum_ascension, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.ASCENSION.get()).level(1).constant())
                .addModule(ModifierRequirementsModule.builder().requireModifier(malum_base.getId(), 1)
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.malum_rebound, 1).inverted())
                                                     .modifierKey(Ids.malum_ascension).build());
        buildModifier(Ids.malum_animated, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.ANIMATED.get()).level(2).constant())
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.malum_haunted, 1).inverted())
                                                     .modifierKey(Ids.malum_animated).build());
        buildModifier(Ids.malum_haunted, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.HAUNTED.get()).level(2).constant())
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(Ids.malum_animated, 1).inverted())
                                                     .modifierKey(Ids.malum_haunted).build());
        buildModifier(Ids.malum_spirit_plunder, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.SPIRIT_PLUNDER.get()).level(2).constant());

        buildModifier(Ids.malum_tyrving, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(StatBoostModule.multiplyAll(ToolStats.ATTACK_DAMAGE).flat(-0.4f));
        buildModifier(Ids.malum_world_of_weight, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.malum_edge_of_deliverance, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(Ids.malum_sol_tiferet, not(modLoaded("malum")))
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Provider";
    }

}
