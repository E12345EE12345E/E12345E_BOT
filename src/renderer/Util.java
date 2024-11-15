package renderer;

import java.awt.Color;

public class Util {
    public static double clamp(double x, double min, double max) {
	    return Math.max(min, Math.min(x, max));
    }
    public static double lerp(double a, double b, double t) {
        return (a + (b - a)* t);
    }
    // Coordinate frame helpers
    public static double toCoordFrameX(double posX) {
        posX = (posX - World.get().getOrigin().x) / World.get().getPixelsPerUnit();
        return posX;
    }
    public static double toCoordFrameY(double posY) {
        posY = -(posY - World.get().getOrigin().y) / World.get().getPixelsPerUnit();
        return posY;
    }
    public static Vec2 toCoordFrame(Vec2 pos) {
        Vec2 origin = World.get().getOrigin();
        return new Vec2((pos.x-origin.x) / World.get().getPixelsPerUnit(), -(pos.y-origin.y) / World.get().getPixelsPerUnit());
    }
    public static double toCoordFrameLength(double len) {
        return len / World.get().getPixelsPerUnit();
    }
    public static double toPixelsX(double posX) {
        Vec2 origin = World.get().getOrigin();
        posX = posX * World.get().getPixelsPerUnit() + origin.x;
        return posX;
    }
    public static double toPixelsY(double posY) {
        Vec2 origin = World.get().getOrigin();
        posY = -posY * World.get().getPixelsPerUnit() + origin.y;
        return posY;
    }
    public static int toPixelsXInt(double posX) {
        Vec2 origin = World.get().getOrigin();
        posX = posX * World.get().getPixelsPerUnit() + origin.x;
        return (int)Math.round(posX);
    }
    public static int toPixelsYInt(double posY) {
        Vec2 origin = World.get().getOrigin();
        posY = -posY * World.get().getPixelsPerUnit() + origin.y;
        return (int)Math.round(posY);
    }    
    public static Vec2 toPixels(Vec2 pos) {
        Vec2 origin = World.get().getOrigin();
        return new Vec2(pos.x * World.get().getPixelsPerUnit() + origin.x, -pos.y * World.get().getPixelsPerUnit() + origin.y);
    }
    public static Vec2 toPixelDims(Vec2 dims) {
        return new Vec2(dims.x * World.get().getPixelsPerUnit(), dims.y * World.get().getPixelsPerUnit());
    }
    public static double toPixelsLength(double len) {
        return len * World.get().getPixelsPerUnit();
    }
    public static int toPixelsLengthInt(double len) {
        return (int)Math.round(len * World.get().getPixelsPerUnit());
    }
    public static Vec2 clampToClosestHalfUnit(Vec2 point) {
        return new Vec2(Math.round(point.x - 0.5) + 0.5, Math.round(point.y - 0.5) + 0.5);
    }
    public static Vec2 maxCoordFrameUnits() {
        return new Vec2(World.get().getCanvasSize().x / World.get().getPixelsPerUnit(), World.get().getCanvasSize().y / World.get().getPixelsPerUnit());
    }
    public static Vec2 maxHalfUnitsInField() {
        Vec2 maxHalfUnits = maxCoordFrameUnits();
        maxHalfUnits = clampToClosestHalfUnit(maxHalfUnits);
        if (!isInsideField(maxHalfUnits, 0.2)) {
            maxHalfUnits.subtract(new Vec2(1, 1));
        }
        return maxHalfUnits;
    }
    // Color utils
    public static Color colorLerp(Color colorA, Color colorB, double t) {
        return colorLerp(colorA, colorB, (float)t);
    }
    public static Color colorLerp(Color colorA, Color colorB, float t) {
        if (t <= 0) {
            return colorA;
        }
        else if (t >= 1) {
            return colorB;
        }
        return new Color(
            (int)lerp((float)colorA.getRed(), (float)colorB.getRed(), t),
            (int)lerp((float)colorA.getGreen(), (float)colorB.getGreen(), t),
            (int)lerp((float)colorA.getBlue(), (float)colorB.getBlue(), t));
    }
    public static Color colorAlpha(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha*255));
    }
    // Intersection utils
    public static boolean circlesIntersect(Vec2 p1, double r1, Vec2 p2, double r2) {
        double dstSqr = Vec2.distanceSqr(p1, p2);
        double radSum = r1 + r2;
        return (dstSqr < radSum * radSum);
    }
    public static boolean circleContainsPoint(Vec2 circlePos, double circleRad, Vec2 point) {
        double dstSqr = Vec2.distanceSqr(circlePos, point);
        return (dstSqr < circleRad * circleRad);
    }
    public static boolean isInsideField(Vec2 point) {
        return isInsideField(point, 0);
    }
    public static boolean isInsideField(Vec2 point, double buffer) {
        if ((point.x <= buffer) || (point.y <= buffer)) {
            return false;
        }
        if ((point.x >= toCoordFrameLength(World.get().getCanvasSize().x) - buffer) || (point.y >= toCoordFrameLength(World.get().getCanvasSize().y) - buffer)) {
            return false;
        }
        return true;
    }
    public static Vec2 intersectSegments(Vec2 seg0start, Vec2 seg0dir, Vec2 seg1start, Vec2 seg1dir) {
        double s = (-seg0dir.y * (seg0start.x - seg1start.x) + seg0dir.x * (seg0start.y - seg1start.y)) / (-seg1dir.x * seg0dir.y + seg0dir.x * seg1dir.y);
        double t = ( seg1dir.x * (seg0start.y - seg1start.y) - seg1dir.y * (seg0start.x - seg1start.x)) / (-seg1dir.x * seg0dir.y + seg0dir.x * seg1dir.y);
        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected, return the point of intersection
            return new Vec2(seg0start.x + (t * seg0dir.x), seg0start.y + (t * seg0dir.y));
        }
        return null;
    }
    public static boolean intersectCircleSegment(Vec2 circleCenter, double circleRadius, Vec2 segStart, Vec2 segDir) {
        double dst = distancePointToSegment(circleCenter, segStart, segDir);
        return (dst < circleRadius);
    }
    public static boolean intersectCircleCapsule(Vec2 circleCenter, double circleRadius, Vec2 capSegStart, Vec2 capSegDir, double capRadius) {
        double dst = distancePointToSegment(circleCenter, capSegStart, capSegDir);
        return (dst < circleRadius + capRadius);
    }

    // Distances (returns null if no intersection, vec2 with intersection otherwise)...
    public static double distancePointToLine(Vec2 point, Vec2 linePoint, Vec2 lineDirUnit) {
        Vec2 v = Vec2.subtract(point, linePoint);
        Vec2 u = Vec2.subtract(v, Vec2.multiply(lineDirUnit, Vec2.dot(v, lineDirUnit)));
        return u.length();
    }
    public static double distancePointToSegment(Vec2 point, Vec2 segStart, Vec2 segDir) {
        Vec2 v = Vec2.subtract(point, segStart);
        double segDirLen = segDir.length();
        Vec2 segDirUnit = Vec2.divide(segDir, segDirLen);
        Vec2 closestPt = Vec2.add(segStart, Vec2.multiply(segDirUnit, Util.clamp(Vec2.dot(v, segDirUnit), 0, segDirLen)));
        return Vec2.distance(point, closestPt);
    }
    public static double distancePointToPlane(Vec2 point, Vec2 linePoint, Vec2 lineNormalUnit) {
        Vec2 v = Vec2.subtract(point, linePoint);
        return Vec2.dot(v, lineNormalUnit);
    }
}