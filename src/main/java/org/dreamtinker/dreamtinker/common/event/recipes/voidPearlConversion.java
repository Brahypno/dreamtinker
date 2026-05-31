package org.dreamtinker.dreamtinker.common.event.recipes;

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
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.voidPearlDropRate;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class voidPearlConversion {
    private static final Map<UUID, ItemEntity> trackedEnderPearl = new HashMap<>();
    private static final Map<UUID, Vec3> trackedPosition = new HashMap<>();

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof ItemEntity item))
            return;
        if (!item.getItem().is(Tags.Items.ENDER_PEARLS))
            return;
        trackedEnderPearl.put(item.getUUID(), item);
        trackedPosition.put(item.getUUID(), item.position());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide)
            return;
        Level level = event.level;
        Iterator<Map.Entry<UUID, ItemEntity>> iterator = trackedEnderPearl.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ItemEntity> entry = iterator.next();
            ItemEntity item = entry.getValue();
            if (level != item.level())
                continue;
            // 如果已消失或不在当前世界，移除
            if (!item.isAlive()){
                iterator.remove();
                trackedPosition.remove(entry.getKey());
                continue;
            }
            if (item.getItem().is(DreamtinkerCommon.void_pearl.get())){
                Vec3 pos = item.position();
                Vec3 tgt = trackedPosition.get(entry.getKey());
                if (tgt == null){
                    iterator.remove();
                    continue;
                }
                Vec3 delta = tgt.subtract(pos);
                double dist = delta.length();

                if (dist < 0.15){
                    // 到达：就位、速度清零、继续悬浮（可拾取）
                    item.setPos(tgt.x, tgt.y, tgt.z);
                    item.setDeltaMovement(Vec3.ZERO);
                    item.setNoGravity(true);
                    item.setPickUpDelay(0);
                    item.setUnlimitedLifetime();
                    trackedPosition.remove(entry.getKey());
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
                        // 继续使用 trackedPosition 作为返航目标
                    }else {
                        iterator.remove();
                        trackedPosition.remove(entry.getKey());
                        // 不 discard，让原版虚空自然移除
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide)
            return;

        Iterator<Map.Entry<UUID, ItemEntity>> iterator = trackedEnderPearl.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ItemEntity> entry = iterator.next();
            if (entry.getValue().level() == level){
                trackedPosition.remove(entry.getKey());
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        trackedEnderPearl.clear();
        trackedPosition.clear();
    }
}
