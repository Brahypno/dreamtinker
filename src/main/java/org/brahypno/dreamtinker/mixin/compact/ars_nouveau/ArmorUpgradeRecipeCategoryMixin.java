package org.brahypno.dreamtinker.mixin.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.client.jei.ArmorUpgradeRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;

import java.util.ArrayList;
import java.util.List;

import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

@Mixin(value = ArmorUpgradeRecipeCategory.class, remap = false)
public class ArmorUpgradeRecipeCategoryMixin {
    @ModifyVariable(
            method = "setRecipe*",
            at = @At("STORE"),
            name = "stacks")
    private List<ItemStack> dt$modifyStacks(List<ItemStack> original, IRecipeLayoutBuilder builder, ArmorUpgradeRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> mutable = new ArrayList<>(original);
        // 在这里加你的东西
        if (0 < recipe.tier && !configCompactDisabled("ars_nouveau"))
            mutable.addAll(DreamtinkerTools.underPlate.values().stream().map(IModifiableDisplay::getDisplayStack).toList());
        return mutable;
    }
}
