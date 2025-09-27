package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.star_regulus;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.nbt.Tag.TAG_INT;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;
import static org.dreamtinker.dreamtinker.utils.DTModiferCheck.getToolWithModifier;


public class as_one extends ArmorModifier {
    private final int as_one_life = AsOneRe.get();
    private static final int SECOND_THRESHOLD = AsOneT.get();

    private static final ResourceLocation TAG_AS_ONE = new ResourceLocation(Dreamtinker.MODID, "as_one");
    private static final ResourceLocation TAG_LAST = new ResourceLocation(Dreamtinker.MODID, "rev_b");

    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        ModDataNBT nbt = tool.getPersistentData();
        if (!nbt.contains(TAG_AS_ONE, TAG_INT))
            nbt.putInt(TAG_AS_ONE, as_one_life);
    }

    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        ToolStack tool = getToolWithModifier(entity, this.getId());
        if (null != tool){
            ModDataNBT tooldata = tool.getPersistentData();
            int count = tooldata.getInt(TAG_AS_ONE);
            if (0 < count){
                tooldata.putInt(TAG_AS_ONE, --count);
                event.setCanceled(true);
                entity.deathTime = -10;
                entity.setHealth((float) (entity.getMaxHealth() * 0.15));
                entity.invulnerableTime = 1000;
            }
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool instanceof ToolStack && tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            int count = nbt.getInt(TAG_AS_ONE);
            if (count > 0){
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.as_one").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!isCorrectSlot)
            return;
        if (world.isClientSide)
            return;
        if (world.getGameTime() % 20 == 0){
            ModDataNBT toolData = tool.getPersistentData();
            int last_second = toolData.getInt(TAG_LAST);
            int as_one_cnt = toolData.getInt(TAG_AS_ONE);
            if (SECOND_THRESHOLD <= last_second + 1){
                toolData.putInt(TAG_LAST, last_second + 1 - SECOND_THRESHOLD);
                toolData.putInt(TAG_AS_ONE, ++as_one_cnt);
            }else
                toolData.putInt(TAG_LAST, last_second + 1);
        }
        List<MobEffectInstance> snapshot = new ArrayList<>(holder.getActiveEffects());
        for (MobEffectInstance inst : snapshot) {
            int amp = inst.getAmplifier();
            if (amp < AsOneA.get()){
                MobEffect type = inst.getEffect();
                int duration = inst.getDuration();
                boolean ambient = inst.isAmbient();
                boolean particles = inst.isVisible();
                boolean icon = inst.showIcon();

                holder.removeEffect(type);
                if (0 <= amp - 2)
                    holder.addEffect(new MobEffectInstance(type, duration, amp - 2, ambient, particles, icon));
            }
        }
    }

    @Override
    public int modifierDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @org.jetbrains.annotations.Nullable LivingEntity holder) {return 0;}

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        return (float) (amount * AsOneS.get());
    }
}

