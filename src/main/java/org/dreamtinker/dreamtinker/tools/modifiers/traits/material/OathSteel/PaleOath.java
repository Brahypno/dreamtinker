package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.OathSteel;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.utils.DTMessages;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

import static org.dreamtinker.dreamtinker.tools.modifiers.events.OathGuardPaleSteelEvents.isGuardianProtectedTarget;
import static org.dreamtinker.dreamtinker.tools.modifiers.events.OathGuardPaleSteelEvents.oathPaleSteelId;

public class PaleOath extends Modifier implements ProtectionModifierHook, InventoryTickModifierHook, TooltipModifierHook, ValidateModifierHook, ModifierRemovalHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROTECTION, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOLTIP, ModifierHooks.VALIDATE, ModifierHooks.REMOVE);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
        if (context.getLevel() instanceof ServerLevel sl && context.getEntity() instanceof ServerPlayer sp)
            modifierValue += sl.getEntitiesOfClass(
                    LivingEntity.class,
                    context.getEntity().getBoundingBox().inflate(16.0D),
                    target -> target.isAlive()
                              && isGuardianProtectedTarget(sp, target)).size() * 2.5f;
        return modifierValue;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;

        ModDataNBT data = tool.getPersistentData();
        int pale = data.getInt(oathPaleSteelId);
        MaterialId id = DreamtinkerMaterialIds.ForlornOathSteel;
        int threshold = Math.max(tool.getCurrentDurability() + tool.getDamage(), 500);
        if (pale < threshold)
            return;

        MaterialNBT mats = tool.getMaterials();
        int index = -1;
        for (int i = 0; i < mats.size(); i++) {
            if (mats.get(i).sameVariant(DreamtinkerMaterialIds.OathGuardPaleSteel)){
                index = i;
                break;
            }
        }
        if (index == -1){
            tool.getPersistentData().remove(oathPaleSteelId);
            return;
        }
        mats = mats.replaceMaterial(index, id);
        ToolStack toolStack = ToolStack.from(stack);
        toolStack.setMaterials(mats);
        toolStack.updateStack(stack);
        if (holder instanceof Player){
            DTMessages.clientChat(
                    Component.translatable("modifier.dreamtinker.pale_oath.success_scale").withStyle(this.getDisplayName().getStyle()), false);
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            int pale = tool.getPersistentData().getInt(oathPaleSteelId);
            int threshold = Math.max(tool.getCurrentDurability() + tool.getDamage(), 500);
            if (0 < pale)
                tooltip.add(Component.translatable("modifier.dreamtinker.pale_oath.tooltip", pale, threshold));
        }
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        if (1 < modifier.getLevel() && 1 < tool.getModifier(this).getLevel())
            return Component.translatable("modifier.dreamtinker.pupal_omen.validate");
        return null;
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(oathPaleSteelId);
        return null;
    }
}
