package org.brahypno.dreamtinker.library.compat.goety;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.brahypno.esotericismtinker.utils.PartInfoLookup;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.List;

/**
 * Resolves the exact material slot, part cost, and result shared by ritual execution and JEI.
 */
public record GoetyTransmutationTarget(ItemStack result, int cost) {
    public static GoetyTransmutationTarget find(
            Level level, ItemStack input, GoetyMaterialTransmutationRecipe recipe) {
        if (input.getCount() != 1){
            return null;
        }
        if (input.getItem() instanceof ToolPartItem part){
            if (!recipe.acceptsInput(part.getMaterial(input))
                || !part.canUseMaterial(recipe.material().getId())
                || !part.getStatType().canUseMaterial(recipe.material().getId())){
                return null;
            }
            int cost = PartInfoLookup.runtimeCost(level, part);
            if (!validCost(cost)){
                return null;
            }
            return new GoetyTransmutationTarget(
                    part.setMaterial(input.copyWithCount(1), recipe.material()), cost);
        }
        if (!(input.getItem() instanceof IModifiable modifiable)){
            return null;
        }

        ToolStack tool = ToolStack.from(input);
        MaterialNBT materials = tool.getMaterials();
        if (materials.isEmpty()){
            return null;
        }
        List<IToolPart> parts = ToolPartsHook.parts(modifiable.getToolDefinition());
        var statTypes = ToolMaterialHook.stats(modifiable.getToolDefinition());
        int size = Math.min(materials.size(), Math.min(parts.size(), statTypes.size()));
        for (int index = 0; index < size; index++) {
            if (!recipe.acceptsInput(materials.get(index).getVariant())
                || !statTypes.get(index).canUseMaterial(recipe.material().getId())){
                continue;
            }
            IToolPart definedPart = parts.get(index);
            if (!(definedPart.asItem() instanceof ToolPartItem part)
                || !part.canUseMaterial(recipe.material().getId())){
                continue;
            }
            int cost = PartInfoLookup.runtimeCost(level, part);
            if (!validCost(cost)){
                continue;
            }
            ItemStack result = input.copyWithCount(1);
            ToolStack resultTool = ToolStack.from(result);
            resultTool.replaceMaterial(index, recipe.material());
            resultTool.updateStack(result);
            return new GoetyTransmutationTarget(result, cost);
        }
        return null;
    }

    private static boolean validCost(int cost) {
        return cost > 0 && cost <= 12;
    }
}
