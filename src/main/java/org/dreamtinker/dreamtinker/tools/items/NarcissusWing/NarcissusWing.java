package org.dreamtinker.dreamtinker.tools.items.NarcissusWing;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

import java.util.List;
import java.util.UUID;

public class NarcissusWing extends ModifiableItem {
    private static final String TAG_OWNER = Dreamtinker.MODID + "owner_uuid";
    public static final String TAG_OWNER_NAME = Dreamtinker.MODID + "OwnerName";

    public NarcissusWing(Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof Player player && isOwnerOrBypass(stack, player))
            super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        if (isOwnerOrBypass(stack, player))
            return super.onLeftClickEntity(stack, player, target);
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if (isOwnerOrBypass(stack, player))
            return super.onBlockStartBreak(stack, pos, player);
        return false;
    }

    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.hasUUID(TAG_OWNER) && SafeClientAccess.getTooltipKey().isShiftOrUnknown()){
            String name = tag.getString(TAG_OWNER_NAME);
            tooltip.add(Component.translatable("tooltip.narcissus_wing.desc1").append(name.isEmpty() ? tag.getUUID(TAG_OWNER).toString() : name)
                                 .withStyle(ChatFormatting.AQUA));
        }
    }

    public static boolean isOwnerOrBypass(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.hasUUID(TAG_OWNER) && !player.level().isClientSide){
            tag.putUUID(TAG_OWNER, player.getUUID());
            tag.putString(TAG_OWNER_NAME, player.getGameProfile().getName());
            return true; // 未绑定：允许首次绑定/使用（也可改为 false 并强制先右键绑定）
        }
        UUID owner = tag.getUUID(TAG_OWNER);
        // 允许创造模式/OP 绕过（按需）
        boolean bypass = player.isCreative();
        return bypass || player.getUUID().equals(owner);
    }
}
