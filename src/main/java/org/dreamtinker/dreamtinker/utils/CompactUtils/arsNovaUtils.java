package org.dreamtinker.dreamtinker.utils.CompactUtils;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.perk.*;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.UUID;

public class arsNovaUtils {
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

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, ArmorItem.Type type) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();
        if (type.getSlot() == pEquipmentSlot){
            UUID uuid = UUID.nameUUIDFromBytes((stack.getItem() + type.getName()).getBytes());
            IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
            if (perkHolder != null){
                attributes.put((Attribute) PerkAttributes.MAX_MANA.get(),
                               new AttributeModifier(uuid, "max_mana_armor", (double) (30 * (perkHolder.getTier() + 1)), AttributeModifier.Operation.ADDITION));
                attributes.put((Attribute) PerkAttributes.MANA_REGEN_BONUS.get(),
                               new AttributeModifier(uuid, "mana_regen_armor", (double) (perkHolder.getTier() + 1), AttributeModifier.Operation.ADDITION));

                for (PerkInstance perkInstance : perkHolder.getPerkInstances()) {
                    IPerk perk = perkInstance.getPerk();
                    attributes.putAll(perk.getModifiers(type.getSlot(), stack, perkInstance.getSlot().value));
                }
            }
        }
        return attributes.build();
    }

    public static void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        IPerkProvider<ItemStack> perkProvider = PerkRegistry.getPerkProvider(stack.getItem());
        if (perkProvider != null){
            IPerkHolder var7 = perkProvider.getPerkHolder(stack);
            if (var7 instanceof ArmorPerkHolder){
                ArmorPerkHolder armorPerkHolder = (ArmorPerkHolder) var7;
                tooltip.add(Component.translatable("ars_nouveau.tier", new Object[]{armorPerkHolder.getTier() + 1}).withStyle(ChatFormatting.GOLD));
            }

            perkProvider.getPerkHolder(stack).appendPerkTooltip(tooltip, stack);
        }

    }
}
