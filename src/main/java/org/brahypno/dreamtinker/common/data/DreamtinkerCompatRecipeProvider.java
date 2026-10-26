package org.brahypno.dreamtinker.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import elucent.eidolon.registries.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.brahypno.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/** Recipes with external JSON formats that have no builder in the project's dependencies. */
public class DreamtinkerCompatRecipeProvider implements DataProvider {
    private final PackOutput.PathProvider paths;

    public DreamtinkerCompatRecipeProvider(PackOutput output) {
        paths = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        BiConsumer<ResourceLocation, JsonObject> save = (id, json) -> {
            String modId = new ResourceLocation(json.get("type").getAsString()).getNamespace();
            JsonArray conditions = new JsonArray();
            conditions.add(CraftingHelper.serialize(DreamtinkerMaterialDataProvider.modLoaded(modId)));
            json.add("conditions", conditions);
            writes.add(DataProvider.saveStable(output, json, paths.json(id)));
        };
        addWorktableRecipes(save);
        addGlyphRecipe(save);
        addGrindingRecipes(save);
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void addWorktableRecipes(BiConsumer<ResourceLocation, JsonObject> save) {
        Map<Character, ItemLike> cloth = Map.of('t', Registry.TATTERED_CLOTH.get());
        save.accept(new ResourceLocation("eidolon", "bonelord_helm_part"),
                    worktable("helmet_plating", "helmet_plating_cast", new String[]{"bsb", "sws", "   "}, "ht t", cloth));
        save.accept(new ResourceLocation("eidolon", "bonelord_boots_part"),
                    worktable("boots_plating", "boots_plating_cast", new String[]{"   ", "sws", "b b"}, "ht t", cloth));
        save.accept(new ResourceLocation("eidolon", "bonelord_maille_part"),
                    worktable("maille", "maille_cast", new String[]{"   ", "sws", "b b"}, "ht t", cloth));
        save.accept(new ResourceLocation("eidolon", "bonelord_greaves_part"),
                    worktable("leggings_plating", "leggings_plating_cast", new String[]{"bsb", "sws", "s s"}, "hlal",
                              Map.of('a', Registry.ENCHANTED_ASH.get(), 'l', Registry.LESSER_SOUL_GEM.get())));
        save.accept(new ResourceLocation("eidolon", "bonelord_chestplate_part"),
                    worktable("chestplate_plating", "chestplate_plating_cast", new String[]{"svs", "bwb", "sbs"}, "htlt",
                              Map.of('v', item("tconstruct:chestplate_plating_cast"), 'w', Registry.WRAITH_HEART.get(),
                                     't', Registry.TATTERED_CLOTH.get(), 'l', Registry.LESSER_SOUL_GEM.get())));
    }

    private static JsonObject worktable(String part, String cast, String[] pattern, String reagents, Map<Character, ItemLike> extraKeys) {
        JsonObject json = recipe("eidolon:worktable");
        JsonArray rows = new JsonArray();
        for (String row : pattern)
            rows.add(row);
        json.add("pattern", rows);
        json.addProperty("reagents", reagents);

        Map<Character, ItemLike> ingredients = new LinkedHashMap<>();
        ingredients.put('b', Registry.IMBUED_BONES.get());
        ingredients.put('s', Registry.SILVER_INGOT.get());
        ingredients.put('h', Registry.HOLY_SYMBOL.get());
        ingredients.put('w', item("tconstruct:" + cast));
        ingredients.putAll(extraKeys);
        JsonObject keys = new JsonObject();
        ingredients.forEach((key, ingredient) -> keys.add(String.valueOf(key), Ingredient.of(ingredient).toJson()));
        json.add("key", keys);

        JsonObject result = new JsonObject();
        result.addProperty("item", "tconstruct:" + part);
        CompoundTag material = new CompoundTag();
        material.putString("Material", DreamtinkerMaterialIds.PaladinBone.toString());
        result.addProperty("nbt", material.toString());
        json.add("result", result);
        return json;
    }

    private static void addGlyphRecipe(BiConsumer<ResourceLocation, JsonObject> save) {
        ResourceLocation id = Dreamtinker.getLocation("glyph_tinker");
        GlyphRecipe recipe = new GlyphRecipe(id, new ItemStack(item("dreamtinker:glyph_tinker")), new ArrayList<>(), 55)
                .withItem(item("tconstruct:pattern"))
                .withItem(item("tconstruct:blank_sand_cast"));
        save.accept(id, recipe.asRecipe().getAsJsonObject());
    }

    private static void addGrindingRecipes(BiConsumer<ResourceLocation, JsonObject> save) {
        JsonObject ingredient = Ingredient.of(DreamtinkerCommon.transmutation_gold_ingot.get()).toJson().getAsJsonObject();
        JsonObject result = new JsonObject();
        result.addProperty("item", DreamtinkerCommon.transmutation_gold_dust.getId().toString());
        result.addProperty("count", 1);

        JsonObject thermal = recipe("thermal:pulverizer");
        thermal.add("ingredient", ingredient.deepCopy());
        thermal.add("result", array(result.deepCopy()));
        thermal.addProperty("experience", 0.0f);
        save.accept(Dreamtinker.getLocation("compat/thermal/pulverizer/transmutation_gold_ingot"), thermal);

        JsonObject create = recipe("create:crushing");
        create.add("ingredients", array(ingredient.deepCopy()));
        create.add("results", array(result.deepCopy()));
        create.addProperty("processingTime", 100);
        save.accept(Dreamtinker.getLocation("compat/create/crushing/transmutation_gold_ingot"), create);

        JsonObject immersive = recipe("immersiveengineering:crusher");
        immersive.addProperty("energy", 6000);
        immersive.add("input", ingredient.deepCopy());
        immersive.add("result", result.deepCopy());
        immersive.add("secondaries", new JsonArray());
        save.accept(Dreamtinker.getLocation("compat/immersiveengineering/crusher/transmutation_gold_ingot"), immersive);

        JsonObject mekanism = recipe("mekanism:crushing");
        JsonObject input = new JsonObject();
        input.add("ingredient", ingredient.deepCopy());
        mekanism.add("input", input);
        mekanism.add("output", result.deepCopy());
        save.accept(Dreamtinker.getLocation("compat/mekanism/crushing/transmutation_gold_ingot"), mekanism);

        JsonObject occultism = recipe("occultism:crushing");
        occultism.add("ingredient", ingredient.deepCopy());
        occultism.add("result", result.deepCopy());
        occultism.addProperty("crushing_time", 200);
        save.accept(Dreamtinker.getLocation("compat/occultism/crushing/transmutation_gold_ingot"), occultism);
    }

    private static JsonObject recipe(String type) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        return json;
    }

    private static JsonArray array(JsonObject value) {
        JsonArray array = new JsonArray();
        array.add(value);
        return array;
    }

    private static Item item(String name) {
        ResourceLocation id = new ResourceLocation(name);
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null || item == Items.AIR)
            throw new IllegalStateException("Missing datagen item: " + id);
        return item;
    }

    @Override
    public @NotNull String getName() {
        return "DreamTinker compatibility recipes";
    }
}
