package org.brahypno.dreamtinker.library.compact.ars_nouveau;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ReactiveModifiableEnchantmentRecipeBuilder implements RecipeBuilder {
    private final List<Ingredient> pedestalItems = new ArrayList<>();
    private Ingredient tools = Ingredient.EMPTY;
    private int source = 0;

    @Nullable
    private SlotType.SlotCount slots;

    private boolean allowCrystal = false;
    private boolean checkTraitLevel = false;

    private ReactiveModifiableEnchantmentRecipeBuilder() {}

    public static ReactiveModifiableEnchantmentRecipeBuilder reactive() {
        return new ReactiveModifiableEnchantmentRecipeBuilder();
    }

    public ReactiveModifiableEnchantmentRecipeBuilder setTools(Ingredient tools) {
        this.tools = tools;
        return this;
    }

    public ReactiveModifiableEnchantmentRecipeBuilder setTools(TagKey<Item> tag) {
        return setTools(Ingredient.of(tag));
    }

    public ReactiveModifiableEnchantmentRecipeBuilder setTools(ItemLike item) {
        return setTools(Ingredient.of(item));
    }

    public ReactiveModifiableEnchantmentRecipeBuilder addPedestalItem(int count, Ingredient ingredient) {
        if (count < 1){
            throw new IllegalArgumentException("Pedestal item count must be at least 1");
        }

        for (int i = 0; i < count; i++) {
            addPedestalItem(ingredient);
        }

        return this;
    }

    public ReactiveModifiableEnchantmentRecipeBuilder addPedestalItem(int count, ItemLike item) {
        return addPedestalItem(count, Ingredient.of(item));
    }

    public ReactiveModifiableEnchantmentRecipeBuilder addPedestalItem(int count, TagKey<Item> tag) {
        return addPedestalItem(count, Ingredient.of(tag));
    }

    public ReactiveModifiableEnchantmentRecipeBuilder addPedestalItem(Ingredient ingredient) {
        this.pedestalItems.add(ingredient);
        return this;
    }

    public ReactiveModifiableEnchantmentRecipeBuilder addPedestalItem(ItemLike item) {
        return addPedestalItem(Ingredient.of(item));
    }

    public ReactiveModifiableEnchantmentRecipeBuilder addPedestalItem(TagKey<Item> tag) {
        return addPedestalItem(Ingredient.of(tag));
    }

    public ReactiveModifiableEnchantmentRecipeBuilder source(int source) {
        this.source = source;
        return this;
    }

    public ReactiveModifiableEnchantmentRecipeBuilder slot(SlotType type, int count) {
        this.slots = new SlotType.SlotCount(type, count);
        return this;
    }

    public ReactiveModifiableEnchantmentRecipeBuilder allowCrystal() {
        this.allowCrystal = true;
        return this;
    }

    public ReactiveModifiableEnchantmentRecipeBuilder checkTraitLevel() {
        this.checkTraitLevel = true;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (tools == Ingredient.EMPTY){
            throw new IllegalStateException("Must set applicable tools: " + id);
        }

        if (pedestalItems.isEmpty() && !allowCrystal){
            throw new IllegalStateException("Must have at least 1 pedestal item: " + id);
        }

        consumer.accept(new Result(
                id,
                tools,
                pedestalItems,
                source,
                slots,
                allowCrystal,
                checkTraitLevel
        ));
    }

    private record Result(ResourceLocation id, Ingredient tools, List<Ingredient> pedestalItems, int source, @Nullable SlotType.SlotCount slots,
                          boolean allowCrystal, boolean checkTraitLevel) implements FinishedRecipe {
        private Result(
                ResourceLocation id,
                Ingredient tools,
                List<Ingredient> pedestalItems,
                int source,
                @Nullable SlotType.SlotCount slots,
                boolean allowCrystal,
                boolean checkTraitLevel
        ) {
            this.id = id;
            this.tools = tools;
            this.pedestalItems = List.copyOf(pedestalItems);
            this.source = source;
            this.slots = slots;
            this.allowCrystal = allowCrystal;
            this.checkTraitLevel = checkTraitLevel;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("tools", tools.toJson());

            JsonArray pedestalArray = new JsonArray();
            for (Ingredient ingredient : pedestalItems) {
                pedestalArray.add(ingredient.toJson());
            }
            json.add("pedestalItems", pedestalArray);

            json.addProperty("source", source);

            if (slots != null){
                json.add("slots", SlotType.SlotCount.LOADABLE.serialize(slots));
            }

            if (allowCrystal){
                json.addProperty("allow_crystal", true);
            }

            if (checkTraitLevel){
                json.addProperty("check_trait_level", true);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return NovaRegistry.REACTIVE_MODIFIABLE_ENCHANTMENT_SERIALIZER.get();
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