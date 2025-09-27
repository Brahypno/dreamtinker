package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.evil;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArmorInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class evil_attack extends Modifier implements MeleeInterface, ArmorInterface, ArrowInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArmorInterfaceInit(hookBuilder);
        this.MeleeInterfaceInit(hookBuilder);
        this.ArrowInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int cursed = context.getEntity() instanceof Player player ? SuperpositionHandler.getCurseAmount(player) : 1;
        return amount * cursed;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        int cursed = context.getAttacker() instanceof Player player ? SuperpositionHandler.getCurseAmount(player) : 1;
        return damage * cursed;
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        int cursed = shooter instanceof Player player ? SuperpositionHandler.getCurseAmount(player) : 1;
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(cursed));
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels() {return true;}


}
