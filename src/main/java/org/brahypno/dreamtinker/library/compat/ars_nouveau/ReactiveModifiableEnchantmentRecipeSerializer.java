package org.brahypno.dreamtinker.library.compat.ars_nouveau;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.tools.SlotType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReactiveModifiableEnchantmentRecipeSerializer
        implements RecipeSerializer<ReactiveModifiableEnchantmentRecipe> {

    @Override
    public @NotNull ReactiveModifiableEnchantmentRecipe fromJson(
            @NotNull ResourceLocation recipeId,
            @NotNull JsonObject json
    ) {
        Ingredient tools = Ingredient.fromJson(GsonHelper.getNonNull(json, "tools"));

        JsonArray pedestalJson = GsonHelper.getAsJsonArray(json, "pedestalItems");
        List<Ingredient> pedestalItems = new ArrayList<>();
        for (JsonElement element : pedestalJson) {
            pedestalItems.add(Ingredient.fromJson(element));
        }

        int sourceCost = json.has("source")
                         ? GsonHelper.getAsInt(json, "source")
                         : GsonHelper.getAsInt(json, "sourceCost", 0);

        SlotType.SlotCount slots = null;
        if (json.has("slots")){
            slots = SlotType.SlotCount.LOADABLE.convert(json.get("slots"), "slots", TypedMap.EMPTY);
        }

        boolean allowCrystal = GsonHelper.getAsBoolean(json, "allow_crystal", false);
        boolean checkTraitLevel = GsonHelper.getAsBoolean(json, "check_trait_level", false);

        return new ReactiveModifiableEnchantmentRecipe(
                recipeId,
                tools,
                pedestalItems,
                sourceCost,
                slots,
                allowCrystal,
                checkTraitLevel
        );
    }

    @Override
    public @Nullable ReactiveModifiableEnchantmentRecipe fromNetwork(
            @NotNull ResourceLocation recipeId,
            @NotNull FriendlyByteBuf buffer
    ) {
        Ingredient tools = Ingredient.fromNetwork(buffer);

        int pedestalSize = buffer.readVarInt();
        List<Ingredient> pedestalItems = new ArrayList<>();
        for (int i = 0; i < pedestalSize; i++) {
            pedestalItems.add(Ingredient.fromNetwork(buffer));
        }

        int sourceCost = buffer.readVarInt();

        SlotType.SlotCount slots = null;
        if (buffer.readBoolean()){
            SlotType type = SlotType.read(buffer);
            int count = buffer.readVarInt();
            slots = new SlotType.SlotCount(type, count);
        }

        boolean allowCrystal = buffer.readBoolean();
        boolean checkTraitLevel = buffer.readBoolean();

        return new ReactiveModifiableEnchantmentRecipe(
                recipeId,
                tools,
                pedestalItems,
                sourceCost,
                slots,
                allowCrystal,
                checkTraitLevel
        );
    }

    @Override
    public void toNetwork(
            @NotNull FriendlyByteBuf buffer,
            @NotNull ReactiveModifiableEnchantmentRecipe recipe
    ) {
        recipe.getTools().toNetwork(buffer);

        List<Ingredient> pedestalItems = recipe.getPedestalIngredients();
        buffer.writeVarInt(pedestalItems.size());
        for (Ingredient ingredient : pedestalItems) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeVarInt(recipe.getSourceCost());

        SlotType.SlotCount slots = recipe.getSlots();
        buffer.writeBoolean(slots != null);
        if (slots != null){
            slots.type().write(buffer);
            buffer.writeVarInt(slots.count());
        }

        buffer.writeBoolean(recipe.allowCrystal());
        buffer.writeBoolean(recipe.checkTraitLevel());
    }
}