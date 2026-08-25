package org.brahypno.dreamtinker.plugin.JEI.goety;

import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialTransmutationRecipe;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

/**
 * One exact part type; valid input materials are expanded lazily by the category.
 */
public record GoetyTransmutationJeiDisplay(
        GoetyMaterialTransmutationRecipe recipe, ToolPartItem part, int cost) {}
