package sketch.environment.snapshot.shape;

import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.shape.BoundingBox;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class ArcSnapshot implements IntersectionShapeSnapshot {
  public final float cx, cy;
  public final float radius;
  public final float startAngle, endAngle;

  public ArcSnapshot(Vector center, float radius, float sAngle, float eAngle) {
    this.cx = center.x;
    this.cy = center.y;
    this.radius = radius;
    this.startAngle = sAngle;
    this.endAngle = eAngle < sAngle ? eAngle + 360 : eAngle;
  }

  private boolean angleHits(float angle) {
    double degAngle = Math.toDegrees(angle);
    if (endAngle < 360) {
      return degAngle >= startAngle && degAngle <= endAngle;
    } else {
      return degAngle >= startAngle || degAngle <= endAngle - 360;
    }
  }

  public Intersection intersect(Ray ray) {
    BoundingBox boundingBox = new BoundingBox(cx - radius, cy - radius, cx + radius, cy + radius);
    if (!boundingBox.intersects(ray)) {
      return null;
    }
    Vector center = new Vector(cx, cy);
    Vector originToCenter = Vector.sub(center, ray.origin);
    float dotProduct = originToCenter.dot(ray.direction);
    Vector projected = Vector.mult(ray.direction, dotProduct);
    Vector projectedToCenter = Vector.sub(originToCenter, projected);
    float distance = projectedToCenter.mag();
    Vector hitPos;
    if (distance > radius) {
      return null;
    } else {
      float m = (float) Math.sqrt(radius * radius - distance * distance);
      float len1 = projected.mag() * Math.signum(projected.dot(ray.direction)) - m;
      float len2 = projected.mag() * Math.signum(projected.dot(ray.direction)) + m;
      Vector hit1 = Vector.add(ray.origin, Vector.mult(ray.direction, len1));
      Vector hit2 = Vector.add(ray.origin, Vector.mult(ray.direction, len2));
      float d1 = Vector.dist(ray.origin, hit1);
      float d2 = Vector.dist(ray.origin, hit2);
      float a1 = (float) ((Vector.sub(hit1, center).heading() + Math.PI * 2) % (Math.PI * 2));
      float a2 = (float) ((Vector.sub(hit2, center).heading() + Math.PI * 2) % (Math.PI * 2));
      if (d1 < d2 && len1 > 0 && angleHits(a1)) {
        hitPos = hit1;
      } else if (len2 > 0 && angleHits(a2)) {
        hitPos = hit2;
      } else {
        return null;
      }
    }
    if (Vector.sub(hitPos, ray.origin).dot(ray.direction) < 0) {
      return null;
    }
    Vector atOrigin = Vector.sub(hitPos, center);
    Vector normal = atOrigin.copy().normalize();
    float a = (float) ((Math.toDegrees(atOrigin.heading()) + 360) % 360);
    float t;
    if (endAngle < 360) {
      t = MathUtils.map(a, startAngle, endAngle, 0, 1);
    } else {
      if (a < endAngle - 360) {
        a += 360;
      }
      t = MathUtils.map(a, startAngle, endAngle, 0, 1);
    }
    return Intersection.stepOne(ray, hitPos, normal, t);
  }
}
