package org.brahypno.dreamtinker.library.compat.goety;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * Shared validation and output construction for ritual execution and JEI.
 */
public record GoetyModifierRitualTarget(ItemStack result) {
    public static GoetyModifierRitualTarget find(ItemStack input, GoetyModifierRitualRecipe recipe) {
        return check(input, recipe).target();
    }

    /**
     * Builds a JEI preview for a structurally valid tool. Missing display-only levels and slots are
     * supplied on a copy, while real ritual validation remains strict in {@link #check}.
     */
    public static GoetyModifierRitualTarget findForDisplay(ItemStack input, GoetyModifierRitualRecipe recipe) {
        if (!matchesToolAndMaterial(input, recipe))
            return null;
        ItemStack preview = input.copyWithCount(1);
        ToolStack tool = ToolStack.from(preview);
        int resultLevel = tool.getUpgrades().getLevel(recipe.modifier()) + 1;
        if (resultLevel > recipe.level().max())
            return null;
        if (resultLevel < recipe.level().min()){
            tool.addModifier(recipe.modifier(), recipe.level().min() - resultLevel);
        }
        var slots = recipe.slots();
        int missingSlots = slots.count() - tool.getFreeSlots(slots.type());
        if (missingSlots > 0){
            tool.getPersistentData().addSlots(slots.type(), missingSlots);
        }
        tool.updateStack(preview);
        return find(preview, recipe);
    }

    public static Check check(ItemStack input, GoetyModifierRitualRecipe recipe) {
        if (!matchesToolAndMaterial(input, recipe)){
            return Check.INVALID;
        }
        ToolStack tool = ToolStack.from(input);
        int resultLevel = tool.getUpgrades().getLevel(recipe.modifier()) + 1;
        if (resultLevel < recipe.level().min()){
            return new Check(null, Component.translatable(
                    "recipe.tconstruct.modifier.min_level",
                    ModifierManager.getValue(recipe.modifier()).getDisplayName(recipe.level().min() - 1)));
        }
        if (resultLevel > recipe.level().max()){
            return new Check(null, Component.translatable(
                    "recipe.tconstruct.modifier.max_level",
                    ModifierManager.getValue(recipe.modifier()).getDisplayName(), recipe.level().max()));
        }
        var slots = recipe.slots();
        if (tool.getFreeSlots(slots.type()) < slots.count()){
            Component error = slots.count() == 1
                              ? Component.translatable("recipe.tconstruct.modifier.not_enough_slot",
                                                       slots.type().getDisplayName())
                              : Component.translatable("recipe.tconstruct.modifier.not_enough_slots",
                                                       slots.count(), slots.type().getDisplayName());
            return new Check(null, error);
        }
        ItemStack result = input.copyWithCount(1);
        ToolStack output = ToolStack.from(result);
        output.getPersistentData().addSlots(slots.type(), -slots.count());
        output.addModifier(recipe.modifier(), 1);
        if (output.tryValidate() != null){
            return Check.INVALID;
        }
        output.updateStack(result);
        return new Check(new GoetyModifierRitualTarget(result), null);
    }

    private static boolean matchesToolAndMaterial(ItemStack input, GoetyModifierRitualRecipe recipe) {
        if (input.getCount() != 1 || !recipe.tools().test(input) || !(input.getItem() instanceof IModifiable)){
            return false;
        }
        return ToolStack.from(input).getMaterials().getList().stream()
                        .anyMatch(material -> material.getVariant().sameVariant(recipe.requiredMaterial()));
    }

    public record Check(@Nullable GoetyModifierRitualTarget target, @Nullable Component error) {
        private static final Check INVALID = new Check(null, null);
    }
}
