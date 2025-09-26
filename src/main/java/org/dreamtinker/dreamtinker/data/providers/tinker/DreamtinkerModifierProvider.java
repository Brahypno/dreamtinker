package org.dreamtinker.dreamtinker.data.providers.tinker;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.SlotType;

public class DreamtinkerModifierProvider extends AbstractModifierProvider implements IConditionBuilder {
    public DreamtinkerModifierProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addModifiers() {
        buildModifier(DreamtinkerModifers.Ids.soul_form).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                                        .addModules(ModifierSlotModule.slot(SlotType.ABILITY).flat(1),
                                                                    ModifierSlotModule.slot(SlotType.DEFENSE).flat(1),
                                                                    ModifierSlotModule.slot(SlotType.SOUL).flat(1),
                                                                    ModifierSlotModule.slot(SlotType.UPGRADE).flat(1));
        buildModifier(DreamtinkerModifers.Ids.wither_body).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(DreamtinkerModifers.Ids.soul_upgrade).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                                           .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                                           .addModules(ModifierSlotModule.slot(SlotType.SOUL).flat(1));
        buildModifier(DreamtinkerModifers.Ids.continuous_explode).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(DreamtinkerModifers.Ids.moonlight_ice_info).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(DreamtinkerModifers.Ids.soul_core)
                .addModule(ModifierRequirementsModule.builder().requireModifier(DreamtinkerModifers.memory_base.getId(), 1)
                                                     .modifierKey(DreamtinkerModifers.Ids.soul_core).build());
        buildModifier(DreamtinkerModifers.Ids.icy_memory)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ModifierRequirementsModule.builder().requireModifier(DreamtinkerModifers.memory_base.getId(), 1)
                                                     .modifierKey(DreamtinkerModifers.Ids.icy_memory).build());
        buildModifier(DreamtinkerModifers.Ids.hate_memory)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ModifierRequirementsModule.builder().requireModifier(DreamtinkerModifers.memory_base.getId(), 1)
                                                     .modifierKey(DreamtinkerModifers.Ids.hate_memory).build());
        buildModifier(DreamtinkerModifers.Ids.huge_ego).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                                       .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                                       .addModules(ModifierSlotModule.slot(SlotType.UPGRADE).eachLevel(1));
        buildModifier(DreamtinkerModifers.Ids.full_concentration).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Provider";
    }
}
