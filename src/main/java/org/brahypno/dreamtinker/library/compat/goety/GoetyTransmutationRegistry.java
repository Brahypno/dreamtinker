package org.brahypno.dreamtinker.library.compat.goety;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.ritual.ModRitualFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.brahypno.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;

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
    public static final RegistryObject<RecipeSerializer<GoetyModifierRitualRecipe>> MODIFIER_SERIALIZER =
            SERIALIZERS.register("goety_modifier_ritual", ModifierSerializer::new);
    public static final DeferredRegister<ModRitualFactory> RITUALS =
            DeferredRegister.create(Goety.location("ritual_factory"), Dreamtinker.MODID);
    public static final RegistryObject<ModRitualFactory> RITUAL = RITUALS.register(
            "tinker_material_transmutation",
            () -> new ModRitualFactory(recipe ->
                                               new GoetyMaterialTransmutationRitual((GoetyMaterialTransmutationRecipe) recipe)));
    public static final RegistryObject<ModRitualFactory> MODIFIER_RITUAL = RITUALS.register(
            "tinker_modifier", () -> new ModRitualFactory(recipe ->
                                                                  new GoetyModifierRitual((GoetyModifierRitualRecipe) recipe)));

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

    public static final class ModifierSerializer implements RecipeSerializer<GoetyModifierRitualRecipe> {
        @Override
        public GoetyModifierRitualRecipe fromJson(ResourceLocation id, JsonObject json) {
            MaterialVariantId material = MaterialVariantId.tryParse(GsonHelper.getAsString(json, "required_material"));
            if (material == null)
                throw new JsonParseException("Invalid required_material in " + id);
            var modifier = new slimeknights.tconstruct.library.modifiers.ModifierId(
                    new ResourceLocation(GsonHelper.getAsString(json, "modifier")));
            SlotCount slots = readSlots(id, json);
            IntRange level = readLevel(id, json);
            Ingredient tools = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "activation_item"));
            JsonArray inputJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            inputJson.forEach(element -> ingredients.add(Ingredient.fromJson(element)));
            return new GoetyModifierRitualRecipe(
                    id, GsonHelper.getAsString(json, "group", ""),
                    GsonHelper.getAsString(json, "craftType", "necroturgy"),
                    new ResourceLocation(GsonHelper.getAsString(json, "ritual_type", "dreamtinker:tinker_modifier")),
                    tools, ingredients, GsonHelper.getAsInt(json, "duration", 30),
                    GsonHelper.getAsInt(json, "soulCost", 1), material, modifier, slots, level,
                    GsonHelper.getAsString(json, "research", ""));
        }

        private static IntRange readLevel(ResourceLocation id, JsonObject json) {
            if (!json.has("level")){
                throw new JsonParseException("Missing level range in " + id);
            }
            try {
                if (json.get("level").isJsonObject()){
                    JsonObject range = GsonHelper.getAsJsonObject(json, "level");
                    int min = GsonHelper.getAsInt(range, "min", ModifierEntry.VALID_LEVEL.min());
                    int max = GsonHelper.getAsInt(range, "max", ModifierEntry.VALID_LEVEL.max());
                    return ModifierEntry.VALID_LEVEL.range(min, max);
                }
                return ModifierEntry.VALID_LEVEL.exactly(GsonHelper.getAsInt(json, "level"));
            }
            catch (IllegalArgumentException exception) {
                throw new JsonParseException("Invalid level range in " + id, exception);
            }
        }

        private static SlotCount readSlots(ResourceLocation id, JsonObject json) {
            JsonObject slotsJson = GsonHelper.getAsJsonObject(json, "slots");
            if (slotsJson.entrySet().size() != 1){
                throw new JsonParseException("Expected exactly one slot type in " + id);
            }
            var entry = slotsJson.entrySet().iterator().next();
            if (!SlotType.isValidName(entry.getKey())){
                throw new JsonParseException("Invalid slot type in " + id + ": " + entry.getKey());
            }
            int count = GsonHelper.convertToInt(entry.getValue(), "slots");
            if (count <= 0){
                throw new JsonParseException("Slot count must be positive in " + id);
            }
            return new SlotCount(SlotType.getOrCreate(entry.getKey()), count);
        }

        @Override
        public GoetyModifierRitualRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient tools = Ingredient.fromNetwork(buffer);
            MaterialVariantId material = MaterialVariantId.tryParse(buffer.readUtf());
            if (material == null)
                throw new IllegalArgumentException("Invalid synced material in " + id);
            var modifier = new slimeknights.tconstruct.library.modifiers.ModifierId(buffer.readResourceLocation());
            SlotCount slots = new SlotCount(SlotType.read(buffer), buffer.readVarInt());
            IntRange level = IntRange.fromNetwork(buffer);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(buffer.readVarInt(), Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buffer));
            return new GoetyModifierRitualRecipe(id, buffer.readUtf(), buffer.readUtf(), buffer.readResourceLocation(),
                                                 tools, ingredients, buffer.readVarInt(), buffer.readVarInt(), material, modifier, slots,
                                                 level, buffer.readUtf());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GoetyModifierRitualRecipe recipe) {
            recipe.tools().toNetwork(buffer);
            buffer.writeUtf(recipe.requiredMaterial().toString());
            buffer.writeResourceLocation(recipe.modifier());
            recipe.slots().type().write(buffer);
            buffer.writeVarInt(recipe.slots().count());
            recipe.level().toNetwork(buffer);
            buffer.writeVarInt(recipe.getIngredients().size());
            recipe.getIngredients().forEach(input -> input.toNetwork(buffer));
            buffer.writeUtf(recipe.getGroup());
            buffer.writeUtf(recipe.getCraftType());
            buffer.writeResourceLocation(recipe.getRitualType());
            buffer.writeVarInt(recipe.getDuration());
            buffer.writeVarInt(recipe.getSoulCost());
            buffer.writeUtf(recipe.getResearch());
        }
    }
}
