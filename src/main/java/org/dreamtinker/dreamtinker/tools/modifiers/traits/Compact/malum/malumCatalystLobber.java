package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.sammy.malum.common.item.curiosities.CatalystFlingerItem;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class malumCatalystLobber extends BattleModifier {
    @Override
    public @NotNull InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        InteractionResult res = ItemRegistry.CATALYST_LOBBER.get().use(player.level(), player, hand).getResult();
        if (InteractionResult.CONSUME == res){
            int state = player.getItemInHand(hand).getOrCreateTag().getInt(CatalystFlingerItem.STATE);
            if (0 == state)
                player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 100);
            else if (1 == state)
                player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 20);
        }
        return res;
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 72000;
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        ItemRegistry.CATALYST_LOBBER.get().inventoryTick(stack, world, holder, itemSlot, isSelected);
    }

}
