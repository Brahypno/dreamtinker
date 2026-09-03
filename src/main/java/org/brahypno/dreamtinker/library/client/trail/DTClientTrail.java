package org.brahypno.dreamtinker.library.client.trail;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

public class DTClientTrail {
    private static final int MAX_INTERPOLATION_STEPS = 64;
    private static final int MAX_POINTS = 256;
    private static final double TELEPORT_DISTANCE_SQR = 32.0D * 32.0D;

    // Reuse expired slots without allocating a Vec3 per sample or shifting a list.
    private Point[] ring;
    private final List<Point> pointView = new PointView();
    private final int lifespan;
    private final double minDistanceSqr;
    private int head;
    private int size;
    private Vec3 lastAddedPosition;
    private boolean bootstrapped;
    private boolean boundsDirty;
    private AABB cachedBounds;

    public DTClientTrail(int lifespan) {
        this(lifespan, 0.0004D);
    }

    public DTClientTrail(int lifespan, double minDistanceSqr) {
        this.lifespan = Math.max(1, lifespan);
        this.minDistanceSqr = minDistanceSqr;
    }

    public void tick(Vec3 pos, Vec3 vel, int bootCount, double bootSpacing, double stepSpacing, float bootAgeFactor) {
        tickOnly();
        if (!bootstrapped){
            bootstrap(pos, vel, bootCount, bootSpacing, bootAgeFactor);
            return;
        }
        addInterpolatedPoints(pos, stepSpacing);
    }

    private void bootstrap(Vec3 headPosition, Vec3 velocity, int count, double spacing, float ageFactor) {
        clear();
        if (!finite(headPosition.x, headPosition.y, headPosition.z)){
            return;
        }
        double lengthSqr = velocity.lengthSqr();
        double dirX = 0.0D;
        double dirY = 0.0D;
        double dirZ = 1.0D;
        if (Double.isFinite(lengthSqr) && lengthSqr > 1.0E-6D){
            double inverseLength = 1.0D / Math.sqrt(lengthSqr);
            dirX = velocity.x * inverseLength;
            dirY = velocity.y * inverseLength;
            dirZ = velocity.z * inverseLength;
        }
        count = Mth.clamp(count, 0, MAX_POINTS);
        int maxAge = Math.max(1, (int) (lifespan * ageFactor));
        for (int i = count - 1; i >= 0; i--) {
            float t = count <= 1 ? 0.0F : i / (float) (count - 1);
            append(headPosition.x - dirX * i * spacing,
                   headPosition.y - dirY * i * spacing,
                   headPosition.z - dirZ * i * spacing, (int) (maxAge * t * t));
        }
        lastAddedPosition = headPosition;
        bootstrapped = true;
    }

    public void tick(Vec3 worldPosition) {
        tickOnly();
        addInterpolatedPoints(worldPosition, 0.35D);
    }

    public void addInterpolatedPoints(Vec3 worldPosition, double spacing) {
        if (!finite(worldPosition.x, worldPosition.y, worldPosition.z)){
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
        int steps = Mth.clamp((int) Math.ceil(Math.sqrt(distanceSqr) / safeSpacing), 1, MAX_INTERPOLATION_STEPS);
        double startX = lastAddedPosition.x;
        double startY = lastAddedPosition.y;
        double startZ = lastAddedPosition.z;
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            addPoint(startX + dx * t, startY + dy * t, startZ + dz * t);
        }
        lastAddedPosition = worldPosition;
    }

