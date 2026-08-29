package org.brahypno.dreamtinker.library.compat.goety;

import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.List;

/**
 * A Goety ritual recipe whose result is derived from the altar's tool part at runtime.
 */
public final class GoetyMaterialTransmutationRecipe extends RitualRecipe {
    private final MaterialVariantId material;
    private final Ingredient unitInput;
    private final List<MaterialVariantId> inputMaterials;

    GoetyMaterialTransmutationRecipe(
            ResourceLocation id, String group, String craftType, ResourceLocation ritualType,
            Ingredient unitInput, int duration, int soulCost, MaterialVariantId material,
            List<MaterialVariantId> inputMaterials, String research) {
        super(id, group, craftType, ritualType, ItemStack.EMPTY, null, null, Ingredient.EMPTY,
              NonNullList.of(Ingredient.EMPTY, unitInput), duration, -1, soulCost,
              null, "", null, "", null, "", null, 0, research);
        this.material = material;
        this.unitInput = unitInput;
        this.inputMaterials = List.copyOf(inputMaterials);
    }

    public MaterialVariantId material() {
        return material;
    }

    public Ingredient unitInput() {
        return unitInput;
    }

    /**
     * Empty means any material that can be replaced by the output material.
     */
    public List<MaterialVariantId> inputMaterials() {
        return inputMaterials;
    }

    public boolean acceptsInput(MaterialVariantId input) {
        return inputMaterials.isEmpty() || inputMaterials.stream().anyMatch(input::sameVariant);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GoetyTransmutationRegistry.SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializer.RITUAL_TYPE.get();
    }
}
