package org.brahypno.dreamtinker.mixin.compat.goety;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.library.compat.goety.GoetyModifierRitual;
import org.brahypno.dreamtinker.library.compat.goety.GoetyModifierRitualRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Replaces Goety's generic item error when a modifier ritual has a more specific failure.
 */
@Mixin(value = DarkAltarBlockEntity.class, remap = false)
public abstract class DarkAltarBlockEntityMixin {
    @Redirect(method = "activate", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V"),
            remap = true)
    private void dreamtinker$replaceModifierRitualError(
            Player receiver, Component message, boolean actionBar,
            Level level, BlockPos pos, Player player, InteractionHand hand, Direction direction) {
        if (message.getContents() instanceof TranslatableContents contents
            && contents.getKey().equals("info.goety.ritual.itemProblem.fail")){
            Component specific = dreamtinker$findModifierRitualError(level, pos, player, player.getItemInHand(hand));
            if (specific != null){
                message = specific;
            }
        }
        receiver.displayClientMessage(message, actionBar);
    }

    @Nullable
    private static Component dreamtinker$findModifierRitualError(
            Level level, BlockPos pos, Player player, ItemStack activation) {
        for (var recipe : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get())) {
            if (recipe instanceof GoetyModifierRitualRecipe modifierRecipe
                && modifierRecipe.getRitual() instanceof GoetyModifierRitual ritual){
                Component error = ritual.identificationError(level, pos, player, activation);
                if (error != null){
                    return error;
                }
            }
        }
        return null;
    }
}
