package org.brahypno.dreamtinker.library.compat.goety;

import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;

/**
 * A Goety ritual that keeps the activation tool and applies a Tinkers modifier.
 */
public final class GoetyModifierRitualRecipe extends RitualRecipe {
    private final Ingredient tools;
    private final MaterialVariantId requiredMaterial;
    private final ModifierId modifier;
    private final SlotCount slots;
    private final IntRange level;

    GoetyModifierRitualRecipe(
            ResourceLocation id, String group, String craftType,
            ResourceLocation ritualType, Ingredient tools,
            NonNullList<Ingredient> ingredients, int duration, int soulCost,
            MaterialVariantId requiredMaterial, ModifierId modifier, SlotCount slots,
            IntRange level, String research) {
        super(id, group, craftType, ritualType, ItemStack.EMPTY, null, null, tools,
              ingredients, duration, -1, soulCost, null, "", null, "", null, "", null, 0, research);
        this.tools = tools;
        this.requiredMaterial = requiredMaterial;
        this.modifier = modifier;
        this.slots = slots;
        this.level = level;
    }

    public Ingredient tools() {return tools;}

    public MaterialVariantId requiredMaterial() {return requiredMaterial;}

    public ModifierId modifier() {return modifier;}

    public SlotCount slots() {return slots;}

    public IntRange level() {return level;}

    @Override
    public RecipeSerializer<?> getSerializer() {return GoetyTransmutationRegistry.MODIFIER_SERIALIZER.get();}

    @Override
    public RecipeType<?> getType() {return ModRecipeSerializer.RITUAL_TYPE.get();}
}
