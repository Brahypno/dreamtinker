package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;

public class range_accelerator extends Modifier
        implements ProjectileLaunchModifierHook, TooltipModifierHook {
    private final @Nullable TagKey<Item> consumeItemTagKey;
    private final @Nullable Item consumeItem;

    public range_accelerator(@NotNull TagKey<Item> itemTagKey) {
        this.consumeItemTagKey = itemTagKey;
        this.consumeItem = null;
    }

    public range_accelerator(@NotNull Item item) {
        this.consumeItem = item;
        this.consumeItemTagKey = null;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(
                this,
                ModifierHooks.PROJECTILE_LAUNCH,
                ModifierHooks.TOOLTIP
        );
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onProjectileLaunch(
            IToolStackView tool,
            ModifierEntry modifier,
            LivingEntity shooter,
            Projectile projectile,
            @Nullable AbstractArrow arrow,
            ModDataNBT persistentData,
            boolean primary
    ) {
        if (!(shooter instanceof Player player) || player.level().isClientSide){
            return;
        }

        int level = modifier.getLevel();
        int needed = getRequiredCount(level);
        if (!consumeRequiredItems(player, needed)){
            return;
        }

        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(level));
        if (projectile instanceof AbstractArrow launchedArrow){
            launchedArrow.setBaseDamage(launchedArrow.getBaseDamage() + 0.5D * level);
        }
    }

    private int getRequiredCount(int level) {
        long required = (long) level * level;
        return required >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) required;
    }

    private boolean matches(ItemStack stack) {
        return (consumeItem != null && stack.is(consumeItem))
               || (consumeItemTagKey != null && stack.is(consumeItemTagKey));
    }

    private int countAvailable(Player player) {
        long total = 0L;

        for (ItemStack stack : player.getInventory().items) {
            if (matches(stack)){
                total += stack.getCount();
                if (total >= Integer.MAX_VALUE){
                    return Integer.MAX_VALUE;
                }
            }
        }

        return (int) total;
    }

    private boolean consumeRequiredItems(Player player, int required) {
        if (required <= 0 || player.getAbilities().instabuild){
            return true;
        }

        if (countAvailable(player) < required){
            return false;
        }

        int remaining = required;
        for (ItemStack stack : player.getInventory().items) {
            if (!matches(stack)){
                continue;
            }

            int taken = Math.min(remaining, stack.getCount());
            stack.shrink(taken);
            remaining -= taken;

            if (remaining <= 0){
                player.getInventory().setChanged();
                return true;
            }
        }

        return false;
    }

    private Component getConsumedItemName() {
        if (consumeItem != null){
            return consumeItem.getDescription();
        }

        if (consumeItemTagKey != null){
            return Component.literal("#" + consumeItemTagKey.location());
        }

        return Component.translatable(
                "modifier.dreamtinker.malum_spirit_accelerator.tooltip.unknown_ingredient"
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

        int level = modifier.getLevel();
        int needed = getRequiredCount(level);

        tooltip.add(Component.translatable(
                                     "modifier.dreamtinker.malum_spirit_accelerator.tooltip.cost",
                                     getConsumedItemName(),
                                     needed
                             )
                             .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable(
                                     "modifier.dreamtinker.malum_spirit_accelerator.tooltip.speed",
                                     level
                             )
                             .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.translatable(
                                     "modifier.dreamtinker.malum_spirit_accelerator.tooltip.damage",
                                     0.5D * level
                             )
                             .withStyle(ChatFormatting.GREEN));

        if (player != null){
            int available = countAvailable(player);
            tooltip.add(Component.translatable(
                                         "modifier.dreamtinker.malum_spirit_accelerator.tooltip.inventory",
                                         available,
                                         needed
                                 )
                                 .withStyle(player.getAbilities().instabuild || available >= needed
                                            ? ChatFormatting.GREEN
                                            : ChatFormatting.RED));
        }
    }
}
