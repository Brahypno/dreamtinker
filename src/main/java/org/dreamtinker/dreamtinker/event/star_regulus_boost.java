package org.dreamtinker.dreamtinker.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.util.Objects;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.StarRegulus;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class star_regulus_boost {
    // 要检测的进度 ID
    private static final ResourceLocation TAG_MO = new ResourceLocation(Dreamtinker.MODID, "magnum_opus");
    // 计时用，每 20 tick（1 秒）执行一次
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent event) {
        if(1!=StarRegulus.get()) return;
        if (event.phase != ServerTickEvent.Phase.END) return;
        // 每 20*10 tick 运行一次
        if (++tickCounter < 20*10) return;
        tickCounter = 0;

        MinecraftServer server = event.getServer();
        // 获取进度对象
        Advancement adv = server.getAdvancements().getAdvancement(TAG_MO);
        if (adv == null) {
            // 进度不存在时跳过
            return;
        }

        // 遍历所有在线玩家
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
            // 如果该玩家已完成此进度
            if (progress.isDone()) {
                // 下面举例给三个效果：夜视、再生、抗性
                // 只有当剩余时长不足 10 tick 时才重刷，避免每 tick 都添加
                int minDuration = 10;
                if (player.getEffect(MobEffects.NIGHT_VISION) == null
                        || Objects.requireNonNull(player.getEffect(MobEffects.NIGHT_VISION)).getDuration() < minDuration) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 10, 3,false,false));
                }
                if (player.getEffect(MobEffects.REGENERATION) == null
                        || Objects.requireNonNull(player.getEffect(MobEffects.REGENERATION)).getDuration() < minDuration) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 10, 3,false,false));
                }
                if (player.getEffect(MobEffects.DAMAGE_RESISTANCE) == null
                        || Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_RESISTANCE)).getDuration() < minDuration) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20*10, 3, false, false));
                }
                if (player.getEffect(MobEffects.HEALTH_BOOST) == null
                        || Objects.requireNonNull(player.getEffect(MobEffects.HEALTH_BOOST)).getDuration() < minDuration) {
                    player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 20*10, 3, false, false));
                }
                if (player.getEffect(MobEffects.LUCK) == null
                        || Objects.requireNonNull(player.getEffect(MobEffects.LUCK)).getDuration() < minDuration) {
                    player.addEffect(new MobEffectInstance(MobEffects.LUCK, 20*10, 3, false, false));
                }
            }
        }
    }
}

