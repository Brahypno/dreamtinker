package org.dreamtinker.dreamtinker.data.providers.tinker;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier;
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
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker";
    }
}
