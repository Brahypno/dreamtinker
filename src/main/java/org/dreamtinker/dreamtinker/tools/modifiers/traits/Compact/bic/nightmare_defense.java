package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.bic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class nightmare_defense extends ArmorModifier {
    private static MobEffect MAGIC_DEPLETION;
    private static final ResourceLocation magic_depletion = new ResourceLocation("born_in_chaos_v1", "magic_depletion");

    public nightmare_defense() {
        if (null == MAGIC_DEPLETION)
            MAGIC_DEPLETION = ForgeRegistries.MOB_EFFECTS.getValue(magic_depletion);
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (null != MAGIC_DEPLETION && holder.hasEffect(MAGIC_DEPLETION))
            holder.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30, 1, false, false));

    }
}
