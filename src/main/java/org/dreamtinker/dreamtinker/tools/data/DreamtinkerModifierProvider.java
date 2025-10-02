package org.dreamtinker.dreamtinker.tools.data;

import com.sammy.malum.registry.common.item.EnchantmentRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
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
        buildModifier(DreamtinkerModifiers.Ids.soul_form).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                                         .addModules(ModifierSlotModule.slot(SlotType.ABILITY).flat(1),
                                                                     ModifierSlotModule.slot(SlotType.DEFENSE).flat(1),
                                                                     ModifierSlotModule.slot(SlotType.SOUL).flat(1),
                                                                     ModifierSlotModule.slot(SlotType.UPGRADE).flat(1));
        buildModifier(DreamtinkerModifiers.Ids.wither_body).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
        buildModifier(DreamtinkerModifiers.Ids.soul_upgrade).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                                            .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                                                            .addModules(ModifierSlotModule.slot(SlotType.SOUL).flat(1));
        buildModifier(DreamtinkerModifiers.Ids.continuous_explode).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(DreamtinkerModifiers.Ids.moonlight_ice_info).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        buildModifier(DreamtinkerModifiers.Ids.soul_core)
                .addModule(ModifierRequirementsModule.builder().requireModifier(DreamtinkerModifiers.memory_base.getId(), 1)
                                                     .modifierKey(DreamtinkerModifiers.Ids.soul_core).build());
        buildModifier(DreamtinkerModifiers.Ids.icy_memory)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ModifierRequirementsModule.builder().requireModifier(DreamtinkerModifiers.memory_base.getId(), 1)
                                                     .modifierKey(DreamtinkerModifiers.Ids.icy_memory).build());
        buildModifier(DreamtinkerModifiers.Ids.hate_memory)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(ModifierRequirementsModule.builder().requireModifier(DreamtinkerModifiers.memory_base.getId(), 1)
                                                     .modifierKey(DreamtinkerModifiers.Ids.hate_memory).build());
        buildModifier(DreamtinkerModifiers.Ids.huge_ego).tooltipDisplay(BasicModifier.TooltipDisplay.TINKER_STATION)
                                                        .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                                                        .addModules(ModifierSlotModule.slot(SlotType.UPGRADE).eachLevel(1));
        buildModifier(DreamtinkerModifiers.Ids.full_concentration).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL);
        addMalumModifiers();

    }

    private void addMalumModifiers() {
        buildModifier(DreamtinkerModifiers.Ids.malum_rebound, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.REBOUND.get()).level(1).constant())
                .addModule(ModifierRequirementsModule.builder().requireModifier(DreamtinkerModifiers.malum_base.getId(), 1)
                                                     .requirement(HasModifierPredicate.hasModifier(DreamtinkerModifiers.Ids.malum_ascension, 1).inverted())
                                                     .modifierKey(DreamtinkerModifiers.Ids.malum_rebound).build());
        buildModifier(DreamtinkerModifiers.Ids.malum_ascension, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.ASCENSION.get()).level(1).constant())
                .addModule(ModifierRequirementsModule.builder().requireModifier(DreamtinkerModifiers.malum_base.getId(), 1)
                                                     .requirement(HasModifierPredicate.hasModifier(DreamtinkerModifiers.Ids.malum_rebound, 1).inverted())
                                                     .modifierKey(DreamtinkerModifiers.Ids.malum_ascension).build());
        buildModifier(DreamtinkerModifiers.Ids.malum_animated, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.ANIMATED.get()).level(2).constant())
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(DreamtinkerModifiers.Ids.malum_haunted, 1).inverted())
                                                     .modifierKey(DreamtinkerModifiers.Ids.malum_animated).build());
        buildModifier(DreamtinkerModifiers.Ids.malum_haunted, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.HAUNTED.get()).level(3).constant())
                .addModule(ModifierRequirementsModule.builder()
                                                     .requirement(HasModifierPredicate.hasModifier(DreamtinkerModifiers.Ids.malum_animated, 1).inverted())
                                                     .modifierKey(DreamtinkerModifiers.Ids.malum_haunted).build());
        buildModifier(DreamtinkerModifiers.Ids.malum_spirit_plunder, modLoaded("malum"))
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(EnchantmentModule.builder(EnchantmentRegistry.SPIRIT_PLUNDER.get()).level(2).constant());
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Provider";
    }

}
