package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.scolecite;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerAttributes;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.utils.DTMessages;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
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
import java.util.UUID;
import java.util.function.BiConsumer;

public class pupalOmen extends Modifier implements ModifyDamageModifierHook, ProtectionModifierHook, InventoryTickModifierHook, ModifierRemovalHook, TooltipModifierHook, AttributesModifierHook, ValidateModifierHook {
    public static final ResourceLocation TAG_SCALE = Dreamtinker.getLocation("scale_worm_armor");
    public static final ResourceLocation TAG_MOTH = Dreamtinker.getLocation("moth_wing_armor");
    private final int OmenInSight = 120;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROTECTION, ModifierHooks.INVENTORY_TICK, ModifierHooks.REMOVE, ModifierHooks.TOOLTIP,
                            ModifierHooks.ATTRIBUTES, ModifierHooks.VALIDATE);
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (context.getLevel().isClientSide || !tool.hasTag(TinkerTags.Items.ARMOR))
            return amount;
        ResourceLocation resourceLocation;
        if (source.is(TinkerTags.DamageTypes.MELEE_PROTECTION) || source.is(TinkerTags.DamageTypes.PROJECTILE_PROTECTION)){
            resourceLocation = TAG_SCALE;
        }else {
            resourceLocation = TAG_MOTH;
        }
        ModDataNBT data = tool.getPersistentData();
        int evolution = (int) (data.getInt(resourceLocation) + Math.floor(amount * 2.0F) + 1);
        data.putInt(resourceLocation, evolution);
        return amount;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;

        ModDataNBT data = tool.getPersistentData();
        int scale = data.getInt(TAG_SCALE);
        int moth = data.getInt(TAG_MOTH);
        MaterialId id;
        int threshold = Math.max((tool.getCurrentDurability() + tool.getDamage()) / 2, OmenInSight);
        if (scale < threshold && moth < threshold)
            return;
        else if (threshold <= scale){
            id = DreamtinkerMaterialIds.PermanenceScale;
        }else {
            id = DreamtinkerMaterialIds.PermanenceWing;
        }
        MaterialNBT mats = tool.getMaterials();
        int index = -1;
        for (int i = 0; i < mats.size(); i++) {
            if (mats.get(i).sameVariant(DreamtinkerMaterialIds.scolecite)){
                index = i;
                break;
            }
        }
        if (index == -1){
            if (holder instanceof Player player){
                player.sendSystemMessage(Component.translatable("modifier.dreamtinker.pupal_omen.failure")
                                                  .withStyle(this.getDisplayName().getStyle()));
            }
            tool.getPersistentData().remove(TAG_SCALE);
            tool.getPersistentData().remove(TAG_MOTH);
            return;
        }
        mats = mats.replaceMaterial(index, id);
        ToolStack toolStack = ToolStack.from(stack);
        toolStack.setMaterials(mats);
        toolStack.updateStack(stack);
        if (holder instanceof Player){
            DTMessages.clientChat(
                    Component.translatable(
                                     threshold <= scale ? "modifier.dreamtinker.pupal_omen.success_scale" : "modifier.dreamtinker.pupal_omen.success_wing")
                             .withStyle(this.getDisplayName().getStyle()), false);
        }
    }

    @Override
    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
        int scale = tool.getPersistentData().getInt(TAG_SCALE);
        int moth = tool.getPersistentData().getInt(TAG_MOTH);
        if (scale < moth){
            modifierValue += Math.min(OmenInSight, Math.abs(scale - moth)) * 0.125F / 4;
        }

        return modifierValue;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            int scale = tool.getPersistentData().getInt(TAG_SCALE);
            int moth = tool.getPersistentData().getInt(TAG_MOTH);
            int threshold = Math.max((tool.getCurrentDurability() + tool.getDamage()) / 2, OmenInSight);
            if (0 < moth)
                tooltip.add(Component.translatable("modifier.dreamtinker.pupal_omen_moth.tooltip", moth, threshold));
            if (0 < scale)
                tooltip.add(Component.translatable("modifier.dreamtinker.pupal_omen_scale.tooltip", scale, threshold));
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
        tool.getPersistentData().remove(TAG_SCALE);
        tool.getPersistentData().remove(TAG_MOTH);
        return null;
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        int scale = tool.getPersistentData().getInt(TAG_SCALE);
        int moth = tool.getPersistentData().getInt(TAG_MOTH);
        int buff = Math.min(OmenInSight, Math.abs(scale - moth));
        if (moth < scale){
            consumer.accept(Attributes.ARMOR,
                            new AttributeModifier(UUID.nameUUIDFromBytes((slot + "." + this.getId()).getBytes()),
                                                  Attributes.ARMOR.getDescriptionId(),
                                                  buff * 0.04,
                                                  AttributeModifier.Operation.ADDITION));
            consumer.accept(Attributes.ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.nameUUIDFromBytes((slot + "." + this.getId()).getBytes()),
                                                  Attributes.ARMOR_TOUGHNESS.getDescriptionId(),
                                                  buff * 0.02,
                                                  AttributeModifier.Operation.ADDITION));
            consumer.accept(Attributes.KNOCKBACK_RESISTANCE,
                            new AttributeModifier(UUID.nameUUIDFromBytes((slot + "." + this.getId()).getBytes()),
                                                  Attributes.KNOCKBACK_RESISTANCE.getDescriptionId(),
                                                  buff * 0.04,
                                                  AttributeModifier.Operation.ADDITION));
            consumer.accept(DreamtinkerAttributes.BLOOD_IN_SHELL.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes((slot + "." + this.getId()).getBytes()),
                                                  DreamtinkerAttributes.BLOOD_IN_SHELL.get().getDescriptionId(),
                                                  buff * 0.04,
                                                  AttributeModifier.Operation.ADDITION));
        }else if (scale < moth){
            consumer.accept(DreamtinkerAttributes.FATE_VEIL.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes((slot + "." + this.getId()).getBytes()),
                                                  DreamtinkerAttributes.FATE_VEIL.get().getDescriptionId(),
                                                  buff * 0.15,
                                                  AttributeModifier.Operation.ADDITION));
        }
    }

}
