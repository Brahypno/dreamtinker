package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

public class HiddenHit extends Modifier implements MeleeDamageModifierHook, ProjectileHitModifierHook, TooltipModifierHook {
    public boolean isNoLevels() {return false;}

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.PROJECTILE_HIT, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (DTHelper.getLivingTarget(context.getTarget()) instanceof Mob mob)
            if (null == mob.getTarget() || !mob.getTarget().is(context.getAttacker()))
                return damage + 3 * modifier.getLevel() * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
            else
                return damage - modifier.getLevel() * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
        return damage;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        Component statName = TooltipModifierHook.statName(modifier.getModifier(), ToolStats.ATTACK_DAMAGE);
        if (ToolStats.ATTACK_DAMAGE.supports(tool.getItem()))
            TooltipModifierHook.addFlatBoost(modifier.getModifier(), statName, (double) 3 * modifier.getLevel() * tool.getMultiplier(ToolStats.ATTACK_DAMAGE),
                                             tooltip);

        statName = TooltipModifierHook.statName(modifier.getModifier(), ToolStats.PROJECTILE_DAMAGE);
        if (ToolStats.PROJECTILE_DAMAGE.supports(tool.getItem()))
            TooltipModifierHook.addFlatBoost(modifier.getModifier(), statName,
                                             (double) modifier.getLevel() * tool.getMultiplier(ToolStats.PROJECTILE_DAMAGE),
                                             tooltip);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (target instanceof Mob mob && projectile instanceof AbstractArrow arr)
            if (attacker != null && (null == mob.getTarget() || !mob.getTarget().is(attacker)))
                arr.setBaseDamage(arr.getBaseDamage() + modifier.getLevel());
            else
                arr.setBaseDamage(arr.getBaseDamage() - modifier.getLevel());
        return false;
    }


}
