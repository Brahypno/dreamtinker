package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.BaseExplodeDefenseRate;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.ExplodeDefenseRatePerLevel;

public class ExplosiveDefense extends Modifier implements DamageBlockModifierHook {
    private static final ThreadLocal<Boolean> DREAMTINKER_COUNTER_BLAST = ThreadLocal.withInitial(() -> false);

    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("ender_dodge"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK);
        super.registerHooks(hookBuilder);
    }

    private static final String NBT_COUNTER_BLAST_UNTIL = "dreamtinker_counter_blast_until";

    public static boolean isCounterBlastLocked(LivingEntity entity) {
        CompoundTag tag = entity.getPersistentData();
        long now = entity.level().getGameTime();
        long until = tag.getLong(NBT_COUNTER_BLAST_UNTIL);

        if (now <= until){
            return true;
        }

        tag.remove(NBT_COUNTER_BLAST_UNTIL);
        return false;
    }

    public static void lockCounterBlast(LivingEntity entity, int ticks) {
        entity.getPersistentData().putLong(
                NBT_COUNTER_BLAST_UNTIL,
                entity.level().getGameTime() + Math.max(0, ticks)
        );
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description",
                                                    String.format("%.0f%%", BaseExplodeDefenseRate.get() * 100),
                                                    String.format("%.0f%%", ExplodeDefenseRatePerLevel.get() * 100))
                                      .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < amount && 0 < level && !DREAMTINKER_COUNTER_BLAST.get()){
            LivingEntity holder = context.getEntity();
            if (!isCounterBlastLocked(holder) && holder.level() instanceof ServerLevel sl){
                DREAMTINKER_COUNTER_BLAST.set(true);
                lockCounterBlast(holder, 1);
                try {
                    Vec3 center = holder.position();
                    // ===== 可调参数 =====
                    double radius = 1.8D + 0.9D * level;               // 范围随等级成长
                    float damageFactor = BaseExplodeDefenseRate.get().floatValue() + ExplodeDefenseRatePerLevel.get().floatValue() * (level - 1);
                    double horizontalKnockback = 0.35D + 0.18D * level;
                    double verticalKnockback = Math.min(0.12D + 0.05D * level, 0.45D);

                    float reflectedDamage = amount * damageFactor;

                    AABB area = holder.getBoundingBox().inflate(radius);
                    boolean played = false;

                    for (LivingEntity target : sl.getEntitiesOfClass(LivingEntity.class, area)) {
                        if (target == holder){
                            continue;
                        }
                        if (!target.isAlive() || target.isInvulnerable()){
                            continue;
                        }

                        Vec3 delta = target.position().subtract(center);
                        double distSqr = delta.lengthSqr();
                        if (distSqr < 1.0e-6){
                            delta = new Vec3(0, 0, 1);
                            distSqr = 1.0;
                        }

                        double dist = Math.sqrt(distSqr);
                        if (dist > radius){
                            continue;
                        }

                        // 距离衰减：边缘更低，中心更高
                        float falloff = (float) (1.0D - dist / radius);
                        if (falloff <= 0){
                            continue;
                        }

                        float finalDamage = reflectedDamage * (0.4f + 0.6f * falloff);
                        if (finalDamage < 0.1f)
                            continue;

                        // 伤害来源：若有直接攻击者则沿用其上下文，否则用普通爆炸
                        DamageSource blastSource = sl.damageSources().explosion(holder, holder);

                        target.hurt(blastSource, finalDamage);

                        Vec3 pushVec = delta.normalize().scale(horizontalKnockback * (0.35D + 0.65D * falloff));
                        target.push(pushVec.x, verticalKnockback * (0.35D + 0.65D * falloff), pushVec.z);
                        target.hurtMarked = true;
                        if (!played){
                            played = true;
                            // 视觉/音效
                            sl.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 1.0D, center.z, 1, 0, 0, 0, 0);
                            sl.playSound(null, holder.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.9f, 1.0f);
                        }
                    }
                }
                finally {
                    DREAMTINKER_COUNTER_BLAST.set(false);
                }
            }
        }
        return false;
    }
}
