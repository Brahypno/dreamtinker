package org.brahypno.dreamtinker.plugin.JEI.goety;

import com.Polarice3.Goety.api.ritual.RitualType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.research.ResearchScroll;
import com.Polarice3.Goety.compat.jei.JeiRecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialTransmutationRecipe;
import org.brahypno.dreamtinker.library.compat.goety.GoetyModifierRitualRecipe;
import org.brahypno.dreamtinker.library.compat.goety.GoetyModifierRitualTarget;
import org.brahypno.dreamtinker.library.compat.goety.GoetyTransmutationTarget;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
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
    private static volatile List<GoetyTransmutationJeiDisplay> registeredDisplays = List.of();

    /**
     * Display inputs per item and material. Building one is expensive and the same pair repeats for
     * every ritual, so it is built once and dropped when the material registry reloads.
     */
    private static final Map<DisplayInputKey, ItemStack> DISPLAY_INPUT_CACHE = new HashMap<>();

    static {
        RecipeCacheInvalidator.addReloadListener(client -> DISPLAY_INPUT_CACHE.clear());
    }

    private GoetyJeiCompat() {}

    public static void registerCategories(IRecipeCategoryRegistration registration, IGuiHelper gui) {
        registration.addRecipeCategories(new GoetyTransmutationCategory(gui));
    }

    public static void registerAdvanced(IAdvancedRegistration registration) {
        registration.addRecipeManagerPlugin(new FocusedRecipeManager());
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

        registeredDisplays = List.copyOf(displays);
        registration.addRecipes(RECIPE_TYPE, registeredDisplays);
    }

    public static void hideNativeRitualDisplays(IJeiRuntime runtime) {
        var level = Minecraft.getInstance().level;
        if (level == null)
            return;
        List<RitualRecipe> customRituals = level.getRecipeManager()
                                                .getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get()).stream()
                                                .filter(recipe -> recipe instanceof GoetyMaterialTransmutationRecipe
                                                                  || recipe instanceof GoetyModifierRitualRecipe)
                                                .toList();
        if (customRituals.isEmpty())
            return;
        var recipeManager = runtime.getRecipeManager();
        recipeManager.hideRecipes(JeiRecipeTypes.RITUAL, customRituals);
        for (var ritualType : RitualType.getAllRitualType()) {
            recipeManager.hideRecipes(JeiRecipeTypes.getRitual(ritualType.getName()), customRituals);
        }
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
        // only tool parts and tools can ever be a display input, so the cache stays small
        if (!(item instanceof ToolPartItem) && !(item instanceof IModifiable)){
            return ItemStack.EMPTY;
        }

        return DISPLAY_INPUT_CACHE.computeIfAbsent(new DisplayInputKey(item, material),
                                                   key -> buildDisplayInput(key.item(), key.material()));
    }

    private static ItemStack buildDisplayInput(Item item, MaterialVariantId material) {
        if (item instanceof ToolPartItem part){
            return part.canUseMaterial(material.getId()) ? part.withMaterial(material) : ItemStack.EMPTY;
        }
        if (item instanceof IModifiable modifiable){
            return ToolBuildHandler.createSingleMaterial(modifiable, MaterialVariant.of(material));
        }
        return ItemStack.EMPTY;
    }

    private record DisplayInputKey(Item item, MaterialVariantId material) {}

    private record DisplayKey(Item item, int cost) {}

    private static final class DisplayBuilder {
        private final List<ItemStack> inputs = new ArrayList<>();
        private final List<ItemStack> outputs = new ArrayList<>();

        private void add(ItemStack input, ItemStack output) {
            inputs.add(input);
            outputs.add(output);
        }
    }

    /**
     * Owns lookup for this dynamic category so static item-only matching cannot bypass material checks.
     */
    private static final class FocusedRecipeManager implements IRecipeManagerPlugin {
        @Override
        public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
            return matchingDisplays(focus).isEmpty() ? List.of() : List.of(RECIPE_TYPE);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T, V> List<T> getRecipes(IRecipeCategory<T> category, IFocus<V> focus) {
            if (!category.getRecipeType().equals(RECIPE_TYPE))
                return List.of();
            return (List<T>) matchingDisplays(focus);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> List<T> getRecipes(IRecipeCategory<T> category) {
            if (!category.getRecipeType().equals(RECIPE_TYPE))
                return List.of();
            return (List<T>) registeredDisplays;
        }
    }

    private static List<GoetyTransmutationJeiDisplay> matchingDisplays(IFocus<?> focus) {
        Object ingredient = focus.getTypedValue().getIngredient();
        RecipeIngredientRole role = focus.getRole();
        if (ingredient instanceof ItemStack stack){
            return registeredDisplays.stream()
                                     .filter(display -> matchesItemFocus(display, stack, role))
                                     .toList();
        }
        if (role == RecipeIngredientRole.OUTPUT && ingredient instanceof ModifierEntry modifier){
            return registeredDisplays.stream()
                                     .filter(display -> display.modifier() != null
                                                        && display.modifier().equals(modifier.getId()))
                                     .toList();
        }
        return List.of();
    }

    private static boolean matchesItemFocus(
            GoetyTransmutationJeiDisplay display, ItemStack stack, RecipeIngredientRole role) {
        return switch (role) {
            case INPUT -> isToolOrPart(stack)
                          ? acceptsFocusedInput(display, stack)
                          : display.pedestalInputs().stream().anyMatch(ingredient -> ingredient.test(stack));
            case OUTPUT -> display.outputs().stream().anyMatch(output -> output.is(stack.getItem()));
            case CATALYST -> stack.is(ModBlocks.DARK_ALTAR.get().asItem())
                             || matchesResearchScroll(display, stack);
            default -> false;
        };
    }

    private static boolean isToolOrPart(ItemStack stack) {
        return stack.getItem() instanceof IModifiable || stack.getItem() instanceof ToolPartItem;
    }

    private static boolean matchesResearchScroll(GoetyTransmutationJeiDisplay display, ItemStack stack) {
        return stack.getItem() instanceof ResearchScroll scroll && scroll.research != null
               && scroll.research.getId().equals(display.recipe().getResearch());
    }

    private static boolean acceptsFocusedInput(GoetyTransmutationJeiDisplay display, ItemStack focused) {
        if (display.inputs().stream().noneMatch(input -> input.is(focused.getItem())))
            return false;
        ItemStack input = focused.copyWithCount(1);
        if (display.recipe() instanceof GoetyModifierRitualRecipe modifierRecipe){
            return GoetyModifierRitualTarget.findForDisplay(input, modifierRecipe) != null;
        }
        if (display.recipe() instanceof GoetyMaterialTransmutationRecipe transmutation){
            var level = Minecraft.getInstance().level;
            if (level == null)
                return false;
            GoetyTransmutationTarget target = GoetyTransmutationTarget.find(level, input, transmutation);
            return target != null && target.cost() == display.pedestalInputs().size();
        }
        return false;
    }
}
