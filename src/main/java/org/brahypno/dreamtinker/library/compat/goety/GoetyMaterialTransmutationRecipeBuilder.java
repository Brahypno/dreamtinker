package org.brahypno.dreamtinker.library.compat.goety;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.brahypno.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Datagen builder for the dynamic Goety tool-part transmutation recipe.
 */
public final class GoetyMaterialTransmutationRecipeBuilder implements RecipeBuilder {
    private final MaterialVariantId material;
    private final Ingredient unitInput;
    private final List<MaterialVariantId> inputMaterials = new ArrayList<>();
    private int duration = 10;
    private int soulCost;
    private String group = "";
    private String craftType = "forge";
    private String research = "";

    private GoetyMaterialTransmutationRecipeBuilder(MaterialVariantId material, Ingredient unitInput) {
        this.material = Objects.requireNonNull(material, "material");
        this.unitInput = Objects.requireNonNull(unitInput, "unitInput");
    }

    public static GoetyMaterialTransmutationRecipeBuilder transmute(
            MaterialVariantId material, Ingredient unitInput) {
        return new GoetyMaterialTransmutationRecipeBuilder(material, unitInput);
    }

    public GoetyMaterialTransmutationRecipeBuilder duration(int duration) {
        if (duration <= 0){
            throw new IllegalArgumentException("duration must be positive");
        }
        this.duration = duration;
        return this;
    }

    public GoetyMaterialTransmutationRecipeBuilder soulCost(int soulCost) {
        if (soulCost < 0){
            throw new IllegalArgumentException("soulCost cannot be negative");
        }
        this.soulCost = soulCost;
        return this;
    }

    /**
     * Restricts the material that may be replaced. Calls preserve the given matching order.
     */
    public GoetyMaterialTransmutationRecipeBuilder inputMaterial(MaterialVariantId material) {
        inputMaterials.add(Objects.requireNonNull(material, "material"));
        return this;
    }

    public GoetyMaterialTransmutationRecipeBuilder inputMaterials(MaterialVariantId... materials) {
        for (MaterialVariantId material : materials) {
            inputMaterial(material);
        }
        return this;
    }

    public GoetyMaterialTransmutationRecipeBuilder craftType(String craftType) {
        this.craftType = Objects.requireNonNull(craftType, "craftType");
        return this;
    }

    /**
     * Requires the player to have read the Goety research scroll with this research ID.
     */
    public GoetyMaterialTransmutationRecipeBuilder research(String research) {
        this.research = Objects.requireNonNull(research, "research");
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, net.minecraft.advancements.CriterionTriggerInstance criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group == null ? "" : group;
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (unitInput.isEmpty()){
            throw new IllegalStateException("Unit input cannot be empty: " + id);
        }
        consumer.accept(new Result(id, material, unitInput, List.copyOf(inputMaterials), duration, soulCost,
                                   group, craftType, research));
    }

    private record Result(ResourceLocation id, MaterialVariantId material, Ingredient unitInput,
                          List<MaterialVariantId> inputMaterials,
                          int duration, int soulCost, String group, String craftType,
                          String research) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("material", material.toString());
            json.add("unit_input", unitInput.toJson());
            if (!inputMaterials.isEmpty()){
                JsonArray inputs = new JsonArray();
                inputMaterials.forEach(input -> inputs.add(input.toString()));
                json.add("input_materials", inputs);
            }
            json.addProperty("ritual_type", Dreamtinker.getLocation("tinker_material_transmutation").toString());
            json.addProperty("craftType", craftType);
            json.addProperty("duration", duration);
            json.addProperty("soulCost", soulCost);
            if (!research.isEmpty()){
                json.addProperty("research", research);
            }
            if (!group.isEmpty()){
                json.addProperty("group", group);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return GoetyTransmutationRegistry.SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
