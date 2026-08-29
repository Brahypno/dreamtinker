package org.brahypno.dreamtinker.library.compat.goety;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.ritual.Ritual;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Executes the modifier application as a normal Goety ritual.
 */
public final class GoetyModifierRitual extends Ritual {
    public GoetyModifierRitual(GoetyModifierRitualRecipe recipe) {super(recipe);}

    private GoetyModifierRitualRecipe modifierRecipe() {return (GoetyModifierRitualRecipe) recipe;}

    @Override
    public boolean identify(Level level, BlockPos pos, Player player, ItemStack activation) {
        return super.identify(level, pos, player, activation)
               && GoetyModifierRitualTarget.find(activation, modifierRecipe()) != null;
    }

    /**
     * Returns a specific error only when this ritual otherwise matches the attempted activation.
     */
    @Nullable
    public Component identificationError(Level level, BlockPos pos, Player player, ItemStack activation) {
        if (!super.identify(level, pos, player, activation)){
            return null;
        }
        return GoetyModifierRitualTarget.check(activation, modifierRecipe()).error();
    }

    @Override
    public boolean isValid(
            Level level, BlockPos pos, DarkAltarBlockEntity altar, Player player,
            ItemStack activation, List<Ingredient> ingredients) {
        return GoetyModifierRitualTarget.find(activation, modifierRecipe()) != null
               && super.isValid(level, pos, altar, player, activation, ingredients);
    }

    @Override
    public void finish(Level level, BlockPos pos, DarkAltarBlockEntity altar, Player player, ItemStack activation) {
        GoetyModifierRitualTarget target = GoetyModifierRitualTarget.find(activation, modifierRecipe());
        if (target == null)
            return;
        ItemStack result = target.result();
        result.onCraftedBy(level, player, 1);
        super.finish(level, pos, altar, player, activation);
        altar.itemStackHandler.ifPresent(handler -> handler.setStackInSlot(0, result));
    }
}
