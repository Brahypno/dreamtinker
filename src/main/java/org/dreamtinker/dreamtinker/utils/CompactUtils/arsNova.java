package org.dreamtinker.dreamtinker.utils.CompactUtils;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class arsNova {
    public static void MeleeSpellDamagePre(LivingEntity shooter, Entity target, InteractionHand hand, ToolStack tool, float damage) {
        ToolAttackContext context = InteractionHand.MAIN_HAND == hand ?
                                    ToolAttackContext.attacker(shooter).target(target).hand(hand).applyAttributes().defaultCooldown().build() :
                                    ToolAttackContext.attacker(shooter).target(target).hand(hand).applyStats(tool).defaultCooldown().build();
        List<ModifierEntry> modifiers = tool.getModifierList();
        // I don't think it's a good Idea let spell damage critical, but that still may happened.
        float oldHealth = 0.0F;
        LivingEntity targetLiving = context.getLivingTarget();
        if (targetLiving != null){
            oldHealth = targetLiving.getHealth();
        }

        float baseKnockback = context.getBaseKnockback();
        float knockback = baseKnockback;

        for (ModifierEntry entry : modifiers) {
            knockback =
                    ((MeleeHitModifierHook) entry.getHook(ModifierHooks.MELEE_HIT)).beforeMeleeHit(tool, entry, context, damage, baseKnockback, knockback);
        }
        LivingEntity attackerLiving = context.getAttacker();
        EquipmentSlot sourceSlot = context.getSlotType();
        ModifierLootingHandler.setLootingSlot(attackerLiving, sourceSlot);
        // And I dont do knock back
        return;
    }

    public static void MeleeSpellDamagePost(LivingEntity shooter, Entity target, InteractionHand hand, ToolStack tool, float damage) {
        ToolAttackContext context = ToolAttackContext.attacker(shooter).target(target).hand(hand).applyAttributes().defaultCooldown().build();
        List<ModifierEntry> modifiers = tool.getModifierList();
        for (ModifierEntry entry : modifiers) {
            entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, entry, context, damage);
        }
        Entity targetEntity = context.getTarget();
        LivingEntity targetLiving = context.getLivingTarget();
        if (targetLiving != null){
            EnchantmentHelper.doPostHurtEffects(targetLiving, shooter);//enchantment should not mater too much
        }
        float speed = (Float) tool.getStats().get(ToolStats.ATTACK_SPEED);
        int time = Math.round(20.0F / speed);
        if (time < targetEntity.invulnerableTime){
            targetEntity.invulnerableTime = (targetEntity.invulnerableTime + time) / 2;
        }
    }

    public static boolean isMelee(SpellContext context) {
        SpellContext local = context;
        while (null != local) {
            if (local.getSpell().recipe.contains(MethodTouch.INSTANCE) || local.getSpell().recipe.contains(MethodSelf.INSTANCE))
                return true;
            local = local.getPreviousContext();
        }
        return false;
    }

    public static boolean isTinker(SpellContext context) {
        SpellContext local = context;
        while (null != local) {
            if (local.getSpell().recipe.contains(AugmentTinker.INSTANCE))
                return true;
            local = local.getPreviousContext();
        }
        return false;
    }

}
