package org.dreamtinker.dreamtinker.common.event.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.util.Objects;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.StarRegulusAdvancement;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.AsOneA;

public class star_regulus_boost {
    // 要检测的进度 ID
    private static final ResourceLocation TAG_MO = new ResourceLocation(Dreamtinker.MODID, "magnum_opus");
    private static int tickCounter = 0;
    private static final int amp = AsOneA.get();

    public static void onServerTick(ServerTickEvent event) {
        if (!StarRegulusAdvancement.get())
            return;
        if (event.phase != ServerTickEvent.Phase.END)
            return;
        // 每 20*10 tick 运行一次
        if (++tickCounter < 20 * 10)
            return;
        tickCounter = 0;

        MinecraftServer server = event.getServer();
        // 获取进度对象
        Advancement adv = server.getAdvancements().getAdvancement(TAG_MO);
        if (adv == null){
            // 进度不存在时跳过
            return;
        }

        // 遍历所有在线玩家
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
            // 如果该玩家已完成此进度
            if (progress.isDone() && player.isAlive()){
                int minDuration = 20;
                if (player.getEffect(MobEffects.NIGHT_VISION) == null
                    || Objects.requireNonNull(player.getEffect(MobEffects.NIGHT_VISION)).getDuration() <= minDuration * 11){
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 21, amp, false, false));
                }
                if (player.getEffect(MobEffects.REGENERATION) == null
                    || Objects.requireNonNull(player.getEffect(MobEffects.REGENERATION)).getDuration() <= minDuration * 11){
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 21, amp, false, false));
                }
                if (player.getEffect(MobEffects.DAMAGE_RESISTANCE) == null
                    || Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_RESISTANCE)).getDuration() <= minDuration * 11){
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 21, amp, false, false));
                }
                if (player.getEffect(MobEffects.HEALTH_BOOST) == null
                    || Objects.requireNonNull(player.getEffect(MobEffects.HEALTH_BOOST)).getDuration() <= minDuration * 11){
                    player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 20 * 21, amp, false, false));
                }
                if (player.getEffect(MobEffects.LUCK) == null
                    || Objects.requireNonNull(player.getEffect(MobEffects.LUCK)).getDuration() <= minDuration * 11){
                    player.addEffect(new MobEffectInstance(MobEffects.LUCK, 20 * 21, amp, false, false));
                }
            }
        }
    }
}

