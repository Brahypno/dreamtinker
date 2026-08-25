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

/**
 * A Goety ritual recipe whose result is derived from the altar's tool part at runtime.
 */
public final class GoetyMaterialTransmutationRecipe extends RitualRecipe {
    private final MaterialVariantId material;
    private final Ingredient unitInput;

    GoetyMaterialTransmutationRecipe(
            ResourceLocation id, String group, String craftType, ResourceLocation ritualType,
            Ingredient unitInput, int duration, int soulCost, MaterialVariantId material,
            String research) {
        super(id, group, craftType, ritualType, ItemStack.EMPTY, null, null, Ingredient.EMPTY,
              NonNullList.of(Ingredient.EMPTY, unitInput), duration, -1, soulCost,
              null, "", null, "", null, "", null, 0, research);
        this.material = material;
        this.unitInput = unitInput;
    }

    public MaterialVariantId material() {
        return material;
    }

    public Ingredient unitInput() {
        return unitInput;
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
