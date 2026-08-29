package org.brahypno.dreamtinker.library.compat.goety;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.ritual.ModRitualFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.brahypno.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.ArrayList;
import java.util.List;

/**
 * Registrations limited to Goety's public ritual factory registry.
 */
public final class GoetyTransmutationRegistry {
    private GoetyTransmutationRegistry() {}

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Dreamtinker.MODID);
    public static final RegistryObject<RecipeSerializer<GoetyMaterialTransmutationRecipe>> SERIALIZER =
            SERIALIZERS.register("goety_material_transmutation", Serializer::new);
    public static final DeferredRegister<ModRitualFactory> RITUALS =
            DeferredRegister.create(Goety.location("ritual_factory"), Dreamtinker.MODID);
    public static final RegistryObject<ModRitualFactory> RITUAL = RITUALS.register(
            "tinker_material_transmutation",
            () -> new ModRitualFactory(recipe ->
                                               new GoetyMaterialTransmutationRitual((GoetyMaterialTransmutationRecipe) recipe)));

    public static final class Serializer implements RecipeSerializer<GoetyMaterialTransmutationRecipe> {
        @Override
        public GoetyMaterialTransmutationRecipe fromJson(ResourceLocation id, JsonObject json) {
            MaterialVariantId material = MaterialVariantId.tryParse(GsonHelper.getAsString(json, "material"));
            if (material == null){
                throw new JsonParseException("Invalid material in " + id);
            }
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "unit_input"));
            List<MaterialVariantId> inputMaterials = readInputMaterials(id, json);
            return new GoetyMaterialTransmutationRecipe(
                    id,
                    GsonHelper.getAsString(json, "group", ""),
                    GsonHelper.getAsString(json, "craftType", "forge"),
                    new ResourceLocation(GsonHelper.getAsString(
                            json, "ritual_type", "dreamtinker:tinker_material_transmutation")),
                    input,
                    GsonHelper.getAsInt(json, "duration", 100),
                    GsonHelper.getAsInt(json, "soulCost", 0),
                    material,
                    inputMaterials,
                    GsonHelper.getAsString(json, "research", ""));
        }

        private static List<MaterialVariantId> readInputMaterials(ResourceLocation recipeId, JsonObject json) {
            if (!json.has("input_materials")){
                return List.of();
            }
            JsonArray array = GsonHelper.getAsJsonArray(json, "input_materials");
            List<MaterialVariantId> materials = new ArrayList<>(array.size());
            array.forEach(element -> {
                MaterialVariantId material = MaterialVariantId.tryParse(GsonHelper.convertToString(element, "input_materials"));
                if (material == null){
                    throw new JsonParseException("Invalid input material in " + recipeId + ": " + element);
                }
                materials.add(material);
            });
            return List.copyOf(materials);
        }

        @Override
        public GoetyMaterialTransmutationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            MaterialVariantId material = MaterialVariantId.tryParse(buffer.readUtf());
            if (material == null){
                throw new IllegalArgumentException("Invalid synced material in " + id);
            }
            return new GoetyMaterialTransmutationRecipe(
                    id, buffer.readUtf(), buffer.readUtf(), buffer.readResourceLocation(),
                    Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readVarInt(), material,
                    buffer.readList(network -> {
                        MaterialVariantId input = MaterialVariantId.tryParse(network.readUtf());
                        if (input == null){
                            throw new IllegalArgumentException("Invalid synced input material in " + id);
                        }
                        return input;
                    }),
                    buffer.readUtf());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GoetyMaterialTransmutationRecipe recipe) {
            buffer.writeUtf(recipe.material().toString());
            buffer.writeUtf(recipe.getGroup());
            buffer.writeUtf(recipe.getCraftType());
            buffer.writeResourceLocation(recipe.getRitualType());
            recipe.unitInput().toNetwork(buffer);
            buffer.writeVarInt(recipe.getDuration());
            buffer.writeVarInt(recipe.getSoulCost());
            buffer.writeCollection(recipe.inputMaterials(), (network, input) -> network.writeUtf(input.toString()));
            buffer.writeUtf(recipe.getResearch());
        }
    }
}
