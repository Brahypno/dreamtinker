package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class lightRangeBoost extends BattleModifier {
    @Override
    public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
        if (ToolStats.ACCURACY == stat || ToolStats.DRAW_SPEED == stat || ToolStats.PROJECTILE_DAMAGE == stat){
            baseValue += 1 + lightCurve(living);
        }
        return baseValue;
    }

    private static float lightCurve(LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        int skyDarken = entity.level().getSkyDarken();                  // 夜晚/天气导致的天光衰减
        int rawBrightness = entity.level().getRawBrightness(pos, skyDarken);
        return 0.25f * (rawBrightness - 7) / 8;
    }
}
