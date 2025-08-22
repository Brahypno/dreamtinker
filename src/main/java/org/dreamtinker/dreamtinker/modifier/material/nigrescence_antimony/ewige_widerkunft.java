package org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.Prometheus;
import static org.dreamtinker.dreamtinker.utils.modiferCheck.ModifierInHand;

public class ewige_widerkunft extends BattleModifier {
    private static final ResourceLocation TAG_TOMB = new ResourceLocation(Dreamtinker.MODID, "ewige_widerkunft");

    {
        MinecraftForge.EVENT_BUS.addListener(this::LivingHurtEvent);
    }

    @Override
    public int modifierDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        int current = tool.getCurrentDurability();
        ModDataNBT nbt = tool.getPersistentData();
        int breaks = nbt.getInt(TAG_TOMB) + 1;

        if (current - breaks * amount <= 1){
            nbt.putInt(TAG_TOMB, breaks);
            tool.setDamage(0);
            if (holder != null){
                holder.sendSystemMessage(Component.literal("13=1"));
                holder.level().explode(holder, holder.getX(), holder.getY(), holder.getZ(), current, true, Level.ExplosionInteraction.MOB);
            }
            return 0;
        }
        return breaks * amount;
    }

    private void LivingHurtEvent(LivingHurtEvent event) {
        if (event.getSource().is(DamageTypeTags.IS_EXPLOSION) && event.getEntity() != null){
            ModifierInHand(event.getEntity(), this.getId());
            event.setCanceled(true);
        }
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (modifier.getLevel() > 0){
            ModDataNBT nbt = tool.getPersistentData();
            int breaks = nbt.getInt(TAG_TOMB);
            if (breaks > 0){
                consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("3d1df7e8-4b20-4e2d-9d5f-5c1b2f8e7c9d"), Attributes.ATTACK_DAMAGE.getDescriptionId(), Math.pow(1 + Prometheus.get(), breaks) / 2, AttributeModifier.Operation.MULTIPLY_BASE));
                consumer.accept(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("8f2c1a9e-3f54-4d7b-96a1-7a2e6cf3b1a4"), Attributes.ATTACK_SPEED.getDescriptionId(), Math.pow(1 + Prometheus.get(), breaks) / 2, AttributeModifier.Operation.MULTIPLY_BASE));
                consumer.accept(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(UUID.fromString("7f58b4e7-9ab6-42bb-952a-c41016464b14"), Attributes.ATTACK_KNOCKBACK.getDescriptionId(), Math.pow(1 + Prometheus.get(), breaks) / 2, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
    }

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_TOMB);
        return null;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool instanceof ToolStack && tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            int count = nbt.getInt(TAG_TOMB);
            if (count > 0){
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.ewige_widerkunft").append(String.valueOf(count)).withStyle(this.getDisplayName().getStyle()));
            }
        }
    }
    
    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
