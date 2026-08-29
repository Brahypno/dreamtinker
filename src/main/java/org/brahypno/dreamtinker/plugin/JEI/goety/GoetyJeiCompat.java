package org.brahypno.dreamtinker.plugin.JEI.goety;

import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialTransmutationRecipe;
import org.brahypno.dreamtinker.library.compat.goety.GoetyModifierRitualRecipe;
import org.brahypno.dreamtinker.library.compat.goety.GoetyModifierRitualTarget;
import org.brahypno.dreamtinker.library.compat.goety.GoetyTransmutationTarget;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;

import java.util.*;

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

    /**
     * Adds ritual-only modifiers to Tinkers' searchable modifier ingredient type.
     */
    public static void registerExtraIngredients(IExtraIngredientRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null)
            return;
        List<ModifierEntry> modifiers = level.getRecipeManager()
                                             .getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get()).stream()
                                             .filter(GoetyModifierRitualRecipe.class::isInstance)
                                             .map(GoetyModifierRitualRecipe.class::cast)
                                             .map(recipe -> new ModifierEntry(ModifierManager.getValue(recipe.modifier()), 1))
                                             .distinct()
                                             .toList();
        if (!modifiers.isEmpty()){
            registration.addExtraIngredients(TConstructJEIConstants.MODIFIER_TYPE, modifiers);
        }
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null || !MaterialRegistry.isFullyLoaded()){
            return;
        }

        List<GoetyTransmutationJeiDisplay> displays = new ArrayList<>();
        for (var ritual : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get())) {
            if (!(ritual instanceof GoetyMaterialTransmutationRecipe transmutation)){
                continue;
            }
            List<MaterialVariantId> candidates = inputCandidates(transmutation);
            Map<DisplayKey, DisplayBuilder> grouped = new LinkedHashMap<>();
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                for (MaterialVariantId candidate : candidates) {
                    ItemStack input = displayInput(item, candidate);
                    if (input.isEmpty()){
                        continue;
                    }
                    GoetyTransmutationTarget target = GoetyTransmutationTarget.find(level, input, transmutation);
                    if (target != null){
                        grouped.computeIfAbsent(new DisplayKey(item, target.cost()), ignored -> new DisplayBuilder())
                               .add(input, target.result());
                    }
                }
            }
            grouped.forEach((key, display) -> displays.add(
                    new GoetyTransmutationJeiDisplay(
                            transmutation, List.copyOf(display.inputs), List.copyOf(display.outputs),
                            Collections.nCopies(key.cost, transmutation.unitInput()), null, null, null)));
        }

        for (var ritual : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get())) {
            if (!(ritual instanceof GoetyModifierRitualRecipe modifierRecipe))
                continue;
            List<ItemStack> inputs = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                if (!(item instanceof IModifiable modifiable))
                    continue;
                ItemStack input = ToolBuildHandler.createSingleMaterial(
                        modifiable, MaterialVariant.of(modifierRecipe.requiredMaterial()));
                if (input.isEmpty() || !modifierRecipe.tools().test(input))
                    continue;
                var tool = slimeknights.tconstruct.library.tools.nbt.ToolStack.from(input);
                int existingLevel = modifierRecipe.level().min() - 1;
                if (existingLevel > 0){
                    tool.addModifier(modifierRecipe.modifier(), existingLevel);
                }
                tool.getPersistentData().addSlots(modifierRecipe.slots().type(), modifierRecipe.slots().count());
                tool.updateStack(input);
                GoetyModifierRitualTarget target = GoetyModifierRitualTarget.find(input, modifierRecipe);
                if (target != null){
                    inputs.add(input);
                    outputs.add(target.result());
                }
            }
            if (!inputs.isEmpty()){
                displays.add(new GoetyTransmutationJeiDisplay(
                        modifierRecipe, List.copyOf(inputs), List.copyOf(outputs),
                        List.copyOf(modifierRecipe.getIngredients()), modifierRecipe.modifier(),
                        modifierRecipe.slots(), modifierRecipe.level()));
            }
        }

        registration.addRecipes(RECIPE_TYPE, displays);
    }

    private static List<MaterialVariantId> inputCandidates(GoetyMaterialTransmutationRecipe recipe) {
        if (!recipe.inputMaterials().isEmpty()){
            return recipe.inputMaterials();
        }
        return MaterialRegistry.getInstance().getVisibleMaterials().stream()
                               .map(material -> MaterialVariantId.create(material.getIdentifier(), ""))
                               .toList();
    }

    private static ItemStack displayInput(Item item, MaterialVariantId material) {
        if (item instanceof ToolPartItem part){
            return part.canUseMaterial(material.getId()) ? part.withMaterial(material) : ItemStack.EMPTY;
        }
        if (item instanceof IModifiable modifiable){
            return ToolBuildHandler.createSingleMaterial(modifiable, MaterialVariant.of(material));
        }
        return ItemStack.EMPTY;
    }

    private record DisplayKey(Item item, int cost) {}

    private static final class DisplayBuilder {
        private final List<ItemStack> inputs = new ArrayList<>();
        private final List<ItemStack> outputs = new ArrayList<>();

        private void add(ItemStack input, ItemStack output) {
            inputs.add(input);
            outputs.add(output);
        }
    }
}
