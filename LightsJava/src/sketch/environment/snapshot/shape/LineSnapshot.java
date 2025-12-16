package sketch.environment.snapshot.shape;

import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.util.Vector;

public class LineSnapshot implements IntersectionShapeSnapshot {
  private final float x1, y1, x2, y2;
  private final float nx, ny;

  public LineSnapshot(Vector p1, Vector p2) {
    this.x1 = p1.x;
    this.y1 = p1.y;
    this.x2 = p2.x;
    this.y2 = p2.y;
    Vector normal = Vector.sub(p2, p1).transpose().mult(-1, 1).normalize(); // (-y, x)
    nx = normal.x;
    ny = normal.y;
  }

  public Intersection intersect(Ray ray) {
    Vector v1 = ray.origin;
    Vector v2 = Vector.add(ray.origin, ray.direction);
    Vector v3 = new Vector(x1, y1);
    Vector v4 = new Vector(x2, y2);
    float denom = (v4.y - v3.y) * (v2.x - v1.x) - (v4.x - v3.x) * (v2.y - v1.y);
    if (denom == 0) {
      return null;
    }
    float ua = ((v4.x - v3.x) * (v1.y - v3.y) - (v4.y - v3.y) * (v1.x - v3.x)) / denom;
    float ub = ((v2.x - v1.x) * (v1.y - v3.y) - (v2.y - v1.y) * (v1.x - v3.x)) / denom;
    if (ua < 0 || ub < 0 || ub > 1) {
      return null;
    }
    return Intersection.stepOne(ray, Vector.lerp(v3, v4, ub), new Vector(nx, ny), ub);
  }
}
