package org.dreamtinker.dreamtinker.event.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import slimeknights.tconstruct.shared.TinkerCommons;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.SoulCastLoveLootChance;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class soulCastConversion {
    private static final int RADIUS = 6;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // 1) 事件未被取消 + 仅服务端
        if (event.isCanceled())
            return;
        Level level = event.getEntity().level();
        if (level.isClientSide)
            return;

        // 2) 仅主世界
        ResourceKey<Level> dim = level.dimension();
        if (dim != Level.OVERWORLD || !level.isDay())
            return;

        Entity victim = event.getEntity();
        if (!(victim instanceof Player || victim instanceof WitherBoss || victim instanceof WitherSkeleton || victim instanceof Phantom ||
              victim instanceof Ghast))
            return;

        // 4) 死亡位置位于“建筑上限高度及以上”
        // 注：最高可放置方块的 Y = level.getMaxBuildHeight() - 1
        int yTop = level.getMaxBuildHeight() - 1;
        BlockPos deathPos = event.getEntity().blockPosition();
        if (deathPos.getY() < yTop)
            return;

        int cx = deathPos.getX();
        int cz = deathPos.getZ();

        for (int x = cx - RADIUS; x <= cx + RADIUS; x++) {
            for (int z = cz - RADIUS; z <= cz + RADIUS; z++) {
                BlockPos pos = new BlockPos(x, yTop, z);
                BlockState state = level.getBlockState(pos);

                if (isSoulGlass(state)){
                    // 移除玻璃（不掉落玻璃本体）
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

                    ItemStack soul_cast = new ItemStack(DreamtinkerItems.soul_cast.get(), 1);
                    CompoundTag tag = soul_cast.getOrCreateTag();
                    tag.putBoolean("desire", true);
                    ItemEntity drop = new ItemEntity(level,
                                                     x + 0.5, yTop + 0.5, z + 0.5,
                                                     soul_cast);
                    level.addFreshEntity(drop);
                    return;
                }
            }
        }
    }

    private static boolean isSoulGlass(BlockState state) {
        return state.getBlock().equals(TinkerCommons.soulGlass.get());
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (event.isCanceled())
            return;
        AgeableMob child = event.getChild();
        if (child == null)
            return;
        Level level = child.level();
        if (level.isClientSide)
            return;

        if (level.random.nextFloat() <= 1 - SoulCastLoveLootChance.get())
            return;

        Vec3 p = event.getParentB().position();
        AABB box = new AABB(p.x - RADIUS, p.y - RADIUS, p.z - RADIUS,
                            p.x + RADIUS, p.y + RADIUS, p.z + RADIUS);

        // 扫描范围内的掉落物实体，筛选出“羽毛”
        System.out.println(child.position());
        for (ItemEntity ie : level.getEntitiesOfClass(ItemEntity.class, box,
                                                      e -> e.isAlive() && !e.getItem().isEmpty() && e.getItem().is(Tags.Items.FEATHERS))) {
            ItemStack feather = ie.getItem();
            int count = feather.getCount();

            ItemStack soul_cast = new ItemStack(DreamtinkerItems.soul_cast.get(), count);
            CompoundTag tag = soul_cast.getOrCreateTag();
            tag.putBoolean("love", true);
            ie.setItem(soul_cast);
        }
    }
}
