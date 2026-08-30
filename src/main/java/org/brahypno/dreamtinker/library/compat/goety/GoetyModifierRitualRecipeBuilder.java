package org.brahypno.dreamtinker.library.compat.goety;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Datagen builder for a Goety ritual that applies a modifier to its center tool.
 */
public final class GoetyModifierRitualRecipeBuilder {
    private final Ingredient tools;
    private final MaterialVariantId material;
    private final ModifierId modifier;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private int duration = 30;
    private int soulCost = 1;
    private String craftType;
    private SlotCount slots;
    private IntRange level;

    private GoetyModifierRitualRecipeBuilder(Ingredient tools, MaterialVariantId material, ModifierId modifier) {
        this.tools = tools;
        this.material = material;
        this.modifier = modifier;
    }

    public static GoetyModifierRitualRecipeBuilder modifier(Ingredient tools, MaterialVariantId material, ModifierId modifier) {
        return new GoetyModifierRitualRecipeBuilder(tools, material, modifier);
    }

    public GoetyModifierRitualRecipeBuilder addInput(Ingredient input) {
        ingredients.add(input);
        return this;
    }

    public GoetyModifierRitualRecipeBuilder duration(int value) {
        duration = value;
        return this;
    }

    public GoetyModifierRitualRecipeBuilder soulCost(int value) {
        soulCost = value;
        return this;
    }

    public GoetyModifierRitualRecipeBuilder craftType(String value) {
        Objects.requireNonNull(value, "craftType");
        if (value.isBlank())
            throw new IllegalArgumentException("craftType cannot be blank");
        craftType = value;
        return this;
    }

    public GoetyModifierRitualRecipeBuilder slots(SlotType type, int count) {
        if (count <= 0)
            throw new IllegalArgumentException("Slot count must be positive");
        slots = new SlotCount(type, count);
        return this;
    }

    public GoetyModifierRitualRecipeBuilder level(int value) {
        level = ModifierEntry.VALID_LEVEL.exactly(value);
        return this;
    }

    public GoetyModifierRitualRecipeBuilder level(int min, int max) {
        level = ModifierEntry.VALID_LEVEL.range(min, max);
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (ingredients.isEmpty() || ingredients.size() > 12)
            throw new IllegalStateException("Goety ritual needs 1-12 inputs: " + id);
        if (slots == null)
            throw new IllegalStateException("Goety modifier ritual needs a slot cost: " + id);
        if (level == null)
            throw new IllegalStateException("Goety modifier ritual needs a level range: " + id);
        if (craftType == null)
            throw new IllegalStateException("Goety modifier ritual needs a craftType: " + id);
        consumer.accept(new Result(id, tools, material, modifier, slots, level,
                                   List.copyOf(ingredients), duration, soulCost, craftType));
    }

    private record Result(ResourceLocation id, Ingredient tools, MaterialVariantId material, ModifierId modifier,
                          SlotCount slots, IntRange level,
                          List<Ingredient> ingredients, int duration, int soulCost, String craftType) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("ritual_type", "dreamtinker:tinker_modifier");
            json.addProperty("craftType", craftType);
            json.addProperty("soulCost", soulCost);
            json.addProperty("duration", duration);
            json.add("activation_item", tools.toJson());
            json.addProperty("required_material", material.toString());
            json.addProperty("modifier", modifier.toString());
            json.add("level", ModifierEntry.VALID_LEVEL.serialize(level));
            JsonObject slotJson = new JsonObject();
            slotJson.addProperty(slots.type().getName(), slots.count());
            json.add("slots", slotJson);
            JsonArray array = new JsonArray();
            ingredients.forEach(input -> array.add(input.toJson()));
            json.add("ingredients", array);
        }

        @Override
        public ResourceLocation getId() {return id;}

        @Override
        public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() {return GoetyTransmutationRegistry.MODIFIER_SERIALIZER.get();}

        @Override
        @Nullable
        public JsonObject serializeAdvancement() {return null;}

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {return null;}
    }
}
