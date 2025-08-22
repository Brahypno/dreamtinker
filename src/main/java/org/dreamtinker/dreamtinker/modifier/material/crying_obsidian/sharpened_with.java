package org.dreamtinker.dreamtinker.modifier.material.crying_obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.CryingDamageBoost;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.CryingParticles;

public class sharpened_with extends BattleModifier {
    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity attacker = context.getAttacker();
        ServerLevel level = (ServerLevel) attacker.level();
        BlockPos pos = null == context.getLivingTarget() ? attacker.getOnPos() : context.getLivingTarget().getOnPos();
        if (canRainAt(level, pos))
            level.setWeatherParameters(0, 6000, true, false);
        else {
            AABB box = new AABB(pos).inflate(8, 6, 8); // 半径8、向上6
            for (int i = 0; i < 120 * CryingParticles.get(); i++) {
                double x = Mth.nextDouble(level.random, box.minX, box.maxX);
                double z = Mth.nextDouble(level.random, box.minZ, box.maxZ);
                double yTop = box.maxY;
                level.sendParticles(ParticleTypes.FALLING_WATER, x, yTop, z, 1, 0, 0, 0, 0);
            }
            // 播放雨声（广播）
            level.playSound(null, pos, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.6F, 1.0F);
        }
        return knockback;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return (float) (damage * (1 + CryingDamageBoost.get()));
    }

    private static boolean canRainAt(ServerLevel level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight())
            return false;                                // 头顶无遮挡
        var biome = level.getBiome(pos).value();
        return biome.getPrecipitationAt(pos) != Biome.Precipitation.NONE;
    }
}
