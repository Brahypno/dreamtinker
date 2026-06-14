package org.brahypno.dreamtinker.plugin.JEI;

import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.brahypno.dreamtinker.library.compact.ars_nouveau.ReactiveModifiableEnchantmentRecipe;
import org.brahypno.esotericismtinker.library.compact.ars_nouveau.recipe.ModifiableEnchantmentRecipe;
import org.brahypno.esotericismtinker.plugin.JEI.ModifiableEnchantmentCategory;

import java.util.ArrayList;
import java.util.List;

public class ArsJeiCompact {

    public static void registerRecipes(IRecipeRegistration registration) {
        if (!ModList.get().isLoaded("ars_nouveau")
            || !ModList.get().isLoaded("esotericism_tinker")){
            return;
        }

        Level level = Minecraft.getInstance().level;
        if (level == null){
            return;
        }

        List<ReactiveModifiableEnchantmentRecipe> recipes = level.getRecipeManager()
                                                                 .getAllRecipesFor(NovaRegistry.REACTIVE_MODIFIABLE_ENCHANTMENT_TYPE.get());

        registration.addRecipes(
                ModifiableEnchantmentCategory.RECIPE_TYPE,
                new ArrayList<ModifiableEnchantmentRecipe>(recipes)
        );
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        Item apparatus = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ars_nouveau", "enchanting_apparatus"));
        if (apparatus != null){
            registration.addRecipeCatalyst(
                    new ItemStack(apparatus),
                    ModifiableEnchantmentCategory.RECIPE_TYPE
            );
        }
    }
}
