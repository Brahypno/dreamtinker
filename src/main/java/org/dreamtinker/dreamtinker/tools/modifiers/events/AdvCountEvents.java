package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public final class AdvCountEvents {
    private AdvCountEvents() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent e) {
        AdvCountService.rebuildForServer(e.getServer());
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent e) {
        MinecraftServer server = e.getPlayer() == null ? e.getPlayerList().getServer() : e.getPlayer().getServer();
        if (server != null){
            AdvCountService.rebuildForServer(server);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        //AdvCountService.warmup(sp); // 可选：不预热也行，首次读取时会懒加载
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp){
            AdvCountService.DONE_BY_PLAYER.remove(sp.getUUID()); // 建议清理
        }
    }

    @SubscribeEvent
    public static void onAdvEarn(AdvancementEvent.AdvancementEarnEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp){
            Advancement adv = e.getAdvancement();
            AdvCountService.onPlayerEarned(sp, adv);
        }
    }

    public static final class AdvCountService {
        private AdvCountService() {}

        /**
         * 仅统计带展示信息的进度（建议 true）
         */
        public static boolean ONLY_DISPLAY = true;
        /**
         * 仅统计某命名空间（null=全部，如 "minecraft" / "yourmodid"）
         */
        public static String NAMESPACE_FILTER = null;

        /**
         * 缓存：被统计的进度清单 + 快速判定集合
         */
        private static volatile List<Advancement> COUNTED_LIST = List.of();
        private static volatile Set<Advancement> COUNTED_SET = Set.of();

        /**
         * 缓存：总数
         */
        private static final AtomicInteger TOTAL = new AtomicInteger(0);
        /**
         * 缓存：玩家 -> 已完成数
         */
        private static final Map<UUID, AtomicInteger> DONE_BY_PLAYER = new ConcurrentHashMap<>();

        /**
         * 对外数据结构：只含总数与完成数
         */
        public record Counts(int done, int total) {
        }

        /* ---------------- 对外 API ---------------- */

        /**
         * O(1) 读取数量；若玩家未初始化则惰性初始化一次
         */
        public static Counts getCounts(ServerPlayer sp) {
            AtomicInteger done = DONE_BY_PLAYER.computeIfAbsent(sp.getUUID(), id -> new AtomicInteger(initDoneFor(sp)));
            return new Counts(done.get(), TOTAL.get());
        }

        /* ---------------- 生命周期/内部 ---------------- */

        /**
         * 在服务器启动或数据包重载时调用：重建清单与总数，并清空玩家缓存
         */
        public static void rebuildForServer(MinecraftServer server) {
            var all = server.getAdvancements().getAllAdvancements().stream()
                            .filter(adv -> !ONLY_DISPLAY || adv.getDisplay() != null)
                            .filter(adv -> NAMESPACE_FILTER == null || adv.getId().getNamespace().equals(NAMESPACE_FILTER))
                            .toList();

            COUNTED_LIST = List.copyOf(all);
            COUNTED_SET = Set.copyOf(all);
            TOTAL.set(COUNTED_LIST.size());
            DONE_BY_PLAYER.clear();
        }

        /**
         * 首次为该玩家计算完成数
         */
        private static int initDoneFor(ServerPlayer sp) {
            int done = 0;
            for (Advancement adv : COUNTED_LIST) {
                AdvancementProgress p = sp.getAdvancements().getOrStartProgress(adv);
                if (p.isDone())
                    done++;
            }
            return done;
        }

        /**
         * 可选预热（玩家登录时）
         */
        public static void warmup(ServerPlayer sp) {
            DONE_BY_PLAYER.computeIfAbsent(sp.getUUID(), id -> new AtomicInteger(initDoneFor(sp)));
        }

        /**
         * 玩家达成进度时的增量更新
         */
        public static void onPlayerEarned(ServerPlayer sp, Advancement adv) {
            if (!COUNTED_SET.isEmpty() && COUNTED_SET.contains(adv)){
                DONE_BY_PLAYER.computeIfAbsent(sp.getUUID(), id -> new AtomicInteger(initDoneFor(sp)))
                              .incrementAndGet();
            }
        }
    }
}
