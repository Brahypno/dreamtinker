package org.brahypno.dreamtinker.tools.modifiers.traits.material.livingSoulSteel;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;

public class AdaptionAlgorithm extends NoLevelsModifier
        implements ToolDamageModifierHook, InventoryTickModifierHook, TooltipModifierHook {
    private static final ResourceLocation TAG_ADAPTION =
            new ResourceLocation(Dreamtinker.MODID, "adaption_algorithm");

    /**
     * 0～9阶段：额外耐久磨损
     * 10阶段：正常耐久表现
     * 11～20阶段：持续自我修复
     */
    private static final int MAX_STAGE = 20;
    private static final int NEUTRAL_STAGE = 10;

    /**
     * 每累计100点原始耐久损伤提升一个阶段。
     */
    private static final int DAMAGE_PER_STAGE = 100;
    private static final int MAX_PROGRESS = MAX_STAGE * DAMAGE_PER_STAGE;

    /**
     * 后半段自我修复每秒结算一次。
     */
    private static final int REPAIR_INTERVAL_TICKS = 20;

    private static int getStage(int progress) {
        return Math.min(
                MAX_STAGE,
                Math.max(0, progress) / DAMAGE_PER_STAGE
        );
    }

    /**
     * 阶段0：额外磨损10点
     * 阶段1：额外磨损9点
     * ...
     * 阶段9：额外磨损1点
     * 阶段10及以上：无额外磨损
     */
    private static int getExtraWear(int stage) {
        return Math.max(0, NEUTRAL_STAGE - stage);
    }

    /**
     * 阶段0～10：不修复
     * 阶段11：每秒修复1点
     * ...
     * 阶段20：每秒修复10点
     */
    private static int getRepairPerSecond(int stage) {
        return Math.max(0, stage - NEUTRAL_STAGE);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(
                this,
                ModifierHooks.TOOL_DAMAGE,
                ModifierHooks.INVENTORY_TICK,
                ModifierHooks.TOOLTIP
        );

        super.registerHooks(hookBuilder);
    }

    @Override
    public int onDamageTool(
            IToolStackView tool,
            ModifierEntry modifier,
            int amount,
            @Nullable LivingEntity holder
    ) {
        ModDataNBT data = tool.getPersistentData();
        int progress = data.getInt(TAG_ADAPTION);

        /*
         * 只按照工具原本将要承受的耐久损伤累计适应进度。
         *
         * 强化自身额外造成的磨损不重复计入进度，
         * 否则前期会因为额外磨损导致适应速度失控。
         */
        if (amount > 0 && progress < MAX_PROGRESS){
            progress = Math.min(
                    MAX_PROGRESS,
                    progress + amount
            );

            data.putInt(TAG_ADAPTION, progress);
        }

        int stage = getStage(progress);
        int extraWear = getExtraWear(stage);

        return Math.max(0, amount + extraWear);
    }

    @Override
    public void onInventoryTick(
            IToolStackView tool,
            ModifierEntry modifier,
            Level world,
            LivingEntity holder,
            int itemSlot,
            boolean isSelected,
            boolean isCorrectSlot,
            ItemStack stack
    ) {
        if (world.isClientSide){
            return;
        }

        if (tool.getDamage() <= 0){
            return;
        }

        if (world.getGameTime() % REPAIR_INTERVAL_TICKS != 0){
            return;
        }

        int progress = tool.getPersistentData().getInt(TAG_ADAPTION);
        int stage = getStage(progress);
        int repairAmount = getRepairPerSecond(stage);

        /*
         * 阶段0～10不会进入修复阶段。
         */
        if (repairAmount <= 0){
            return;
        }

        tool.setDamage(
                Math.max(0, tool.getDamage() - repairAmount)
        );
    }

    @Override
    public void addTooltip(
            IToolStackView tool,
            @NotNull ModifierEntry modifier,
            @Nullable Player player,
            List<Component> tooltip,
            TooltipKey tooltipKey,
            TooltipFlag tooltipFlag
    ) {
        if (!tooltipKey.isShiftOrUnknown()){
            return;
        }

        int progress = Math.min(
                MAX_PROGRESS,
                Math.max(
                        0,
                        tool.getPersistentData().getInt(TAG_ADAPTION)
                )
        );

        int stage = getStage(progress);
        int extraWear = getExtraWear(stage);
        int repairPerSecond = getRepairPerSecond(stage);

        tooltip.add(
                Component.translatable(
                                 "modifier.dreamtinker.adaption_algorithm.tooltip.stage",
                                 stage,
                                 MAX_STAGE
                         )
                         .withStyle(this.getDisplayName().getStyle())
        );

        tooltip.add(
                Component.translatable(
                                 "modifier.dreamtinker.adaption_algorithm.tooltip.progress",
                                 progress,
                                 MAX_PROGRESS
                         )
                         .withStyle(ChatFormatting.GRAY)
        );

        if (extraWear > 0){
            tooltip.add(
                    Component.translatable(
                                     "modifier.dreamtinker.adaption_algorithm.tooltip.extra_wear",
                                     extraWear
                             )
                             .withStyle(ChatFormatting.RED)
            );
        }else if (repairPerSecond > 0){
            tooltip.add(
                    Component.translatable(
                                     "modifier.dreamtinker.adaption_algorithm.tooltip.repair_per_second",
                                     repairPerSecond
                             )
                             .withStyle(ChatFormatting.GREEN)
            );
        }else {
            tooltip.add(
                    Component.translatable(
                                     "modifier.dreamtinker.adaption_algorithm.tooltip.neutral"
                             )
                             .withStyle(ChatFormatting.YELLOW)
            );
        }
    }
}