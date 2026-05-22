package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerAttributes;

import java.util.UUID;
import java.util.function.BiConsumer;

public class DespairRain extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, InventoryTickModifierHook, AttributesModifierHook {
    public boolean isNoLevels() {return false;}

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * (context.isCritical() ? modifier.getLevel() + 1 : 1.0f / modifier.getLevel());
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken())
            consumer.accept(TinkerAttributes.CRITICAL_DAMAGE.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                                                  TinkerAttributes.CRITICAL_DAMAGE.get().getDescriptionId(),
                                                  4 + modifier.getLevel(),
                                                  AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity victim = context.getLivingTarget();
        if (null != victim){
            AttributeInstance attr_instance = victim.getAttribute(Attributes.MAX_HEALTH);
            if (null != attr_instance){
                UUID uuid = UUID.nameUUIDFromBytes((this.getId() + "." + Attributes.MAX_HEALTH.getDescriptionId()).getBytes());
                double debuff = 0;
                AttributeModifier cur = attr_instance.getModifier(uuid);
                if (null != cur)
                    debuff = cur.getAmount();
                attr_instance.removeModifier(uuid);
                attr_instance.addTransientModifier(
                        new AttributeModifier(uuid, Attributes.MAX_HEALTH.getDescriptionId(), debuff - damageDealt * 0.1f * modifier.getLevel(),
                                              AttributeModifier.Operation.ADDITION));
            }

        }
    }


    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if ((isCorrectSlot || isSelected) && world.getGameTime() % 20 == 0)
            holder.setHealth(modifier.getLevel());

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT,
                            ModifierHooks.INVENTORY_TICK, ModifierHooks.ATTRIBUTES);
        super.registerHooks(hookBuilder);
    }
    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }
}
