package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public class SideAttack extends Modifier implements MeleeDamageModifierHook, ProjectileHitModifierHook, TooltipModifierHook {
    /**
     * Projectile persistent data key for the stat multiplier
     */
    private static final ResourceLocation AMMO_MULTIPLIER = ToolStats.PROJECTILE_DAMAGE.getName().withSuffix("_ammo_multiplier");
    /**
     * Projectile persistent data key for the stat multiplier
     */
    private static final ResourceLocation BOW_MULTIPLIER = ToolStats.PROJECTILE_DAMAGE.getName().withSuffix("_bow_multiplier");

    private static float getFacingAngleDegHorizontal(Entity attacker, Entity target) {
        Vec3 a = attacker instanceof Projectile ? attacker.getDeltaMovement() : attacker.getLookAngle();
        Vec3 b = target.getLookAngle().scale(-1);

        Vec3 ah = new Vec3(a.x, 0, a.z);
        Vec3 bh = new Vec3(b.x, 0, b.z);

        if (ah.lengthSqr() < 1.0E-6 || bh.lengthSqr() < 1.0E-6){
            return 1f;
        }

        ah = ah.normalize();
        bh = bh.normalize();

        double dot = Mth.clamp(ah.dot(bh), -1.0D, 1.0D);
        return (float) Math.abs(dot);
    }

    public boolean isNoLevels() {return false;}

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.PROJECTILE_HIT, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        float dir = getFacingAngleDegHorizontal(context.getAttacker(), context.getTarget());
        damage += damage * dir * (1.4f + (modifier.getEffectiveLevel() - 1) * 0.4f) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
        return damage;
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        ResourceLocation key = modifier.getId();
        // if we already boosted power from an entity, don't boost again
        // minimizes issues with projectile bounces and piercing
        if (!persistentData.getBoolean(key) && null != target){
            // as soon as we attempt to boost, mark this modifier as having run
            // means the second entity will not get to apply its boost if the first did not apply it
            persistentData.putBoolean(key, true);

            float multiplier = 1;
            if (persistentData.contains(AMMO_MULTIPLIER, Tag.TAG_ANY_NUMERIC)){
                multiplier *= persistentData.getFloat(AMMO_MULTIPLIER);
            }
            if (persistentData.contains(BOW_MULTIPLIER, Tag.TAG_ANY_NUMERIC)){
                multiplier *= persistentData.getFloat(BOW_MULTIPLIER);
            }
            float dir = getFacingAngleDegHorizontal(projectile, target);
            float multi = dir * (1.4f + (modifier.getEffectiveLevel() - 1) * 0.4f) * multiplier;

            if (projectile instanceof AbstractArrow arrow){
                arrow.setBaseDamage(arrow.getBaseDamage() * (1 + multi));
            }else if (projectile instanceof ProjectileWithPower withPower){
                withPower.setPower(withPower.getPower() * (1 + multi));
            }
        }
        return false;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        Component statName = TooltipModifierHook.statName(modifier.getModifier(), ToolStats.ATTACK_DAMAGE);
        if (ToolStats.ATTACK_DAMAGE.supports(tool.getItem())){
            tooltip.add(
                    this.applyStyle(
                                Component.literal(Util.PERCENT_BOOST_FORMAT.format(0) + " -- " +
                                                  Util.PERCENT_BOOST_FORMAT.format(Math.nextUp(
                                                          (1.4f + (modifier.getEffectiveLevel() - 1) * 0.4f) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE)))))
                        .append(statName));
        }

        statName = TooltipModifierHook.statName(modifier.getModifier(), ToolStats.PROJECTILE_DAMAGE);
        if (ToolStats.PROJECTILE_DAMAGE.supports(tool.getItem()))
            tooltip.add(
                    this.applyStyle(
                            Component.literal(Util.PERCENT_BOOST_FORMAT.format(0) + " -- " +
                                              Util.PERCENT_BOOST_FORMAT.format((1.4f + (modifier.getEffectiveLevel() - 1) * 0.4f))).append(statName)));
    }

}
