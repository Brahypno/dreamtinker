package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class BornWithMe extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, TooltipModifierHook {
    private float buff(int level) {
        return .5f + .2f * level;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT,
                            ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (context.isExtraAttack()){
            damage *= (1 + buff(modifier.getLevel()));
        }
        return damage;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        Component statName = TooltipModifierHook.statName(modifier.getModifier(), ToolStats.ATTACK_DAMAGE);
        if (ToolStats.ATTACK_DAMAGE.supports(tool.getItem()))
            TooltipModifierHook.addPercentBoost(modifier.getModifier(), statName, (double) buff(modifier.getLevel()), tooltip);
    }

    private static final float HEALTH_RATIO_EPS = 1.0E-3F;

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        Level level = context.getLevel();
        LivingEntity victim = DTHelper.getLivingTarget(context.getTarget());

        if (null != victim && !level.isClientSide){
            LivingEntity attacker = context.getAttacker();
            boolean is_born =
                    victim.isDeadOrDying() || attacker.getHealth() / attacker.getMaxHealth() <= victim.getHealth() / victim.getMaxHealth() + HEALTH_RATIO_EPS;
            if (is_born){
                AttributeInstance reach = attacker.getAttribute(ForgeMod.ENTITY_REACH.get());
                double range = null != reach ? reach.getValue() + 0.5 : 1.5;
                if (range > 0){
                    AABB attackerBox = attacker.getBoundingBox();
                    AABB searchBox = attackerBox.inflate(range, 0.75D, range);
                    Entity rootVehicle = attacker.getRootVehicle();
                    for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class, searchBox,
                                                                           aoeTarget -> aoeTarget != attacker
                                                                                        && aoeTarget != victim
                                                                                        && aoeTarget != rootVehicle
                                                                                        && !aoeTarget.isDeadOrDying()
                                                                                        && !attacker.isAlliedTo(aoeTarget)
                                                                                        && (!(aoeTarget instanceof ArmorStand stand) || !stand.isMarker())
                    )) {
                        if (aoeTarget.isAlive() && !aoeTarget.isRemoved())
                            ToolAttackUtil.performAttack(tool,
                                                         ToolAttackContext.attacker(attacker).target(aoeTarget).cooldown(1).applyAttributes().extraAttack()
                                                                          .build());
                        break;
                    }
                }
            }
        }
        if (context.isExtraAttack()){
            float expected = damageDealt * 0.05f * modifier.getLevel();
            context.getAttacker().setSecondsOnFire(5);
            context.getTarget().setSecondsOnFire(5);
            context.getAttacker().hurt(context.makeDamageSource(), expected);
        }
    }
    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }
}
