package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.dreamtinker.dreamtinker.utils.MaskService;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.DespairShade;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.RedShadeEnable;

public class DespairWind extends BattleModifier {
    private static final String TAG_extra_hit = Dreamtinker.getLocation("extra_hit").toString();

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (holder instanceof ServerPlayer player){
            if ((isCorrectSlot || isSelected) && world.getGameTime() % 20 == 0)
                if (world.random.nextFloat() < DespairShade.get())
                    MaskService.ensureOn(player, RedShadeEnable.get() ? 0x568A221C : 0x6E3D3A3A, 100);
            MaskService.ensureOff(player, 100);
        }
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        LivingEntity attacker = context.getAttacker();
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS));
        if (null != target && !target.level().isClientSide){
            DTHelper.sendVibeBarFx((ServerLevel) target.level(), attacker, target, 0xFF971E1E);
            for (Attribute attr : attributes) {
                AttributeInstance attr_instance = target.getAttribute(attr);
                if (null != attr_instance){
                    UUID uuid = UUID.nameUUIDFromBytes((this.getId() + "." + attr.getDescriptionId()).getBytes());
                    AttributeModifier cur = attr_instance.getModifier(uuid);
                    if ((cur == null)){
                        attr_instance.removeModifier(uuid);
                        attr_instance.addPermanentModifier(new AttributeModifier(uuid, attr.getDescriptionId(), attr_instance.getValue(),
                                                                                 AttributeModifier.Operation.ADDITION));
                    }
                }
            }
            target.setAbsorptionAmount(0);
            target.invulnerableTime = 0;
            if (target instanceof ServerPlayer sp){
                if (sp.isCreative() || sp.isSpectator())
                    return knockback;

                boolean keepInv = sp.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
                if (!keepInv){
                    // 尽量贴近死亡掉落
                    sp.getInventory().dropAll();
                    sp.getInventory().clearContent();
                }
            }else {
                dropAllEquipmentLikeDeath(target);
            }

        }
        return knockback;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity victim = context.getLivingTarget();
        if (null != victim && !victim.level().isClientSide){
            CompoundTag data = victim.getPersistentData();
            if (data.getLong(TAG_extra_hit) < victim.level().getGameTime()){
                RegistryAccess registryAccess = victim.level().registryAccess();
                victim.hurt(DreamtinkerDamageTypes.source(registryAccess, DreamtinkerDamageTypes.NULL_VOID, null, context.getAttacker()), damageDealt);
                data.putLong(TAG_extra_hit, victim.level().getGameTime());
            }
            remove_attributes(context.getLivingTarget());
        }
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        if (null != context.getLivingTarget() && !context.getLivingTarget().level().isClientSide)
            remove_attributes(context.getLivingTarget());
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
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
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
}
