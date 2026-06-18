package org.brahypno.dreamtinker.common.event.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.brahypno.dreamtinker.utils.DTPartInfoLookup;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.*;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class moonlightConversion {
    // 缓存: 正在被跟踪的蓝冰掉落物
    private static final Map<UUID, ItemEntity> trackedBlueIce = new HashMap<>();
    private static final Map<UUID, Long> nextConversionTick = new HashMap<>();
    private static final Map<UUID, Long> nextHintTick = new HashMap<>();

    private static final int TRACKING_INTERVAL = 5;
    private static final int CONVERSION_INTERVAL = 20;
    private static final int HINT_INTERVAL = 20;
    private static final List<MaterialStatsId> MOONLIGHT_PART_STATS = List.of(
            HeadMaterialStats.ID,
            StatlessMaterialStats.BINDING.getIdentifier(),
            HandleMaterialStats.ID
    );

    // 当蓝冰掉落到世界中（被玩家丢出）
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof ItemEntity item))
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
        long gameTime = level.getGameTime();
        if (gameTime % TRACKING_INTERVAL != 0)
            return;

        int moonPhase = level.getMoonPhase();
        boolean allowedMoonlight = level.isNight() && (moonPhase == 0 || moonPhase == 4);

        Iterator<Map.Entry<UUID, ItemEntity>> iterator = trackedBlueIce.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, ItemEntity> entry = iterator.next();
            UUID id = entry.getKey();
            ItemEntity item = entry.getValue();

            if (item == null || item.isRemoved() || !item.isAlive() || !item.getItem().is(Items.BLUE_ICE)){
                iterator.remove();
                nextConversionTick.remove(id);
                nextHintTick.remove(id);
                continue;
            }

            if (!isTouchingWater(level, item))
                continue;

            if (!allowedMoonlight){
                showWrongMoonHint(level, item, id, gameTime);
                continue;
            }

            if (gameTime < nextConversionTick.getOrDefault(id, 0L))
                continue;

            convertBlueIceByCost(level, item, iterator, id, gameTime);
        }
    }

    private static void convertBlueIceByCost(Level level, ItemEntity item, Iterator<Map.Entry<UUID, ItemEntity>> iterator, UUID id, long gameTime) {
        ItemStack stack = item.getItem();
        int maxCost = stack.getCount();

        DTPartInfoLookup.CostedPart result = DTPartInfoLookup.runtimePartWithCost(level.getRecipeManager(), level.registryAccess(),
                                                                                  DreamtinkerMaterialIds.moonlight_ice.getId(), MOONLIGHT_PART_STATS, maxCost,
                                                                                  level.random);

        if (result.isEmpty())
            return;

        level.addFreshEntity(new ItemEntity(level, item.getX(), item.getY(), item.getZ(), result.stack()));

        stack.shrink(result.cost());
        nextConversionTick.put(id, gameTime + CONVERSION_INTERVAL);

        if (stack.isEmpty()){
            item.discard();
            iterator.remove();
            nextConversionTick.remove(id);
            nextHintTick.remove(id);
        }else {
            item.setItem(stack);
        }
    }

    private static void showWrongMoonHint(Level level, ItemEntity item, UUID id, long gameTime) {
        if (gameTime < nextHintTick.getOrDefault(id, 0L))
            return;

        nextHintTick.put(id, gameTime + HINT_INTERVAL);

        if (!(level instanceof ServerLevel serverLevel))
            return;

        serverLevel.sendParticles(ParticleTypes.SMOKE, item.getX(), item.getY() + 0.25D, item.getZ(), 4, 0.15D, 0.08D, 0.15D, 0.01D);
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, item.getX(), item.getY() + 0.25D, item.getZ(), 3, 0.12D, 0.06D, 0.12D, 0.005D);
        level.playSound(null, item.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.35F, 0.55F + level.random.nextFloat() * 0.15F);
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide)
            return;
        trackedBlueIce.entrySet().removeIf(entry -> {
            boolean removing = entry.getValue().level() == level;
            if (removing){
                nextConversionTick.remove(entry.getKey());
                nextHintTick.remove(entry.getKey());
            }
            return removing;
        });
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        trackedBlueIce.clear();
        nextConversionTick.clear();
        nextHintTick.clear();
        DTPartInfoLookup.clearCaches();
    }

    private static boolean isTouchingWater(Level level, ItemEntity item) {
        AABB box = item.getBoundingBox().inflate(0.05D);

        int minX = Mth.floor(box.minX);
        int maxX = Mth.floor(box.maxX);
        int minY = Mth.floor(box.minY);
        int maxY = Mth.floor(box.maxY);
        int minZ = Mth.floor(box.minZ);
        int maxZ = Mth.floor(box.maxZ);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    pos.set(x, y, z);
                    if (level.getFluidState(pos).is(FluidTags.WATER)){
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