    public void addPoint(Vec3 worldPosition) {
        addPoint(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    private void addPoint(double x, double y, double z) {
        if (!finite(x, y, z)){
            return;
        }
        if (size > 0){
            Point last = pointAt(size - 1);
            double dx = last.x - x;
            double dy = last.y - y;
            double dz = last.z - z;
            if (dx * dx + dy * dy + dz * dz < minDistanceSqr){
                return;
            }
        }
        append(x, y, z, 0);
    }

    private void append(double x, double y, double z, int age) {
        // Projectile instances also exist on dedicated servers, where trails are
        // never ticked. Allocate the buffer only when the first point is added.
        if (ring == null)
            ring = new Point[MAX_POINTS];
        if (size == MAX_POINTS){
            head = (head + 1) & (MAX_POINTS - 1);
            size--;
        }
        int slot = (head + size) & (MAX_POINTS - 1);
        Point point = ring[slot];
        if (point == null){
            point = new Point(this);
            ring[slot] = point;
        }
        point.reset(x, y, z, age);
        size++;
        boundsDirty = true;
    }

    private Point pointAt(int index) {
        return ring[(head + index) & (MAX_POINTS - 1)];
    }

    public void tickOnly() {
        int expiredPrefix = 0;
        for (int i = 0; i < size; i++) {
            Point point = pointAt(i);
            point.age++;
            if (point.age > lifespan){
                expiredPrefix++;
            }
        }
        if (expiredPrefix > 0){
            head = (head + expiredPrefix) & (MAX_POINTS - 1);
            size -= expiredPrefix;
            boundsDirty = true;
        }
    }

    /**
     * Live read-only view; point slots may be reused after the next trail update.
     */
    public List<Point> points() {
        return pointView;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int lifespan() {
        return lifespan;
    }

    /**
     * Both interpolation endpoints are included; recompute only after a change.
     */
    public AABB bounds() {
        if (!boundsDirty){
            return cachedBounds;
        }
        if (size == 0){
            cachedBounds = null;
        }else {
            double minX = Double.POSITIVE_INFINITY, minY = minX, minZ = minX;
            double maxX = Double.NEGATIVE_INFINITY, maxY = maxX, maxZ = maxX;
            for (int i = 0; i < size; i++) {
                Point point = pointAt(i);
                minX = Math.min(minX, Math.min(point.oldX, point.x));
                minY = Math.min(minY, Math.min(point.oldY, point.y));
                minZ = Math.min(minZ, Math.min(point.oldZ, point.z));
                maxX = Math.max(maxX, Math.max(point.oldX, point.x));
                maxY = Math.max(maxY, Math.max(point.oldY, point.y));
                maxZ = Math.max(maxZ, Math.max(point.oldZ, point.z));
            }
            cachedBounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        }
        boundsDirty = false;
        return cachedBounds;
    }

    public void clear() {
        head = 0;
        size = 0;
        cachedBounds = null;
        boundsDirty = false;
        lastAddedPosition = null;
        bootstrapped = false;
    }

    private static boolean finite(double x, double y, double z) {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }

    private final class PointView extends AbstractList<Point> implements RandomAccess {
        @Override
        public Point get(int index) {
            if (index < 0 || index >= size){
                throw new IndexOutOfBoundsException(index);
            }
            return pointAt(index);
        }

        @Override
        public int size() {
            return size;
        }
    }

    public static class Point {
        private final DTClientTrail owner;
        private double oldX, oldY, oldZ;
        private double x, y, z;
        private int age;
        private Vec3 rawPosition;

        public Point(Vec3 position) {
            this.owner = null;
            reset(position.x, position.y, position.z, 0);
        }

        private Point(DTClientTrail owner) {
            this.owner = owner;
        }

        private void reset(double x, double y, double z, int age) {
            this.oldX = this.x = x;
            this.oldY = this.y = y;
            this.oldZ = this.z = z;
            this.age = age;
            this.rawPosition = null;
        }

        public double getX(float partialTicks) {
            return oldX + (x - oldX) * partialTicks;
        }

        public double getY(float partialTicks) {
            return oldY + (y - oldY) * partialTicks;
        }

        public double getZ(float partialTicks) {
            return oldZ + (z - oldZ) * partialTicks;
        }

        public Vec3 getRawPosition() {
            if (rawPosition == null){
                rawPosition = new Vec3(x, y, z);
            }
            return rawPosition;
        }

        public int getAge() {
            return age;
        }

        public void setPosition(Vec3 position) {
            oldX = x;
            oldY = y;
            oldZ = z;
            x = position.x;
            y = position.y;
            z = position.z;
            rawPosition = position;
            if (owner != null){
                owner.boundsDirty = true;
            }
        }
    }
}
