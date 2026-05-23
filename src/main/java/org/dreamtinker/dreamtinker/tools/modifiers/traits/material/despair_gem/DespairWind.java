package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.utils.DTDamageUtils;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.dreamtinker.dreamtinker.utils.MaskService;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.RedShadeEnable;

public class DespairWind extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook {

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        LivingEntity attacker = context.getAttacker();
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS));
        if (null != target && !target.level().isClientSide){
            if (context.getPlayerAttacker() instanceof ServerPlayer sp){
                MaskService.colorIsolation(sp, Dreamtinker.getLocation("modifier/despair_wind"), RedShadeEnable.get() ? 0xFF8A221C : 0x6E3D3A3A, 50, 0.55F,
                                           1.28F, 100, -1);
            }
            DTHelper.sendVibeBarFx((ServerLevel) target.level(), attacker, target, 0xFF971E1E);
            for (Attribute attr : attributes) {
                AttributeInstance attr_instance = target.getAttribute(attr);
                if (null != attr_instance){
                    UUID uuid = UUID.nameUUIDFromBytes((this.getId() + "." + attr.getDescriptionId()).getBytes());
                    AttributeModifier cur = attr_instance.getModifier(uuid);
                    if ((cur == null)){
                        attr_instance.removeModifier(uuid);
                        attr_instance.addPermanentModifier(new AttributeModifier(uuid, attr.getDescriptionId(), -1,
                                                                                 AttributeModifier.Operation.MULTIPLY_TOTAL));
                    }
                }
            }
            target.setAbsorptionAmount(0);
            target.invulnerableTime = 0;
            if (target instanceof ServerPlayer sp){
                if (false){
                    if (sp.isCreative() || sp.isSpectator())
                        return knockback;
                    boolean keepInv = sp.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
                    if (!keepInv){
                        // 尽量贴近死亡掉落
                        sp.getInventory().dropAll();
                        sp.getInventory().clearContent();
                    }
                }else
                    dropAllEquipmentLikeDeath(target);
            }else {
                dropAllEquipmentLikeDeath(target);
            }

        }
        return knockback;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity victim = DTHelper.getLivingTarget(context.getTarget());
        if (null != victim && !victim.level().isClientSide){
            remove_attributes(victim);
            if (context.getPlayerAttacker() instanceof ServerPlayer sp){
                MaskService.remove(sp, Dreamtinker.getLocation("modifier/despair_wind"));
            }
        }
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        DTDamageUtils.damageHandler(context.getTarget(), DreamtinkerDamageTypes.source(context.getLevel().registryAccess(), DreamtinkerDamageTypes.NULL_VOID,
                                                                                       context.makeDamageSource()), damageAttempted);
        afterMeleeHit(tool, modifier, context, damageAttempted);
    }

    private void remove_attributes(LivingEntity entity) {
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS));
        for (Attribute attr : attributes) {
            AttributeInstance attr_instance = entity.getAttribute(attr);
            if (null != attr_instance){
                UUID uuid = UUID.nameUUIDFromBytes((this.getId() + "." + attr.getDescriptionId()).getBytes());
                attr_instance.removeModifier(uuid);
            }
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != target)
            target.invulnerableTime = 0;
        return false;
    }

    private static void dropAllEquipmentLikeDeath(LivingEntity e) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = e.getItemBySlot(slot);
            if (stack.isEmpty())
                continue;

            // 可选：尊重“消失诅咒”，死亡会直接消失，这里仿真
            if (net.minecraft.world.item.enchantment.EnchantmentHelper.hasVanishingCurse(stack)){
                e.setItemSlot(slot, ItemStack.EMPTY);
                continue;
            }

            ItemEntity drop = e.spawnAtLocation(stack.copy(), 0.5f);
            if (drop != null){
                drop.setDefaultPickUpDelay();
                drop.setDeltaMovement(drop.getDeltaMovement().add(0.0, 0.2, 0.0));
            }
            e.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        super.registerHooks(hookBuilder);
    }
    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }
}
