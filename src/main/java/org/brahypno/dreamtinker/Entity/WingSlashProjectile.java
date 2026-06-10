package org.brahypno.dreamtinker.Entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WingSlashProjectile extends AbstractSlashProjectile {
    public WingSlashProjectile(EntityType<? extends WingSlashProjectile> type, Level level) {
        super(type, level);
    }

    public WingSlashProjectile(Level level) {
        this(DreamtinkerEntityTypes.WING_SLASH.get(), level);
    }

    public WingSlashProjectile(EntityType<? extends WingSlashProjectile> type, Level level, LivingEntity owner) {
        super(type, level, owner);
    }

    public static WingSlashProjectile shootFrom(
            Level level,
            LivingEntity owner,
            float power,
            int maxLife,
            byte pierceLevel,
            double speed
    ) {
        return shootFrom(
                level,
                owner,
                owner.getLookAngle(),
                power,
                maxLife,
                pierceLevel,
                speed,
                0xEDE9DD,
                220,
                1.0F,
                1.0F,
                18.0F
        );
    }

    public static WingSlashProjectile shootFrom(
            Level level,
            LivingEntity owner,
            float power,
            int maxLife,
            byte pierceLevel,
            double speed,
            int rgb,
            int alpha,
            float lengthScale,
            float widthScale,
            float spinSpeed
    ) {
        return shootFrom(
                level,
                owner,
                owner.getLookAngle(),
                power,
                maxLife,
                pierceLevel,
                speed,
                rgb,
                alpha,
                lengthScale,
                widthScale,
                spinSpeed
        );
    }

    public static WingSlashProjectile shootFrom(
            Level level,
            LivingEntity owner,
            Vec3 direction,
            float power,
            int maxLife,
            byte pierceLevel,
            double speed,
            int rgb,
            int alpha,
            float lengthScale,
            float widthScale,
            float spinSpeed
    ) {
        return shootProjectile(
                new WingSlashProjectile(DreamtinkerEntityTypes.WING_SLASH.get(), level, owner),
                level,
                owner,
                direction,
                power,
                maxLife,
                pierceLevel,
                speed,
                rgb,
                alpha,
                lengthScale,
                widthScale,
                spinSpeed
        );
    }

    public static WingSlashProjectile shootFrom(
            Level level,
            LivingEntity owner,
            Vec3 direction,
            float power,
            int maxLife,
            float maxDistance,
            byte pierceLevel,
            double speed,
            int rgb,
            int alpha,
            float lengthScale,
            float widthScale,
            float spinSpeed
    ) {
        return shootProjectile(
                new WingSlashProjectile(DreamtinkerEntityTypes.WING_SLASH.get(), level, owner),
                level,
                owner,
                direction,
                power,
                maxLife,
                maxDistance,
                pierceLevel,
                speed,
                rgb,
                alpha,
                lengthScale,
                widthScale,
                spinSpeed
        );
    }
}