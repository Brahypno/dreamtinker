package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.thunderCurse;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class thundering_curse {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level world = player.level();
        if (!world.isClientSide && world.isThundering() && world.getGameTime() % 200 == 0){
            int wearingConductive = DTModifierCheck.getEntityModifierNum(player, DreamtinkerModifiers.Ids.thundering_curse);
            if (world.random.nextFloat() < thunderCurse.get() * wearingConductive){
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(world);
                if (bolt != null){
                    bolt.moveTo(player.getX(), player.getY(), player.getZ());
                    world.addFreshEntity(bolt);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.level.isClientSide())
            return;
        if (!(e.level instanceof ServerLevel level))
            return;
        if (!level.dimension().equals(Level.OVERWORLD))
            return;
        if (!level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE))
            return;

        // 1) 统计加载区玩家并计算倾向值
        float stormBias = 0f; // 建议 0.0 ~ 1.0
        for (ServerPlayer p : level.players()) {
            if (p.isSpectator())
                continue;
            // 仅统计“实体可tick的区块”（说明所在区已加载并活跃）
            if (!level.getChunkSource().isPositionTicking(p.chunkPosition().toLong()))
                continue;
            stormBias += DTModifierCheck.getEntityModifierNum(p, DreamtinkerModifiers.Ids.thundering_curse);
        }
        stormBias = (float) Math.min(stormBias * thunderCurse.get(), 1.0f);

        // 2) 读取当前天气状态
        ServerLevelData data = (ServerLevelData) level.getLevelData();
        boolean raining = data.isRaining();
        boolean thundering = data.isThundering();
        int rainTime = data.getRainTime();
        int thunderTime = data.getThunderTime();
        int clearTime = data.getClearWeatherTime();

        if (level.getGameTime() % 6000L == 0){//6000=20*60*5
            var rand = level.random;

            // 晴 -> 雨：受 stormBias 提高概率
            if (!raining && clearTime < 20 * 60){
                float base = 0.02f;               // 原来很低的自然几率
                float chance = base + 0.25f * stormBias;
                if (rand.nextFloat() < chance){
                    // 设置为下雨，时间段适当加长
                    startRain(level, data, 6000 + (int) (12000 * stormBias));
                }
            }

            // 雨 -> 雷：受 stormBias 提高概率；同时延长雷暴时长
            if (raining && !thundering){
                float base = 0.05f;
                float chance = base + 0.35f * stormBias;
                if (rand.nextFloat() < chance){
                    startThunder(level, data, 3000 + (int) (9000 * stormBias));
                }
            }
            /*
            // 雷 -> 维持：stormBias 越高越难结束
            if (thundering){
                // 若剩余雷暴时间很短，按倾向补点时长
                if (thunderTime < 200 && rand.nextFloat() < 0.25f * stormBias){
                    data.setThunderTime(thunderTime + 1000 + (int) (4000 * stormBias));
                    data.setRaining(true);
                    data.setRainTime(Math.max(data.getRainTime(), data.getThunderTime()));
                }
            }*/
        }
    }

    private static void startRain(ServerLevel level, ServerLevelData data, int duration) {
        data.setClearWeatherTime(0);
        data.setRaining(true);
        data.setRainTime(duration);
        data.setThundering(false);
        // 某些版本也可用 level.setWeatherParameters(...)，但直接写 data 更直观可控
    }

    private static void startThunder(ServerLevel level, ServerLevelData data, int duration) {
        data.setClearWeatherTime(0);
        data.setRaining(true);
        data.setRainTime(Math.max(data.getRainTime(), duration));
        data.setThundering(true);
        data.setThunderTime(duration);
    }
}
