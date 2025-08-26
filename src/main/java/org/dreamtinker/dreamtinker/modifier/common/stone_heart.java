package org.dreamtinker.dreamtinker.modifier.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.utils.DTModiferCheck;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.StoneheartProjreduce;

public class stone_heart extends ArmorModifier {
    {
        MinecraftForge.EVENT_BUS.addListener(this::LivingHealEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onEat);
    }

    private void LivingHealEvent(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (DTModiferCheck.haveModifierIn(entity, this.getId()))
            event.setAmount((float) (event.getAmount() * 0.2));
    }

    public void onEat(LivingEntityUseItemEvent.Finish e) {
        if (!(e.getEntity() instanceof Player p) || p.level().isClientSide)
            return;
        var fd = p.getFoodData();
        fd.setFoodLevel(Math.min(20, fd.getFoodLevel() - 2));
        fd.setSaturation(Math.min(fd.getFoodLevel(), fd.getSaturationLevel() - 2f));
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        LivingEntity holder = context.getEntity();
        Level level = holder.level();
        if (!level.isClientSide() && !(source.getEntity() instanceof LivingEntity)){
            return source.is(DamageTypes.DROWN) || source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.ON_FIRE) ||
                   source.is(DamageTypes.STARVE);

        }
        return false;
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (context.getEntity() instanceof Player player && !player.level().isClientSide)
            if (source.is(DamageTypes.ARROW) || source.is(DamageTypes.FIREWORKS) || source.is(DamageTypes.FIREBALL) ||
                source.getEntity() instanceof Projectile || source.getDirectEntity() instanceof Projectile)
                return (float) (amount * StoneheartProjreduce.get());
        return amount;
    }

}
