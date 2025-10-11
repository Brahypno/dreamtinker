package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.sammy.malum.common.capability.MalumPlayerDataCapability;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class MalumHexStaff extends BattleModifier {
    private final boolean Erosion;

    public MalumHexStaff(boolean erosion) {
        this.Erosion = erosion;
    }

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
        if (!this.Erosion){
            if (1 < modifier.getLevel())
                ItemRegistry.STAFF_OF_THE_AURIC_FLAME.get().onUseTick(entity.level(), entity, entity.getItemInHand(hand), timeLeft);
            else
                ItemRegistry.MNEMONIC_HEX_STAFF.get().onUseTick(entity.level(), entity, entity.getItemInHand(hand), timeLeft);
        }else
            ItemRegistry.EROSION_SCEPTER.get().onUseTick(entity.level(), entity, entity.getItemInHand(hand), timeLeft);
    }

    @Override
    public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
        int charge = 0;
        ItemStack staff = entity.getItemInHand(entity.getUsedItemHand());
        if (entity instanceof Player player){
            MalumPlayerDataCapability capability = MalumPlayerDataCapability.getCapability(player);
            charge = capability.reserveStaffChargeHandler.chargeCount;
        }
        if (!this.Erosion){
            if (1 < modifier.getLevel())
                for (int i = 0; i < modifier.getLevel() - 1; i++)
                    ItemRegistry.STAFF_OF_THE_AURIC_FLAME.get()
                                                         .releaseUsing(staff, entity.level(), entity, timeLeft);
            else
                ItemRegistry.MNEMONIC_HEX_STAFF.get().releaseUsing(staff, entity.level(), entity, timeLeft);
        }else
            for (int i = 0; i < modifier.getLevel(); i++)
                ItemRegistry.EROSION_SCEPTER.get().releaseUsing(staff, entity.level(), entity, timeLeft);

        if (entity instanceof Player player){
            player.awardStat(Stats.ITEM_USED.get(staff.getItem()));
            if (!player.getAbilities().instabuild){
                if (charge <= 0)//reserveStaff consumed in above releaseUsing, this is used to add cooldown.
                    player.getCooldowns()
                          .addCooldown(staff.getItem(), this.Erosion || modifier.getLevel() < 2 ? 80 : 160);
                else {
                    MalumPlayerDataCapability capability = MalumPlayerDataCapability.getCapability(player);
                    capability.reserveStaffChargeHandler.chargeCount = --charge;
                }
            }
        }
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 72000;
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        return this.Erosion || level < 2 ? super.getDisplayName() :
               Component.translatable("modifier.dreamtinker.malum_hex_staff_1").withStyle((style) -> style.withColor(this.getTextColor()));
    }
}
