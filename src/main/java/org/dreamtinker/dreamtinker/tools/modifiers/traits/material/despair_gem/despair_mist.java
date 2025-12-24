package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony.the_wolf_wonder.filterMobEffects;

public class despair_mist extends BattleModifier {
    private final ResourceLocation TAG_MIST = Dreamtinker.getLocation("despair_mist");

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public boolean isNoLevels() {return false;}

    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        if (!level.isClientSide)
            InfectDespair(tool, player, entry.getLevel());
    }

    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        if (!level.isClientSide)
            InfectDespair(tool, player, entry.getLevel());
    }

    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        if (!level.isClientSide)
            InfectDespair(tool, player, entry.getLevel());
    }

    private void InfectDespair(IToolStackView tool, Player player, int level) {
        int mist = tool.getPersistentData().getInt(TAG_MIST);
        AttributeInstance reach = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        double range = null != reach ? reach.getValue() : 1;
        range += level;
        AABB box = player.getBoundingBox().inflate(range);
        List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                LivingEntity.class, box, LivingEntity::isAlive);
        Map<MobEffect, MobEffectInstance> best = collectBestNegatives(nearby, level);
        for (LivingEntity livingEntity : nearby) {
            if (player.isAlliedTo(livingEntity) || livingEntity instanceof ArmorStand || livingEntity.is(player) ||
                null != livingEntity.getTeam() && player.isAlliedTo(livingEntity.getTeam()))
                continue;
            List<MobEffectInstance> snapshot = new ArrayList<>(livingEntity.getActiveEffects());
            for (MobEffectInstance inst : snapshot)
                if (inst.getEffect().isBeneficial()){
                    livingEntity.removeEffect(inst.getEffect());
                    mist++;
                }

            for (MobEffectInstance inst : best.values()) {
                if (!livingEntity.hasEffect(inst.getEffect()))
                    mist++;
                livingEntity.forceAddEffect(inst, player);
            }
        }
        if (9 < mist){
            for (LivingEntity livingEntity : nearby) {
                if (player.isAlliedTo(livingEntity) || livingEntity instanceof ArmorStand || livingEntity.is(player) ||
                    null != livingEntity.getTeam() && player.isAlliedTo(livingEntity.getTeam()))
                    continue;
                ToolAttackUtil.performAttack(tool, ToolAttackContext.attacker(player).target(livingEntity).cooldown(1).applyAttributes().build());
            }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
            mist -= 9;
        }
        tool.getPersistentData().putInt(TAG_MIST, mist);
    }

    public static Map<MobEffect, MobEffectInstance> collectBestNegatives(List<LivingEntity> nearby, int level) {
        Map<MobEffect, MobEffectInstance> best = new HashMap<>();

        for (LivingEntity e : nearby) {
            for (MobEffectInstance inst : e.getActiveEffects()) {
                MobEffect eff = inst.getEffect();
                if (!filterMobEffects(eff))
                    continue;

                MobEffectInstance cur = best.get(eff);
                if (cur == null
                    || inst.getAmplifier() > cur.getAmplifier()
                    || (inst.getAmplifier() == cur.getAmplifier() && inst.getDuration() > cur.getDuration())){
                    // 取更高等级；若等级相同取更久时间；保留可见性/图标等标志位
                    best.put(eff, new MobEffectInstance(
                            eff,
                            inst.getDuration() + level * 20,
                            inst.getAmplifier() + e.level().random.nextFloat() < 0.1 ? (eff.equals(MobEffects.HARM) ? 0 : 1) : 0,
                            inst.isAmbient(),
                            inst.isVisible(),
                            inst.showIcon()
                    ));
                }
            }
        }
        return best;
    }
}
