package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.*;

public class GlacialRiver extends Modifier implements MeleeDamageModifierHook, MonsterMeleeHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_HIT);
        super.registerHooks(hookBuilder);
    }

    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description", glacialRiverPortion.get() * 100)
                                      .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        Level level = context.getLevel();
        float damageboost = 0;
        for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class,
                                                               context.getAttacker().getBoundingBox().inflate(glacialRiverRange.get(), 0.25D, 5))) {
            if (aoeTarget.isAlliedTo(context.getAttacker()))
                continue;
            float life_hurt = (float) (aoeTarget.getMaxHealth() * glacialRiverPortion.get());
            float life = !glacialRiverKillPlayer.get() && aoeTarget instanceof Player && aoeTarget.getHealth() - life_hurt < 1 ? 1 :
                         (aoeTarget.getHealth()) - life_hurt;
            aoeTarget.setHealth(life);
            damageboost += life_hurt;
        }
        damage += damageboost;
        return damage;
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        //cannot boost damage, so just do range HP set
        Level level = context.getLevel();
        for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class,
                                                               context.getAttacker().getBoundingBox().inflate(glacialRiverRange.get(), 0.25D, 5))) {
            if (aoeTarget.isAlliedTo(context.getAttacker()))
                continue;
            float life_hurt = (float) (aoeTarget.getMaxHealth() * glacialRiverPortion.get());
            float life = !glacialRiverKillPlayer.get() && aoeTarget instanceof Player && aoeTarget.getHealth() - life_hurt < 1 ? 1 :
                         (aoeTarget.getHealth()) - life_hurt;
            aoeTarget.setHealth(life);
        }
    }
}
