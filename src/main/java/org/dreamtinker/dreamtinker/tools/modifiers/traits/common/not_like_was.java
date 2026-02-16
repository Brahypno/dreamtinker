package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.BasicInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.nbt.Tag.TAG_INT;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.UnbuildLimits;

public class not_like_was extends NoLevelsModifier implements BasicInterface {
    public static final ResourceLocation TAG_CHANGE_TIMES = Dreamtinker.getLocation("not_like_was_changing");

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.BasicInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        int times = Math.min(UnbuildLimits.get(), context.getPersistentData().getInt(TAG_CHANGE_TIMES));

        float armor_value = times * 0.5714f;
        float range_value = times * 0.23f;
        if (21 < times && context.hasTag(TinkerTags.Items.HARVEST))
            ToolStats.HARVEST_TIER.update(builder, Tiers.NETHERITE);
        else if (12 < times && context.hasTag(TinkerTags.Items.HARVEST))
            ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);

        ToolStats.ATTACK_DAMAGE.add(builder, times * 0.792f);
        ToolStats.ATTACK_SPEED.add(builder, times * 0.05f);
        ToolStats.MINING_SPEED.add(builder, times * 0.555f);
        ToolStats.ARMOR.add(builder, armor_value);
        ToolStats.ARMOR_TOUGHNESS.add(builder, armor_value);
        ToolStats.KNOCKBACK_RESISTANCE.add(builder, armor_value);
        ToolStats.DRAW_SPEED.add(builder, range_value);
        ToolStats.VELOCITY.add(builder, range_value);
        ToolStats.ACCURACY.add(builder, range_value);
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            if (nbt.contains(TAG_CHANGE_TIMES, TAG_INT)){
                int count = nbt.getInt(TAG_CHANGE_TIMES);
                if (UnbuildLimits.get() <= count)
                    tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.not_like_was_1").append(String.valueOf(count))
                                         .withStyle(this.getDisplayName().getStyle()));
                else
                    tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.not_like_was").append(String.valueOf(count))
                                         .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }
}
