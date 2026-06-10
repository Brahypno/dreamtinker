package org.brahypno.dreamtinker.library.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class SecondaryArmorEffectDamageHandler {
    @SubscribeEvent
    static void SecondaryNoneEquipmentLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        // give modifiers a chance to respond to damage happening
        float amount = event.getAmount();
        EquipmentContext context = new EquipmentContext(entity);
        if (entity instanceof Player player){
            for (ItemStack stack : player.getInventory().items) {
                if (null == stack || stack.isEmpty() || !stack.is(TinkerTags.Items.ARMOR) || stack.equals(player.getMainHandItem()))
                    continue;
                if (stack.getItem() instanceof IModifiable){
                    IToolStackView toolStackView = ToolStack.from(stack);
                    amount = DTModifierCheck.modifyDamageTakenInventory(ModifierHooks.MODIFY_DAMAGE, context, source, amount,
                                                                        OnAttackedModifierHook.isDirectDamage(source), DTModifierCheck.toSlot(stack),
                                                                        toolStackView);
                    if (amount <= 0)
                        break;
                }
            }
            event.setAmount(amount);
            if (amount <= 0){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    static void SecondaryNoneEquipmentLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        // client side always returns false, so this should be fine?
        if (entity.level().isClientSide() || entity.isDeadOrDying()){
            return;
        }
        // I cannot think of a reason to run when invulnerable
        DamageSource source = event.getSource();
        if (entity.isInvulnerableTo(source)){
            return;
        }

        // a lot of counterattack hooks want to detect direct attacks, so save time by calculating once
        boolean isDirectDamage = OnAttackedModifierHook.isDirectDamage(source);

        // determine if there is any modifiable armor, handles the target wearing modifiable armor
        EquipmentContext context = new EquipmentContext(entity);
        float amount = event.getAmount();
        if (entity instanceof Player player){
            for (ItemStack stack : player.getInventory().items) {
                if (null == stack || stack.isEmpty() || !stack.is(TinkerTags.Items.ARMOR) || stack.equals(player.getMainHandItem()))
                    continue;
                if (stack.getItem() instanceof IModifiable){
                    IToolStackView toolStack = ToolStack.from(stack);
                    if (!toolStack.isBroken()){
                        EquipmentSlot slotType = DTModifierCheck.toSlot(stack);
                        for (ModifierEntry entry : toolStack.getModifierList()) {
                            if (entry.getModifier().is(DreamtinkerTagKeys.Modifiers.ArmorWorkingWhenUnequipped) &&
                                entry.getHook(ModifierHooks.DAMAGE_BLOCK)
                                     .isDamageBlocked(toolStack, entry, context, slotType, source, amount)){
                                event.setCanceled(true);
                                return;
                            }
                        }
                    }
                }
            }
            for (ItemStack stack : player.getInventory().items) {
                if (null != stack && !stack.isEmpty() && stack.is(TinkerTags.Items.ARMOR) && stack.getItem() instanceof IModifiable){
                    IToolStackView toolStack = ToolStack.from(stack);
                    if (!toolStack.isBroken()){
                        EquipmentSlot slotType = DTModifierCheck.toSlot(stack);
                        for (ModifierEntry entry : toolStack.getModifierList()) {
                            if (entry.getModifier().is(DreamtinkerTagKeys.Modifiers.ArmorWorkingWhenUnequipped))
                                entry.getHook(ModifierHooks.ON_ATTACKED).onAttacked(toolStack, entry, context, slotType, source, amount, isDirectDamage);
                        }
                    }
                }
            }
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof Player player){
            context = new EquipmentContext(player);
            for (ItemStack stack : player.getInventory().items) {
                if (null != stack && !stack.isEmpty() && stack.is(TinkerTags.Items.ARMOR) && stack.getItem() instanceof IModifiable){
                    IToolStackView toolStack = ToolStack.from(stack);
                    if (!toolStack.isBroken()){
                        EquipmentSlot slotType = DTModifierCheck.toSlot(stack);
                        for (ModifierEntry entry : toolStack.getModifierList()) {
                            if (entry.getModifier().is(DreamtinkerTagKeys.Modifiers.ArmorWorkingWhenUnequipped))
                                entry.getHook(ModifierHooks.DAMAGE_DEALT)
                                     .onDamageDealt(toolStack, entry, context, slotType, entity, source, amount, isDirectDamage);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    static void SecondaryNoneEquipmentHurtHandler(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        EquipmentContext context = new EquipmentContext(entity);
        float originalDamage = event.getAmount();

        // for our own armor, we have boosts from modifiers to consider
        if (entity instanceof Player player){
            for (ItemStack stack : player.getInventory().items) {
                if (null == stack || stack.isEmpty() || !stack.is(TinkerTags.Items.ARMOR) || stack.equals(player.getMainHandItem()))
                    continue;
                if (stack.getItem() instanceof IModifiable){
                    IToolStackView toolStackView = ToolStack.from(stack);
                    originalDamage = DTModifierCheck.modifyDamageTakenInventory(ModifierHooks.MODIFY_HURT, context, source, originalDamage,
                                                                                OnAttackedModifierHook.isDirectDamage(source), DTModifierCheck.toSlot(stack),
                                                                                toolStackView);
                    if (originalDamage <= 0)
                        break;
                }
            }
            event.setAmount(originalDamage);
            if (originalDamage <= 0){
                event.setCanceled(true);
            }
        }
    }
}
