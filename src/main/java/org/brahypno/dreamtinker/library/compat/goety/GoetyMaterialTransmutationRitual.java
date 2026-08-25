package org.brahypno.dreamtinker.library.compat.goety;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.PedestalBlockEntity;
import com.Polarice3.Goety.common.ritual.Ritual;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.brahypno.esotericismtinker.utils.PartInfoLookup;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.List;

/**
 * Dynamic forge ritual: the altar part keeps its item type and receives the recipe material.
 */
public final class GoetyMaterialTransmutationRitual extends Ritual {
    public GoetyMaterialTransmutationRitual(GoetyMaterialTransmutationRecipe recipe) {
        super(recipe);
    }

    private GoetyMaterialTransmutationRecipe transmutation() {
        return (GoetyMaterialTransmutationRecipe) recipe;
    }

    private static int partCost(Level level, ItemStack stack) {
        return stack.getItem() instanceof ToolPartItem part ? PartInfoLookup.runtimeCost(level, part) : 0;
    }

    private boolean validCenter(ItemStack stack) {
        if (stack.getCount() != 1 || !(stack.getItem() instanceof ToolPartItem part)){
            return false;
        }
        return part.canUseMaterial(transmutation().material().getId())
               && part.getStatType().canUseMaterial(transmutation().material().getId());
    }

    private boolean validOfferings(Level level, BlockPos pos, int remaining) {
        if (remaining < 0 || remaining > 12){
            return false;
        }
        List<ItemStack> offerings = getItemsOnPedestals(level, pos);
        return offerings.size() == remaining
               && offerings.stream().allMatch(stack ->
                                                      stack.getCount() == 1 && transmutation().unitInput().test(stack));
    }

    @Override
    public boolean identify(Level level, BlockPos pos, Player player, ItemStack activation) {
        return validCenter(activation) && validOfferings(level, pos, partCost(level, activation));
    }

    @Override
    public boolean isValid(
            Level level, BlockPos pos, DarkAltarBlockEntity altar, Player player,
            ItemStack activation, List<Ingredient> ignored) {
        int required = partCost(level, activation);
        int consumed = altar.consumedIngredients.size();
        return required > 0 && validCenter(activation)
               && validOfferings(level, pos, required - consumed);
    }

    @Override
    public boolean consumeAdditionalIngredients(
            Level level, BlockPos pos, Player player, List<Ingredient> ignored,
            int time, List<ItemStack> consumed) {
        if (!(level.getBlockEntity(pos) instanceof DarkAltarBlockEntity altar)){
            return false;
        }
        ItemStack center = altar.itemStackHandler.map(handler -> handler.getStackInSlot(0)).orElse(ItemStack.EMPTY);
        int required = partCost(level, center);
        if (required <= 0 || required > 12){
            return false;
        }
        int duration = Math.max(1, transmutation().getDuration());
        int targetConsumed = Math.min(required, (int) Math.floor(time / (duration / (float) required)));
        if (consumed.size() >= targetConsumed){
            return true;
        }
        for (PedestalBlockEntity pedestal : getPedestals(level, pos)) {
            if (consumed.size() >= targetConsumed){
                break;
            }
            pedestal.itemStackHandler.ifPresent(handler ->
                                                        consumeAdditionalIngredientFromPedestal(
                                                                level, transmutation().unitInput(), pedestal, consumed, handler));
        }
        if (consumed.size() >= targetConsumed){
            return true;
        }
        player.displayClientMessage(Component.translatable("info.goety.ritual.cannotConsume.fail"), true);
        return false;
    }

    @Override
    public void finish(Level level, BlockPos pos, DarkAltarBlockEntity altar, Player player, ItemStack activation) {
        if (!(activation.getItem() instanceof ToolPartItem part)){
            return;
        }
        ItemStack result = part.setMaterial(activation.copyWithCount(1), transmutation().material());
        result.onCraftedBy(level, player, 1);
        super.finish(level, pos, altar, player, activation);
        altar.itemStackHandler.ifPresent(handler -> handler.setStackInSlot(0, result));
    }
}
