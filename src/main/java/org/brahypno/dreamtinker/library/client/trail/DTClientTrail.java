package org.brahypno.dreamtinker.library.client.trail;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DTClientTrail {
    private static final Vec3 FALLBACK_DIRECTION = new Vec3(0.0D, 0.0D, 1.0D);
    private static final int MAX_INTERPOLATION_STEPS = 64;
    private static final int MAX_POINTS = 256;
    private static final double TELEPORT_DISTANCE_SQR = 32.0D * 32.0D;

    private final List<Point> points = new ArrayList<>();
    private final int lifespan;
    private final double minDistanceSqr;
    private final double minDistance;

    private Vec3 lastAddedPosition;
    private boolean bootstrapped;

    public DTClientTrail(int lifespan) {
        this(lifespan, 0.0004D);
    }

    public DTClientTrail(int lifespan, double minDistanceSqr) {
        this.lifespan = lifespan;
        this.minDistanceSqr = minDistanceSqr;
        this.minDistance = Math.sqrt(minDistanceSqr);
    }

    public void tick(Vec3 pos, Vec3 vel, int bootCount, double bootSpacing, double stepSpacing, float bootAgeFactor) {
        tickOnly();
        if (!bootstrapped){
            bootstrap(pos, vel, bootCount, bootSpacing, bootAgeFactor);
            return;
        }
        addInterpolatedPoints(pos, stepSpacing);
    }

    private void bootstrap(Vec3 head, Vec3 velocity, int count, double spacing, float ageFactor) {
        clear();

        double lengthSqr = velocity.lengthSqr();
        double dirX = FALLBACK_DIRECTION.x;
        double dirY = FALLBACK_DIRECTION.y;
        double dirZ = FALLBACK_DIRECTION.z;
        if (lengthSqr > 1.0E-6D){
            double inverseLength = 1.0D / Math.sqrt(lengthSqr);
            dirX = velocity.x * inverseLength;
            dirY = velocity.y * inverseLength;
            dirZ = velocity.z * inverseLength;
        }

        int maxAge = Math.max(1, (int) (lifespan * ageFactor));
        for (int i = count - 1; i >= 0; i--) {
            float t = count <= 1 ? 0.0F : i / (float) (count - 1);
            Point point = new Point(new Vec3(
                    head.x - dirX * i * spacing,
                    head.y - dirY * i * spacing,
                    head.z - dirZ * i * spacing
            ));
            point.age = (int) (maxAge * t * t);
            points.add(point);
        }

        lastAddedPosition = head;
        bootstrapped = true;
    }

    public void tick(Vec3 worldPosition) {
        tickOnly();
        addInterpolatedPoints(worldPosition, 0.35D);
    }

    public void addInterpolatedPoints(Vec3 worldPosition, double spacing) {
        if (!Double.isFinite(worldPosition.x)
            || !Double.isFinite(worldPosition.y)
            || !Double.isFinite(worldPosition.z)){
            clear();
            return;
        }

        if (lastAddedPosition == null){
            addPoint(worldPosition);
            lastAddedPosition = worldPosition;
            return;
        }

        double dx = worldPosition.x - lastAddedPosition.x;
        double dy = worldPosition.y - lastAddedPosition.y;
        double dz = worldPosition.z - lastAddedPosition.z;
        double distanceSqr = dx * dx + dy * dy + dz * dz;

        if (!Double.isFinite(distanceSqr) || distanceSqr > TELEPORT_DISTANCE_SQR){
            clear();
            addPoint(worldPosition);
            lastAddedPosition = worldPosition;
            bootstrapped = true;
            return;
        }

        if (distanceSqr < minDistanceSqr){
            return;
        }

        double safeSpacing = Math.max(spacing, 1.0E-3D);
        double distance = Math.sqrt(distanceSqr);
        int steps = Mth.clamp(
                (int) Math.ceil(distance / safeSpacing),
                1,
                MAX_INTERPOLATION_STEPS
        );

        double startX = lastAddedPosition.x;
        double startY = lastAddedPosition.y;
        double startZ = lastAddedPosition.z;

        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            addPoint(new Vec3(
                    startX + dx * t,
                    startY + dy * t,
                    startZ + dz * t
            ));
        }

        lastAddedPosition = worldPosition;
        trimToMaximum();
    }

    private void trimToMaximum() {
        int overflow = points.size() - MAX_POINTS;
        if (overflow > 0){
            points.subList(0, overflow).clear();
        }
    }

    public void addPoint(Vec3 worldPosition) {
        if (!points.isEmpty()){
            Point last = points.get(points.size() - 1);
            if (last.position.distanceToSqr(worldPosition) < minDistanceSqr){
                return;
            }
        }
        points.add(new Point(worldPosition));
    }

    public void tickOnly() {
        int expiredPrefix = 0;
        for (Point point : points) {
            point.age++;
            if (point.age > lifespan){
                expiredPrefix++;
            }
        }
        if (expiredPrefix > 0){
            points.subList(0, expiredPrefix).clear();
        }
    }

    public List<Point> points() {
        return points;
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public int lifespan() {
        return lifespan;
    }

    public void clear() {
        points.clear();
        lastAddedPosition = null;
        bootstrapped = false;
    }

    public static class Point {
        private Vec3 oldPosition;
        private Vec3 position;
        private int age;

        public Point(Vec3 position) {
            this.oldPosition = position;
            this.position = position;
        }

        public double getX(float partialTicks) {
            return oldPosition.x + (position.x - oldPosition.x) * partialTicks;
        }

        public double getY(float partialTicks) {
            return oldPosition.y + (position.y - oldPosition.y) * partialTicks;
        }

        public double getZ(float partialTicks) {
            return oldPosition.z + (position.z - oldPosition.z) * partialTicks;
        }

        public Vec3 getRawPosition() {
            return position;
        }

        public int getAge() {
            return age;
        }

        public void setPosition(Vec3 position) {
            this.oldPosition = this.position;
            this.position = position;
        }
    }
}
