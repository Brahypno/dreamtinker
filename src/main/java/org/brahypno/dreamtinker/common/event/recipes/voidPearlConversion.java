package org.brahypno.dreamtinker.common.event.recipes;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.voidPearlDropRate;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class voidPearlConversion {
    private static final Map<Level, Map<UUID, TrackedPearl>> trackedByLevel = new IdentityHashMap<>();
    private static final int FALL_CHECK_INTERVAL = 5;

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof ItemEntity item))
            return;
        if (!item.getItem().is(Tags.Items.ENDER_PEARLS))
            return;
        trackedByLevel.computeIfAbsent(event.getLevel(), ignored -> new HashMap<>())
                      .put(item.getUUID(), new TrackedPearl(item, item.position()));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide)
            return;
        Level level = event.level;
        Map<UUID, TrackedPearl> tracked = trackedByLevel.get(level);
        if (tracked == null)
            return;
        Iterator<TrackedPearl> iterator = tracked.values().iterator();
        while (iterator.hasNext()) {
            TrackedPearl pearl = iterator.next();
            ItemEntity item = pearl.item();
            // 如果已消失或不在当前世界，移除
            if (level != item.level() || !item.isAlive()){
                iterator.remove();
                continue;
            }
            if (item.getItem().is(DreamtinkerCommon.void_pearl.get())){
                Vec3 pos = item.position();
                Vec3 tgt = pearl.origin();
                Vec3 delta = tgt.subtract(pos);
                double dist = delta.length();

                if (dist < 0.15){
                    // 到达：就位、速度清零、继续悬浮（可拾取）
                    item.setPos(tgt.x, tgt.y, tgt.z);
                    item.setDeltaMovement(Vec3.ZERO);
                    item.setNoGravity(true);
                    item.setPickUpDelay(0);
                    item.setUnlimitedLifetime();
                    iterator.remove();
                }else {
                    // 朝目标移动：速度随距离缩放，带一点阻尼
                    double speed = Math.min(0.45, 0.12 + dist * 0.05); // 近处慢、远处快
                    Vec3 vel = delta.normalize().scale(speed);
                    // 轻微插值以更顺滑
                    Vec3 newVel = item.getDeltaMovement().scale(0.6).add(vel.scale(0.4));
                    item.setDeltaMovement(newVel);
                    item.setNoGravity(true);
                    item.noPhysics = true;
                }
            }else {
                if (level.getGameTime() % FALL_CHECK_INTERVAL != 0)
                    continue;

                double conversionLine = level.getMinBuildHeight() - 56.0D;

                if (item.getY() < conversionLine){
                    int inputCount = item.getItem().getCount();
                    int outputCount = 0;

                    for (int i = 0; i < inputCount; i++) {
                        if (level.getRandom().nextFloat() < voidPearlDropRate.get()){
                            outputCount++;
                        }
                    }

                    if (outputCount > 0){
                        item.setItem(new ItemStack(DreamtinkerCommon.void_pearl.get(), outputCount));
                        item.setNoGravity(true);
                        item.setPickUpDelay(40);
                        // 继续使用入世界时的位置作为返航目标
                    }else {
                        iterator.remove();
                        // 不 discard，让原版虚空自然移除
                    }
                }
            }
        }
        if (tracked.isEmpty()){
            trackedByLevel.remove(level);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide)
            return;

        trackedByLevel.remove(level);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        trackedByLevel.clear();
    }

    private record TrackedPearl(ItemEntity item, Vec3 origin) {}
}
