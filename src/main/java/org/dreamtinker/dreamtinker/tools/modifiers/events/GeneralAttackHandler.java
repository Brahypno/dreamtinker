package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTDamageUtils;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralAttackHandler {
    private static final int allowed_extra_times = 2;
    private static final ThreadLocal<Integer> extra_attack_depth = ThreadLocal.withInitial(() -> 0);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingAttackEvent(LivingAttackEvent event) {
        DamageSource dmg = event.getSource();
        Entity dmgEntity = dmg.getEntity();
        float damageAmount = event.getAmount();
        LivingEntity victim = event.getEntity();
        if (0 == damageAmount || event.isCanceled())
            return;
        Level world = victim.level();
        if (world.isClientSide())
            return;
        RegistryAccess registryAccess = world.registryAccess();
        if (dmgEntity instanceof LivingEntity attacker){
            if (DTModifierCheck.haveModifierIn(attacker, DreamtinkerModifiers.despair_wind.getId())){
                int depth = extra_attack_depth.get();
                if (depth < allowed_extra_times){
                    try {
                        int inv = victim.invulnerableTime;
                        victim.invulnerableTime = 0;
                        extra_attack_depth.set(depth + 1);
                        DTDamageUtils.damageHandler(victim,
                                                    DreamtinkerDamageTypes.source(registryAccess, DreamtinkerDamageTypes.NULL_VOID, null, attacker),
                                                    damageAmount);
                        victim.invulnerableTime = inv;
                    }
                    finally {
                        extra_attack_depth.set(depth);
                    }
                }
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

}
