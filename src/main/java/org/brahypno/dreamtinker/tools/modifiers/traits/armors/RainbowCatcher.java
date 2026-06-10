package org.brahypno.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.shared.TinkerAttributes;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static net.minecraft.nbt.Tag.TAG_FLOAT;
import static net.minecraft.tags.DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS;

public class RainbowCatcher extends Modifier implements InventoryTickModifierHook, AttributesModifierHook, DamageBlockModifierHook, TooltipModifierHook {
    private final ResourceLocation TAG_IN_RAIN = Dreamtinker.getLocation("rainbow_catcher");
    private final ResourceLocation TAG_RAIN_BLOCK = Dreamtinker.getLocation("rainbow_catcher_blocker");

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.ATTRIBUTES, ModifierHooks.DAMAGE_BLOCK, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        ModDataNBT stat = tool.getPersistentData();
        boolean cur = holder.isInWaterOrRain() && !holder.isInWater();
        boolean prev = stat.contains(TAG_IN_RAIN) && stat.getBoolean(TAG_IN_RAIN);
        float blocked_dmg = stat.getFloat(TAG_RAIN_BLOCK);
        boolean early_check =
                (!isSelected && !isCorrectSlot) && DTModifierCheck.ModifierInBody(holder, this.getId());//unequipped but still have ability to block damage.
        if (0 < blocked_dmg)
            if (cur != prev && !cur || (!isSelected && !isCorrectSlot)){
                holder.hurt(DreamtinkerDamageTypes.source(holder.level().registryAccess(), DreamtinkerDamageTypes.rain_bow, null, null),
                            Math.min(Integer.MAX_VALUE, blocked_dmg * (early_check ? 1 : 3)));
                stat.remove(TAG_RAIN_BLOCK);

            }
        if (cur != prev)
            stat.putBoolean(TAG_IN_RAIN, cur);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken() && tool.getPersistentData().getBoolean(TAG_IN_RAIN))
            consumer.accept(TinkerAttributes.JUMP_COUNT.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                                                  this.getTranslationKey(),
                                                  modifier.getLevel() * 2,
                                                  AttributeModifier.Operation.ADDITION));
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        if (!tool.isBroken() && context.getEntity().isInWaterOrRain() && !context.getEntity().isInWater() && !source.is(ALWAYS_HURTS_ENDER_DRAGONS)){
            float already = tool.getPersistentData().getFloat(TAG_RAIN_BLOCK);
            tool.getPersistentData().putFloat(TAG_RAIN_BLOCK, Math.min(Integer.MAX_VALUE, already + amount));
            return true;
        }
        return false;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            if (nbt.contains(TAG_RAIN_BLOCK, TAG_FLOAT)){
                int count = nbt.getInt(TAG_RAIN_BLOCK);
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.rainbow_catcher").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }


}
