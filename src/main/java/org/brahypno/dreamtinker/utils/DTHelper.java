package org.brahypno.dreamtinker.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.network.S2CVibeBarFx;

import java.util.*;

import static org.brahypno.dreamtinker.config.DreamtinkerConfig.ProjLimit;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class DTHelper {
    public static final double MIN_PROJECTILE_SPEED_SQR = 1.0E-6D;
    public static final double PROJECTILE_SPAWN_EXTRA_DISTANCE = 0.45D;
    private static final Map<ServerLevel, ProjectileIndex> PROJECTILES = new IdentityHashMap<>();

    public static void clearProjectile(ServerLevel level, double px, double pz) {
        ProjectileIndex index = PROJECTILES.get(level);
        if (index != null){
            index.clearStalledWhenAtLimit(ProjLimit.get());
        }
    }

    /**
     * O(1) state maintenance called from the Projectile tick mixin.
     */
    public static void trackProjectileTick(Projectile projectile) {
        if (projectile.level() instanceof ServerLevel level){
            PROJECTILES.computeIfAbsent(level, ignored -> new ProjectileIndex()).update(projectile);
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof Projectile projectile){
            PROJECTILES.computeIfAbsent(level, ignored -> new ProjectileIndex()).update(projectile);
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof Projectile projectile){
            ProjectileIndex index = PROJECTILES.get(level);
            if (index != null){
                index.remove(projectile);
                if (index.isEmpty()){
                    PROJECTILES.remove(level);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PROJECTILES.clear();
    }

    private static boolean isStalledProjectile(Projectile projectile) {
        return projectile.isAlive()
               && projectile.getDeltaMovement().lengthSqr() <= MIN_PROJECTILE_SPEED_SQR;
    }

    private static final class ProjectileIndex {
        private final Set<Projectile> live = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Set<Projectile> stalled = Collections.newSetFromMap(new IdentityHashMap<>());

        private void update(Projectile projectile) {
            if (!projectile.isAlive()){
                remove(projectile);
                return;
            }
            live.add(projectile);
            if (isStalledProjectile(projectile)){
                stalled.add(projectile);
            }else {
                stalled.remove(projectile);
            }
        }

        private void remove(Projectile projectile) {
            live.remove(projectile);
            stalled.remove(projectile);
        }

        private void clearStalledWhenAtLimit(int limit) {
            if (live.size() < limit || stalled.isEmpty()){
                return;
            }
            // Removing an entity fires EntityLeaveLevelEvent, so iterate over a stable copy.
            for (Projectile projectile : new ArrayList<>(stalled)) {
                if (isStalledProjectile(projectile)){
                    projectile.remove(Entity.RemovalReason.DISCARDED);
                }else {
                    stalled.remove(projectile);
                }
            }
        }

        private boolean isEmpty() {
            return live.isEmpty();
        }
    }

    public static void sendVibeBarFx(
            ServerLevel level, LivingEntity attacker, LivingEntity target,
            int argb /*0xAARRGGBB*/) {
        Vec3 d = target.position().subtract(attacker.position());
        Vec3 flat = new Vec3(d.x, 0, d.z);
        if (flat.lengthSqr() < 1.0e-6)
            return;

        Vec3 attackDir = flat.normalize();
        Vec3 barDir = new Vec3(-attackDir.z, 0, attackDir.x); // 水平且垂直于 attacker->target

        int life = 8;          // 0.4s
        float amp = 0.05f;      // 抖幅
        float hz = 26.0f;       // 频率
        float yFrac = 0.60f;    // 位置（胸口）

        DNetwork.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target),
                new S2CVibeBarFx(target.getId(), (float) barDir.x, (float) barDir.z, argb, life, amp, hz, yFrac)
        );
    }
}
