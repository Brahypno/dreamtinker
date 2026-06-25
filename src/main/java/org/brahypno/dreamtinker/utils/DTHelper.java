package org.brahypno.dreamtinker.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.network.S2CVibeBarFx;

import java.util.List;

import static org.brahypno.dreamtinker.config.DreamtinkerConfig.ProjLimit;

public class DTHelper {
    public static final double MIN_PROJECTILE_SPEED_SQR = 1.0E-6D;
    public static final double PROJECTILE_SPAWN_EXTRA_DISTANCE = 0.45D;

    public static void clearProjectile(ServerLevel level, double px, double pz) {
        int viewDist = level.getServer().getPlayerList().getViewDistance();
        double radius = viewDist * 16.0D;

        AABB box = new AABB(px - radius, level.getMinBuildHeight(), pz - radius, px + radius, level.getMaxBuildHeight(), pz + radius);

        List<Projectile> list = level.getEntitiesOfClass(Projectile.class, box, Projectile::isAlive);

        if (list.size() < ProjLimit.get()){
            return;
        }

        for (Projectile old : list) {
            if (isStalledProjectile(old))
                old.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private static boolean isStalledProjectile(Projectile projectile) {
        return projectile.isAlive()
               && projectile.getDeltaMovement().lengthSqr() <= MIN_PROJECTILE_SPEED_SQR;
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
