package org.brahypno.dreamtinker.plugin.JEI.goety;

import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialTransmutationRecipe;
import org.brahypno.esotericismtinker.utils.PartInfoLookup;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-only bridge that is called only when Goety is present.
 */
public final class GoetyJeiCompat {
    public static final RecipeType<GoetyTransmutationJeiDisplay> RECIPE_TYPE = RecipeType.create(
            Dreamtinker.MODID, "goety_material_transmutation", GoetyTransmutationJeiDisplay.class);

    private GoetyJeiCompat() {}

    public static void registerCategories(IRecipeCategoryRegistration registration, IGuiHelper gui) {
        registration.addRecipeCategories(new GoetyTransmutationCategory(gui));
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null){
            return;
        }

        List<PartCost> parts = new ArrayList<>();
        for (var item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof ToolPartItem part){
                int cost = PartInfoLookup.runtimeCost(level, part);
                if (cost > 0 && cost <= 12){
                    parts.add(new PartCost(part, cost));
                }
            }
        }

        List<GoetyTransmutationJeiDisplay> displays = new ArrayList<>();
        for (var ritual : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get())) {
            if (!(ritual instanceof GoetyMaterialTransmutationRecipe transmutation)){
                continue;
            }
            for (PartCost partCost : parts) {
                ToolPartItem part = partCost.part();
                if (part.canUseMaterial(transmutation.material().getId())
                    && part.getStatType().canUseMaterial(transmutation.material().getId())){
                    displays.add(new GoetyTransmutationJeiDisplay(transmutation, part, partCost.cost()));
                }
            }
        }

        GoetyTransmutationCategory.clearMaterialCache();
        registration.addRecipes(RECIPE_TYPE, displays);
    }

    private record PartCost(ToolPartItem part, int cost) {}
}
