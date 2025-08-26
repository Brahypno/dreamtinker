package org.dreamtinker.dreamtinker.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ProjLimit;

public class DThelper {
    public static void clearProjectile(ServerLevel level, double px, double pz) {
        int viewDist = level.getServer().getPlayerList().getViewDistance();
        double radius = viewDist * 16.0;
        AABB box = new AABB(px - radius, level.getMinBuildHeight(), pz - radius, px + radius, level.getMaxBuildHeight(), pz + radius);
        Predicate<Projectile> all = p -> true;
        List<Projectile> list = level.getEntitiesOfClass(Projectile.class, box, all);
        if (ProjLimit.get() <= list.size())
            for (Projectile old : list)
                old.remove(Entity.RemovalReason.DISCARDED);
    }
}
