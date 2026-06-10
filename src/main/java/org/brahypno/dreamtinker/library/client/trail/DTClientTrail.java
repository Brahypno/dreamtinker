package org.brahypno.dreamtinker.library.client.trail;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DTClientTrail {
    private final List<Point> points = new ArrayList<>();
    private final int lifespan;
    private final double minDistanceSqr;

    public DTClientTrail(int lifespan) {
        this(lifespan, 0.0004D);
    }

    public DTClientTrail(int lifespan, double minDistanceSqr) {
        this.lifespan = lifespan;
        this.minDistanceSqr = minDistanceSqr;
    }

    private Vec3 lastAddedPosition;
    private boolean bootstrapped;

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
        Vec3 dir = velocity.lengthSqr() > 1.0E-6D ? velocity.normalize() : new Vec3(0, 0, 1);
        int maxAge = Math.max(1, (int) (lifespan * ageFactor));

        for (int i = count - 1; i >= 0; i--) {
            float t = count <= 1 ? 0.0F : i / (float) (count - 1);
            Point p = new Point(head.subtract(dir.scale(i * spacing)));
            p.age = (int) (maxAge * t * t);
            points.add(p);
        }

        lastAddedPosition = head;
        bootstrapped = true;
    }

    public void tick(Vec3 worldPosition) {
        tickOnly();
        addInterpolatedPoints(worldPosition, 0.35D);
    }


    public void addInterpolatedPoints(Vec3 worldPosition, double spacing) {
        if (lastAddedPosition == null){
            addPoint(worldPosition);
            lastAddedPosition = worldPosition;
            return;
        }

        double distance = lastAddedPosition.distanceTo(worldPosition);

        if (distance < Math.sqrt(minDistanceSqr)){
            return;
        }

        int steps = Math.max(1, (int) Math.ceil(distance / spacing));

        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3 p = lastAddedPosition.lerp(worldPosition, t);
            addPoint(p);
        }

        lastAddedPosition = worldPosition;
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
        Iterator<Point> iterator = points.iterator();

        while (iterator.hasNext()) {
            Point point = iterator.next();
            point.age++;

            if (point.age > lifespan){
                iterator.remove();
            }
        }
    }

    public List<Point> points() {
        return points;
    }

    public int lifespan() {
        return lifespan;
    }

    public void clear() {
        points.clear();
        lastAddedPosition = null;
        bootstrapped = false;
    }

    public void seedLine(Vec3 head, Vec3 velocity, int count, double spacing) {
        clear();

        Vec3 dir = velocity.lengthSqr() > 1.0E-6D ? velocity.normalize() : new Vec3(0, 0, 1);

        for (int i = count - 1; i >= 0; i--) {
            Point p = new Point(head.subtract(dir.scale(i * spacing)));
            p.age = i * 2;
            points.add(p);
        }
    }

    public static class Point {
        private Vec3 oldPosition;
        private Vec3 position;
        private int age;

        public Point(Vec3 position) {
            this.oldPosition = position;
            this.position = position;
            this.age = 0;
        }

        public Vec3 getPosition(float partialTicks) {
            return oldPosition.lerp(position, partialTicks);
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
