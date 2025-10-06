package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class MalumHexStaff extends BattleModifier {
    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(itemstack.getItem())){
            return InteractionResult.PASS;
        }else {
            GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1.5f);
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
        InteractionHand hand = entity.getUsedItemHand();
        ItemRegistry.MNEMONIC_HEX_STAFF.get().onUseTick(entity.level(), entity, entity.getItemInHand(hand), timeLeft);
    }

    @Override
    public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
        ItemRegistry.MNEMONIC_HEX_STAFF.get().releaseUsing(entity.getItemInHand(entity.getUsedItemHand()), entity.level(), entity, timeLeft);
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 72000;
    }
}
