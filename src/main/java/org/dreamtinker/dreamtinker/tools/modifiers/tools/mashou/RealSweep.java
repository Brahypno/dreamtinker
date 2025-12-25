package org.dreamtinker.dreamtinker.tools.modifiers.tools.mashou;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.Entity.SlashOrbitEntity;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.joml.Vector3f;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.realSweepRange;

public class RealSweep extends BattleModifier {

    public void superSweep(IToolStackView tool, ModifierEntry entry, Player player, Level level, Entity entity) {
        if (!level.isClientSide && player.getAttackStrengthScale(0) > 0.8 && !tool.isBroken() && player.getOffhandItem().isEmpty()){
            AttributeInstance reach = player.getAttribute(ForgeMod.ENTITY_REACH.get());
            double range = null != reach ? Math.min(realSweepRange.get(), reach.getValue()) : 1;
            if (range > 0){
                double rangeSq = range * range;
                for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range, 0.25D, range))) {
                    if (aoeTarget != player && !player.isAlliedTo(aoeTarget) && !(aoeTarget instanceof ArmorStand stand && stand.isMarker()) &&
                        player.distanceToSqr(aoeTarget) < rangeSq && aoeTarget != entity && entity != player.getRootVehicle()){
                        ToolAttackUtil.performAttack(tool, ToolAttackContext.attacker(player).target(aoeTarget).cooldown(1).applyAttributes().build());
                    }
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                //sweepArcRightToLeft((ServerLevel) level, player, range - 3, range, 360.0, 2000);
                SlashOrbitEntity e = new SlashOrbitEntity((ServerLevel) level, player, 6, 15, 0.35f, 6, 0, 4);
                e.setSolidColor(0xFF000000)
                 .setHueShift(0.02f);
                level.addFreshEntity(e);

            }
        }
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        superSweep(tool, entry, player, level, null);
    }

    @Override
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        superSweep(tool, entry, player, level, null);
    }

    @Override
    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        superSweep(tool, entry, player, level, target);
    }

    private static void sweepArcRightToLeft(ServerLevel level, LivingEntity entity, double innerRadius, double outerRadius, double arcAngleDeg, int durationTicks) {
        double centerY = entity.getY() + entity.getBbHeight() * 0.6;
        double centerX = entity.getX();
        double centerZ = entity.getZ();
        float yaw = entity.getYRot() + 90;
        if (180 < yaw)
            yaw = yaw - 360;

        // 每 tick 扫过的角度
        double anglePerTick = arcAngleDeg / durationTicks;

        for (int tick = 0; tick < durationTicks; tick++) {
            final int currentTick = tick;

            float finalYaw = yaw;
            level.getServer().execute(() -> {
                // 起始角度 = 玩家面朝 + arc/2 (右边界)，逐渐向左减小
                double angle = Math.toRadians(finalYaw + arcAngleDeg / 2 - currentTick * anglePerTick);
                //System.out.println("yaw:" + finalYaw + " angle: " + angle);

                int stepsRadius = 7;
                for (int j = 0; j <= stepsRadius; j++) {
                    double r = innerRadius + (outerRadius - innerRadius) * j / (double) stepsRadius;
                    double px = centerX + r * Math.cos(angle);
                    double pz = centerZ + r * Math.sin(angle);

                    level.sendParticles(new DustColorTransitionOptions(new Vector3f(0.0F, 0.0F, 0.0F), // 黑色起始
                                                                       new Vector3f(1.0F, 1.0F, 1.0F), // 白色渐隐
                                                                       1.0F), px, centerY, pz, 1, 0, 0, 0, 0);
                }
            });
        }
    }

    @Override
    public boolean isNoLevels() {return false;}
}
