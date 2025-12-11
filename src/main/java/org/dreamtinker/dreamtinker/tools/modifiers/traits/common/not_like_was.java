package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.nbt.Tag.TAG_INT;

public class not_like_was extends BattleModifier {
    public static final ResourceLocation TAG_CHANGE_TIMES = Dreamtinker.getLocation("not_like_was_changing");

    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        float value = 0.05f + context.getPersistentData().getInt(TAG_CHANGE_TIMES) * 0.01f;
        float armor_value = 0.1f + context.getPersistentData().getInt(TAG_CHANGE_TIMES) * 0.01f;
        float range_value = 0.02f + context.getPersistentData().getInt(TAG_CHANGE_TIMES) * 0.01f;
        if (20 < context.getPersistentData().getInt(TAG_CHANGE_TIMES))
            ToolStats.HARVEST_TIER.update(builder, Tiers.NETHERITE);
        else if (10 < context.getPersistentData().getInt(TAG_CHANGE_TIMES))
            ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
        ToolStats.ATTACK_DAMAGE.multiply(builder, value);
        ToolStats.ATTACK_SPEED.multiply(builder, value);
        ToolStats.MINING_SPEED.multiply(builder, value / 5);
        ToolStats.ARMOR.multiply(builder, armor_value);
        ToolStats.ARMOR_TOUGHNESS.multiply(builder, armor_value);
        ToolStats.KNOCKBACK_RESISTANCE.multiply(builder, value);
        ToolStats.DRAW_SPEED.multiply(builder, range_value);
        ToolStats.VELOCITY.multiply(builder, range_value);
        ToolStats.ACCURACY.multiply(builder, range_value);
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            if (nbt.contains(TAG_CHANGE_TIMES, TAG_INT)){
                int count = nbt.getInt(TAG_CHANGE_TIMES);
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.not_like_was").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }
}
