package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.bic;

import com.google.common.collect.Multiset;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static slimeknights.tconstruct.library.modifiers.modules.armor.EffectImmunityModule.EFFECT_IMMUNITY;

public class dark_defense extends ArmorModifier {
    private static MobEffect rampahe = null;
    private static final ResourceLocation rampahe_location = new ResourceLocation("born_in_chaos_v1", "rampant_rampage");

    public dark_defense() {
        rampahe = ForgeRegistries.MOB_EFFECTS.getValue(rampahe_location);
    }

    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (DTModifierCheck.ModifierALLBody(context.getEntity(), this.getId()) && !tool.isBroken() &&
            ArmorLevelModule.validSlot(tool, context.getChangedSlot(), TinkerTags.Items.HELD_ARMOR)){
            context.getTinkerData().ifPresent((data) -> ((Multiset) data.computeIfAbsent(EFFECT_IMMUNITY)).add(MobEffects.WITHER));
        }

    }

    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (!tool.isBroken() && ArmorLevelModule.validSlot(tool, context.getChangedSlot(), TinkerTags.Items.HELD_ARMOR)){
            context.getTinkerData().ifPresent((data) -> {
                Multiset<MobEffect> effects = (Multiset) data.get(EFFECT_IMMUNITY);
                if (effects != null){
                    effects.remove(MobEffects.WITHER);
                }
            });
        }
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide && !(isCorrectSlot || isSelected))
            return;
        if (2 == itemSlot && isCorrectSlot && stack.is(Tags.Items.ARMORS_CHESTPLATES) && DTModifierCheck.ModifierALLBody(holder, this.getId())){
            if (holder.hasEffect(MobEffects.WITHER))
                holder.removeEffect(MobEffects.WITHER);
            CompoundTag data = holder.getPersistentData();
            if (holder.getHealth() <= 6.0f && !data.getBoolean("bic_dark_ramp_already")){
                holder.addEffect(new MobEffectInstance(rampahe, 100, 0));
                holder.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2, false, false));
                data.putBoolean("bic_dark_ramp_already", true);
            }else if (6.0f < holder.getHealth())
                data.putBoolean("bic_dark_ramp_already", false);
        }
    }
}
