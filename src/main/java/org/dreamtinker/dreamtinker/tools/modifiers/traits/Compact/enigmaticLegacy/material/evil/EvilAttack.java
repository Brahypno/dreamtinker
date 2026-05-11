package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.evil;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArmorInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.BasicInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class EvilAttack extends Modifier implements BasicInterface, MeleeInterface, ArmorInterface, ArrowInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArmorInterfaceInit(hookBuilder);
        this.MeleeInterfaceInit(hookBuilder);
        this.ArrowInterfaceInit(hookBuilder);
        this.BasicInterfaceInit(hookBuilder);
        hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.cursed_ring_bound.getId(), 1, true));
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int cursed = context.getEntity() instanceof Player player ? SuperpositionHandler.getCurseAmount(player) + 1 : 1;
        return amount * cursed;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        int cursed = context.getAttacker() instanceof Player player ? SuperpositionHandler.getCurseAmount(player) + 1 : 1;
        return damage * cursed;
    }

    @Override
    public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
        if (ToolStats.DRAW_SPEED == stat || ToolStats.VELOCITY == stat){
            int cursed = living instanceof Player player ? SuperpositionHandler.getCurseAmount(player) + 1 : 1;
            baseValue *= cursed;

        }
        return baseValue;
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels() {return true;}


}
