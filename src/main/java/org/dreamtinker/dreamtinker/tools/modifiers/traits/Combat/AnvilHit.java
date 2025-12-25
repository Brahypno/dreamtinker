package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.utils.ProjectileHitMemory;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class AnvilHit extends BattleModifier {

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (null != context.getLivingTarget() && context.getAttacker().level() instanceof ServerLevel level && !level.isClientSide)
            spawnAnvilSmash(level, context.getLivingTarget(), modifier.getLevel(), 3, (int) damageDealt);
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        if (null != context.getLivingTarget() && context.getAttacker().level() instanceof ServerLevel level && !level.isClientSide)
            spawnAnvilSmash(level, context.getLivingTarget(), modifier.getLevel(), 3, (int) damage);
    }


    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != target && target.level() instanceof ServerLevel level && !level.isClientSide){
            String anvil_hit = "dreamtinker_anvil_hit_range";
            if (!ProjectileHitMemory.hasTriggered(anvil_hit, projectile, target.getUUID())){
                spawnAnvilSmash(level, target, modifier.getLevel(), 3, 100);
                ProjectileHitMemory.markTriggered(anvil_hit, projectile, target.getUUID());
            }

        }
        return false;
    }

    private static void spawnAnvilSmash(
            ServerLevel level, LivingEntity target,
            double spawnHeight, float dmgPerBlock, int dmgMax) {
        // 生成位置：目标正上方 spawnHeight 格
        BlockPos spawn = target.blockPosition().above((int) Math.max(3, spawnHeight));
        FallingBlockEntity anvil = FallingBlockEntity.fall(
                level,
                spawn,
                Blocks.ANVIL.defaultBlockState()
        );

        // 造成砸落伤害（参数：每格伤害、最大伤害）
        anvil.setHurtsEntities(dmgPerBlock, dmgMax);

        // 不掉落物品
        anvil.disableDrop();

        // 打上标记，用于落地时阻止转成方块并立即清理
        anvil.getPersistentData().putBoolean("dt_no_place", true);

        // 让它尽快进入下落态
        anvil.time = 1;

        //level.addFreshEntity(anvil);
    }
}
