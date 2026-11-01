package org.brahypno.dreamtinker.library.client.sigil;

import java.util.ArrayList;
import java.util.List;

/** Cached paths in a unit-radius plane. Reveal lengths follow geometry, never an animation clock. */
public final class NarcissusSigilGeometry {
    public static final List<List<Path>> VARIANTS = List.of(build(0), build(1), build(2), build(3));
    public static final List<Path> AFTERGLOW = List.of(ellipse(1, 1, 0, 16, 0, 0.25f, false),
                                                     polygon(4, 0.93f, Math.PI / 4, 0.03f, 0.25f, false));

    private NarcissusSigilGeometry() {}

    public record Path(float[] points, float[] distances, float[] normals, float start, float end, boolean core) {
        public float visibleLength(float progress) {
            float fraction = Math.max(0, Math.min(1, (progress - start) / (end - start)));
            return distances[distances.length - 1] * fraction;
        }
    }

    private static List<Path> build(int style) {
        List<Path> paths = new ArrayList<>();
        paths.add(ellipse(1, 1, 0, 48, 0, 0.25f, false));
        paths.add(polygon(4, 0.93f, Math.PI / 4, 0.03f, 0.25f, false));
        if (style == 0) {
            paths.add(ellipse(0.52f, 0.52f, 0, 24, 0.25f, 0.55f, false));
            // Sixteen perimeter chords leave a clear aperture instead of filling the entire disc.
            for (int thread = 0; thread < 16; thread++) {
                int node = thread % 2 == 0 ? thread / 2 : thread / 2 + 8;
                double a = node * Math.PI / 8;
                double b = (node + 5) * Math.PI / 8;
                float begin = 0.55f + thread * 0.018f;
                paths.add(path(new float[]{(float) Math.cos(a) * 0.85f, (float) Math.sin(a) * 0.85f,
                                           (float) Math.cos(b) * 0.85f, (float) Math.sin(b) * 0.85f}, begin, begin + 0.08f, false));
            }
        } else if (style == 1) {
            paths.add(polygon(4, 0.34f, 0, 0.25f, 0.55f, false));
            for (int petal = 0; petal < 3; petal++)
                paths.add(ellipse(0.83f, 0.32f, petal * Math.PI / 3, 32, 0.55f + petal * 0.06f, 0.77f + petal * 0.06f, false));
        } else if (style == 2) {
            paths.add(polygon(4, 0.82f, 0, 0.25f, 0.55f, false));
            for (int square = 0; square < 3; square++)
                paths.add(polygon(4, 0.82f, (square + 1) * Math.PI / 12, 0.55f + square * 0.06f, 0.77f + square * 0.06f, false));
        } else {
            // A dashed orbit provides structure without another continuous ring of crossing lines.
            for (int dash = 0; dash < 12; dash++) {
                float[] points = new float[4];
                for (int i = 0; i < 2; i++) {
                    double angle = (dash + i * 0.32) * Math.PI / 6;
                    points[i * 2] = (float) Math.cos(angle) * 0.65f;
                    points[i * 2 + 1] = (float) Math.sin(angle) * 0.65f;
                }
                paths.add(path(points, 0.25f + dash * 0.015f, 0.37f + dash * 0.015f, false));
            }
            paths.add(ellipse(0.84f, 0.37f, Math.PI / 3, 32, 0.55f, 0.82f, false));
            paths.add(ellipse(0.84f, 0.37f, -Math.PI / 3, 32, 0.62f, 0.90f, false));
        }
        paths.add(ellipse(0.025f, 0.025f, 0, 6, 0, 0.12f, true));
        paths.add(ellipse(0.15f, 0.15f, 0, 12, 0.90f, 0.97f, true));
        paths.add(polygon(4, 0.23f, 0, 0.91f, 1, true));
        return List.copyOf(paths);
    }

    private static Path polygon(int sides, float radius, double rotation, float start, float end, boolean core) {
        return ellipse(radius, radius, rotation, sides, start, end, core);
    }

    private static Path ellipse(float xRadius, float yRadius, double rotation, int segments, float start, float end, boolean core) {
        float[] points = new float[(segments + 1) * 2];
        double cos = Math.cos(rotation);
        double sin = Math.sin(rotation);
        for (int i = 0; i <= segments; i++) {
            double angle = i * Math.PI * 2 / segments;
            double x = Math.cos(angle) * xRadius;
            double y = Math.sin(angle) * yRadius;
            points[i * 2] = (float) (x * cos - y * sin);
            points[i * 2 + 1] = (float) (x * sin + y * cos);
        }
        return path(points, start, end, core);
    }

    private static Path path(float[] points, float start, float end, boolean core) {
        float[] distances = new float[points.length / 2];
        for (int i = 1; i < distances.length; i++) {
            float dx = points[i * 2] - points[i * 2 - 2];
            float dy = points[i * 2 + 1] - points[i * 2 - 1];
            distances[i] = distances[i - 1] + (float) Math.sqrt(dx * dx + dy * dy);
        }
        return new Path(points, distances, normals(points, distances), start, end, core);
    }

    /** Cached miter joins close ribbon seams without adding vertices or calculating normals each frame. */
    private static float[] normals(float[] points, float[] distances) {
        int count = distances.length;
        float[] result = new float[points.length];
        boolean closed = Math.abs(points[0] - points[points.length - 2]) < 0.00001f
                         && Math.abs(points[1] - points[points.length - 1]) < 0.00001f;
        for (int i = 0; i < count; i++) {
            int before = i == 0 ? (closed ? count - 2 : 0) : i - 1;
            int after = i == count - 1 ? (closed ? 1 : i) : i + 1;
            float ax = points[i * 2] - points[before * 2];
            float ay = points[i * 2 + 1] - points[before * 2 + 1];
            float bx = points[after * 2] - points[i * 2];
            float by = points[after * 2 + 1] - points[i * 2 + 1];
            if (before == i) { ax = bx; ay = by; }
            if (after == i) { bx = ax; by = ay; }
            float aLength = (float) Math.hypot(ax, ay);
            float bLength = (float) Math.hypot(bx, by);
            ax /= aLength;
            ay /= aLength;
            bx /= bLength;
            by /= bLength;
            float denominator = Math.max(0.5f, 1 + ax * bx + ay * by);
            result[i * 2] = -(ay + by) / denominator;
            result[i * 2 + 1] = (ax + bx) / denominator;
        }
        return result;
    }
}
