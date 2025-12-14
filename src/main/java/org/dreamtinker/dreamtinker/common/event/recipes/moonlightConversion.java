package org.dreamtinker.dreamtinker.common.event.recipes;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class moonlightConversion {
    // 缓存: 正在被跟踪的蓝冰掉落物
    private static final Map<UUID, ItemEntity> trackedBlueIce = new HashMap<>();

    // 当蓝冰掉落到世界中（被玩家丢出）
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity item))
            return;
        if (item.getItem().getItem() != Items.BLUE_ICE)
            return;
        // 跟踪蓝冰
        trackedBlueIce.put(item.getUUID(), item);
    }

    // 每 tick 检查蓝冰是否浸入水中
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide)
            return;
        Level level = event.level;
        Iterator<Map.Entry<UUID, ItemEntity>> iterator = trackedBlueIce.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ItemEntity> entry = iterator.next();
            ItemEntity item = entry.getValue();
            if (level != item.level())
                continue;
            // 如果已消失或不在当前世界，移除
            if (!item.isAlive()){
                iterator.remove();
                continue;
            }

            // 检查其当前位置是否为水
            int moonPhase = level.getMoonPhase();
            boolean isAllowedPhase = (moonPhase == 0 || moonPhase == 4) && level.isNight();

            if (item.isInWater()){
                if (!isAllowedPhase){//
                    level.playSound(item, item.blockPosition(), SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.BLOCKS, 0.5F, 0.8F);
                    level.addParticle(ParticleTypes.SMOKE, item.getX(), item.getY() + 0.2, item.getZ(), 0, 0.01, 0);
                    continue;
                }
                // 转换蓝冰
                ItemStack result = DTHelper.getPart(DreamtinkerMaterialIds.moonlight_ice.getId(), HeadMaterialStats.ID, level.random);
                if (!result.isEmpty()){
                    level.addFreshEntity(new ItemEntity(level, item.getX(), item.getY(), item.getZ(), result));
                }
                item.discard(); // 移除原蓝冰
                iterator.remove();
            }
        }
    }
}
