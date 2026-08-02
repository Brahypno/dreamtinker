package org.brahypno.dreamtinker.tools.modifiers.traits.material.OathSteel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.brahypno.dreamtinker.tools.modifiers.events.OathGuardPaleSteelEvents.findProtectedTargets;

public class HealOath extends Modifier implements ModifyDamageModifierHook, InventoryTickModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity entity = context.getEntity();

        if (entity instanceof ServerPlayer player && isPrimaryOathPiece(player, slotType)){
            float strongestReduction = 0.0F;
            for (LivingEntity target : findProtectedTargets(player)) {
                float healthRatio = target.getHealth() / target.getMaxHealth();
                if (healthRatio < 0.5F){
                    /*
                     * 原本意图可由中英文语言文件互相印证：附近守护对象低于半血时，
                     * 持有者根据其伤势获得减伤，且“最高 20%”。因此线性映射应为：
                     *   守护对象生命 50% -> 0% 减伤
                     *   守护对象生命  0% -> 20% 减伤
                     *
                     * 旧代码把 strongest 初始化为 1，再计算 max(1, 0..0.2)，导致 16 格
                     * 实体查询永远不影响伤害。这里算出的值是“减伤比例”，不是“剩余伤害
                     * 倍率”，所以最终乘数必须是 (1 - strongestReduction)。
                     */
                    float reduction = (0.5F - healthRatio) * 0.40F;
                    strongestReduction = Math.max(strongestReduction, reduction);
                }
            }
            amount *= 1.0F - strongestReduction;
        }
        return amount;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if ((isSelected || isCorrectSlot) && holder instanceof ServerPlayer player && !world.isClientSide &&
            world.getGameTime() % 20 == 0){
            for (LivingEntity target : findProtectedTargets(player)) {
                float healthRatio = target.getHealth() / target.getMaxHealth();
                /* Preserve the original support behavior: refresh/replace an existing Regeneration
                 * effect on a badly wounded protected target, but do not grant Regeneration from
                 * nothing. This behavior is separate from the documented damage reduction above. */
                if (healthRatio < 0.5F && target.hasEffect(MobEffects.REGENERATION)){
                    target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * modifier.getLevel(), modifier.getLevel() - 1));
                }
            }
        }
    }

    /**
     * Armor hooks run once per equipped piece. The description promises a global cap of 20%, so only
     * the first equipped Heal Oath piece applies the shared reduction instead of compounding it four
     * times. If the modifier is present in an unusual non-armor slot, fall back to the current hook.
     */
    private boolean isPrimaryOathPiece(ServerPlayer player, EquipmentSlot currentSlot) {
        EquipmentSlot[] armorOrder = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };
        for (EquipmentSlot slot : armorOrder) {
            if (ModifierUtil.getModifierLevel(player.getItemBySlot(slot), getId()) > 0) {
                return slot == currentSlot;
            }
        }
        return true;
    }
}
