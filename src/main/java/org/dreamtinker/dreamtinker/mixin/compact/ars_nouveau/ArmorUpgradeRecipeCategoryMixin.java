package org.dreamtinker.dreamtinker.mixin.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.client.jei.ArmorUpgradeRecipeCategory;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.Item;
import org.dreamtinker.dreamtinker.tools.items.UnderArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

@Mixin(value = ArmorUpgradeRecipeCategory.class, remap = false)
public class ArmorUpgradeRecipeCategoryMixin {
    @ModifyReturnValue(
            method = "lambda$setRecipe$0*",
            at = @At("RETURN")
    )
    private static boolean dt$extendFilter(boolean original, ArmorUpgradeRecipe recipe, Item item) {
        if (original || !configCompactDisabled("ars_nouveau"))
            return original;

        return recipe.tier > 0 && item instanceof UnderArmorItem;
    }
}
