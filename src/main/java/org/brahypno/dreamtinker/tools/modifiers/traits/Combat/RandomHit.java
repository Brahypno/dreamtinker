package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public class RandomHit extends Modifier implements MeleeDamageModifierHook, ProjectileLaunchModifierHook, TooltipModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    private float lower = 1f, higher = 1f;

    public RandomHit(float lower, float higher) {
        this.lower = lower;
        this.higher = higher;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * random_hit_value(context.getAttacker().level().random, modifier.getLevel());
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        projectile.setDeltaMovement(projectile.getDeltaMovement()
                                              .scale(random_hit_value(shooter.level().random, modifier.getLevel())));
    }

    private float random_hit_value(RandomSource rs, int level) {
        if (rs.nextFloat() < 0.001)
            return rs.nextBoolean() ? 10.0f : 0.1f;
        if (rs.nextFloat() < 0.1)
            return 2.0f;
        return Mth.nextFloat(rs, lower * (1.1f - level * 0.1f), Math.nextUp((higher * (level + 1)) / 2));
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        Component statName = TooltipModifierHook.statName(modifier.getModifier(), ToolStats.ATTACK_DAMAGE);
        if (ToolStats.ATTACK_DAMAGE.supports(tool.getItem())){
            tooltip.add(
                    this.applyStyle(
                            Component.literal(Util.PERCENT_BOOST_FORMAT.format(lower * (1.1f - modifier.getLevel() * 0.1f)) + " -- " +
                                              Util.PERCENT_BOOST_FORMAT.format(Math.nextUp((higher * (modifier.getLevel() + 1)) / 2))).append(statName)));
        }

        statName = TooltipModifierHook.statName(modifier.getModifier(), ToolStats.VELOCITY);
        if (ToolStats.VELOCITY.supports(tool.getItem()))
            tooltip.add(
                    this.applyStyle(
                            Component.literal(Util.PERCENT_BOOST_FORMAT.format(lower * (1.1f - modifier.getLevel() * 0.1f)) + " -- " +
                                              Util.PERCENT_BOOST_FORMAT.format(Math.nextUp((higher * (modifier.getLevel() + 1)) / 2))).append(statName)));
    }

}
