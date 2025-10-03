package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.crying_obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class in_rain extends ArmorModifier {
    @Override
    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
        return modifierValue - 80;
    }

    @Override
    public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
        // no point trying if not on the ground
        Level level = living.level();
        if (tool.isBroken() || !living.onGround() || level.isClientSide)
            return;


        BlockState state = level.getBlockState(newPos);
        BlockState below_state = level.getBlockState(newPos.below());
        if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE))
            level.setBlockAndUpdate(newPos, Blocks.AIR.defaultBlockState());
        if (state.getBlock() instanceof FarmBlock)
            level.setBlock(newPos, state.setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE), 3);
        if (state.is(Blocks.LAVA))
            level.setBlockAndUpdate(newPos, Blocks.OBSIDIAN.defaultBlockState());
        if (below_state.is(Blocks.LAVA))
            level.setBlockAndUpdate(newPos.below(), Blocks.OBSIDIAN.defaultBlockState());
    }

    private static final String ONE_KEY = "dreamtinker:in_rain";

    public static boolean shouldApply(LivingEntity e, DamageSource src) {
        var tag = e.getPersistentData();
        long now = e.level().getGameTime();
        // 生成“本次伤害”的指纹：tick + source 标识（可换成更稳妥的 id）
        long stamp = (now << 8) ^ src.getMsgId().hashCode();
        long last = tag.getLong(ONE_KEY);
        if (last == stamp)
            return false;
        tag.putLong(ONE_KEY, stamp);
        return true;
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
