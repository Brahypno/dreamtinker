package org.dreamtinker.dreamtinker.tools.modifiers.traits.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ArrowHarvest extends Modifier implements ProjectileHitModifierHook {
    private static float computeExplosionPower(Projectile projectile, float basePower, float speedFactor, float maxPower) {
        Vec3 v = projectile.getDeltaMovement();
        double speed = v.length(); // 实际飞行速度
        float power = (float) (basePower + speed * speedFactor);

        // 如果你还有“蓄力/力量”变量，也可以乘进去
        // power *= chargeMultiplier;

        return Mth.clamp(Mth.sqrt(power), 0.5F, maxPower);
    }

    private static ItemStack makeFortuneTool(int fortuneLevel) {
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        if (fortuneLevel > 0){
            stack.enchant(Enchantments.BLOCK_FORTUNE, fortuneLevel);
        }
        return stack;
    }

    private static void destroyBlockWithLootContext(
            ServerLevel level,
            @Nullable Entity source,
            BlockPos pos,
            BlockState state,
            float explosionPower,
            ItemStack lootTool
    ) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        LootParams.Builder params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, source)
                .withOptionalParameter(LootContextParams.TOOL, lootTool)
                .withOptionalParameter(LootContextParams.EXPLOSION_RADIUS, explosionPower);

        List<ItemStack> drops = state.getDrops(params);

        level.removeBlock(pos, false);

        for (ItemStack stack : drops) {
            if (!stack.isEmpty()){
                Block.popResource(level, pos, stack);
            }
        }
    }

    private static boolean shouldDestroyBlockVanillaLike(
            ServerLevel level,
            Explosion explosion,
            Vec3 center,
            BlockPos targetPos,
            BlockState targetState,
            float explosionPower
    ) {
        if (targetState.isAir()){
            return false;
        }

        ExplosionDamageCalculator calculator = new ExplosionDamageCalculator();

        Vec3 targetCenter = Vec3.atCenterOf(targetPos);
        Vec3 delta = targetCenter.subtract(center);
        double length = delta.length();
        if (length < 1.0e-6){
            return calculator.shouldBlockExplode(explosion, level, targetPos, targetState, explosionPower);
        }

        Vec3 step = delta.normalize().scale(0.3D);

        // 这一项相当于“这条路径当前还剩多少爆炸力”
        // 这里给一点随机浮动，会更接近原版观感
        float remainingPower = explosionPower * (0.7F + level.random.nextFloat() * 0.6F);

        Vec3 cursor = center;
        int steps = Mth.ceil(length / 0.3D);

        for (int i = 0; i < steps; i++) {
            BlockPos currentPos = BlockPos.containing(cursor);
            BlockState currentState = level.getBlockState(currentPos);
            FluidState fluidState = level.getFluidState(currentPos);

            if (!currentState.isAir() || !fluidState.isEmpty()){
                Optional<Float> resistanceOpt = calculator.getBlockExplosionResistance(
                        explosion, level, currentPos, currentState, fluidState
                );

                if (resistanceOpt.isPresent()){
                    float resistance = resistanceOpt.get();
                    remainingPower -= (resistance + 0.3F) * 0.3F;
                }
            }

            if (remainingPower <= 0.0F){
                return false;
            }

            cursor = cursor.add(step);
        }

        return calculator.shouldBlockExplode(explosion, level, targetPos, targetState, remainingPower);
    }

    public static void explodeWithFortuneDrops(
            ServerLevel level,
            @Nullable Entity source,
            Vec3 center,
            float explosionPower,
            ItemStack lootTool
    ) {
        int radius = Mth.ceil(explosionPower * 2.0F);
        BlockPos centerPos = BlockPos.containing(center);
        Explosion explosion = new Explosion(
                level,
                source,
                center.x, center.y, center.z,
                explosionPower,
                false,
                Explosion.BlockInteraction.KEEP
        );

        for (BlockPos pos : BlockPos.betweenClosed(centerPos.offset(-radius, -radius, -radius),
                                                   centerPos.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()){
                continue;
            }

            if (shouldDestroyBlockVanillaLike(level, explosion, center, pos, state, explosionPower)){
                destroyBlockWithLootContext(level, source, pos, state, explosionPower, lootTool);
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public int getPriority() {
        return -10000;
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {

        if (!(projectile.level() instanceof ServerLevel level)){
            return false;
        }

        float explosionPower = computeExplosionPower(projectile, 1.5F, 1.2F, 8.0F);

        // 例如根据爆炸强度给不同等级时运
        int fortune = explosionPower >= 6.0F ? 3 : explosionPower >= 4.0F ? 2 : 1;
        ItemStack lootTool = makeFortuneTool((int) (explosionPower + modifier.getLevel() - 1));

        explodeWithFortuneDrops(
                level,
                owner,
                hit.getLocation(),
                explosionPower,
                lootTool
        );
        projectile.discard();
        return false;
    }
}
