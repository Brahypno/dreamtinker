package org.dreamtinker.dreamtinker.modifier.material.echo_shard;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static net.minecraft.nbt.Tag.TAG_INT;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.EchoAttackCharge;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.EchoAttackChargingChance;
import static slimeknights.tconstruct.library.tools.helper.ToolAttackUtil.getAttributeAttackDamage;

public class echoed_attack extends BattleModifier {
    private static final ResourceLocation TAG_ECHO_ENERGY = new ResourceLocation(Dreamtinker.MODID, "echo_energy");
    private static final int E_C = EchoAttackCharge.get();
    private static final double ChargingChance = EchoAttackChargingChance.get();

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_ECHO_ENERGY);
        return null;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool instanceof ToolStack && tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            if (nbt.contains(TAG_ECHO_ENERGY, TAG_INT)){
                int count = nbt.getInt(TAG_ECHO_ENERGY);
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.echo_energy").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        ModDataNBT nbt = tool.getPersistentData();
        int count = nbt.getInt(TAG_ECHO_ENERGY);
        if (E_C <= count){
            count -= E_C;
            performSonicBoomSweep(tool, (ServerLevel) context.getAttacker().level(), context.getAttacker());
            nbt.putInt(TAG_ECHO_ENERGY, count);
        }
        return knockback;
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (!(shooter.level() instanceof ServerLevel))
            return;
        ModDataNBT nbt = tool.getPersistentData();
        int count = nbt.getInt(TAG_ECHO_ENERGY) + 1;
        if (projectile instanceof AbstractArrow && null != arrow && Math.random() < ChargingChance){
            ((AbstractArrow) projectile).setBaseDamage(((AbstractArrow) projectile).getBaseDamage() * 1.5);
            count++;
        }
        if (E_C <= count){
            count -= E_C;
            nbt.putInt(TAG_ECHO_ENERGY, count);
            performSonicBoomSweep(tool, (ServerLevel) shooter.level(), shooter);
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        hitEntity(tool, context.getAttacker(), context.getLivingTarget(), context);
    }

    @Override
    public int getPriority() {
        return 10;
    }

    private void hitEntity(IToolStackView tool, LivingEntity attacker, LivingEntity target, ToolAttackContext context) {
        int echo_energy = 1;
        if (null != target && Math.random() < ChargingChance){
            shortCutDamage(tool, context);
            for (LivingEntity entity : new LivingEntity[]{attacker, target}) {
                if (entity != null){
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60));
                }
            }
            echo_energy++;
        }

        ModDataNBT nbt = tool.getPersistentData();
        int count = nbt.getInt(TAG_ECHO_ENERGY);
        nbt.putInt(TAG_ECHO_ENERGY, count + echo_energy);
    }

    private void shortCutDamage(IToolStackView tool, ToolAttackContext context) {
        float damage = getAttributeAttackDamage(tool, context.getAttacker(), Util.getSlotType(
                tool.getItem().equals(context.getAttacker().getMainHandItem().getItem()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND));
        float baseDamage = damage;
        List<ModifierEntry> modifiers = tool.getModifierList();
        for (ModifierEntry entry : modifiers) {
            damage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, entry, context, baseDamage, damage);
        }
        Objects.requireNonNull(context.getLivingTarget()).setInvulnerable(false);
        DamageSource dam;
        if (context.getAttacker() instanceof Player player)
            dam = context.getLivingTarget().level().damageSources().playerAttack(player);
        else
            dam = context.getLivingTarget().level().damageSources().mobAttack(context.getAttacker());
        context.getLivingTarget().hurt(dam, damage);
    }

    public static void performSonicBoomSweep(IToolStackView tool, ServerLevel level, LivingEntity attacker) {
        if (level.isClientSide)
            return;
        //Sonic Boom damage depend on range modifier!
        List<ModifierEntry> modifiers = tool.getModifierList();
        Arrow arrow = new Arrow(level, attacker);
        ModDataNBT persistentData = ModDataNBT.readFromNBT(arrow.getPersistentData());
        ModDataNBT nbt = tool.getPersistentData();
        int count = nbt.getInt(TAG_ECHO_ENERGY);//In case count is modified due to below hook.
        for (ModifierEntry entry : modifiers) {
            entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, attacker, arrow, arrow, persistentData, true);
        }
        float damage = (float) (arrow.getDeltaMovement().length() * arrow.getBaseDamage());
        nbt.putInt(TAG_ECHO_ENERGY, count);

        //
        Vec3 start = attacker.position().add(0, attacker.getEyeHeight(), 0);
        Vec3 direction = attacker.getLookAngle(); // 玩家视线方向
        double maxDistance = 20.0;

        // 记录已攻击的实体，防止重复
        Set<LivingEntity> hitEntities = new HashSet<>();

        // 每格进行检测
        for (int i = 1; i <= maxDistance; i++) {
            Vec3 pos = start.add(direction.scale(i));

            // 发出粒子
            level.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);

            // 检测这格内的实体（半径1格球形范围）
            List<LivingEntity> entities =
                    level.getEntitiesOfClass(LivingEntity.class, new AABB(pos.x - 0.5, pos.y - 0.5, pos.z - 0.5, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5),
                                             e -> e != attacker && e.isAlive() && attacker.canAttack(e));

            for (LivingEntity target : entities) {
                if (hitEntities.contains(target))
                    continue;

                hitEntities.add(target);

                // 造成伤害
                target.hurt(target.level().damageSources().sonicBoom(attacker), damage);

                // 计算击退（同 Warden）
                double resist = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                double knockY = 0.5 * (1.0 - resist);
                double knockXZ = 2.5 * (1.0 - resist);
                target.push(direction.x * knockXZ, direction.y * knockY, direction.z * knockXZ);
            }
        }

        // 播放音效
        attacker.level().playSound(null, attacker.getOnPos(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL, 3.0F, 1.0F);
    }
}
