package sketch.environment.shape;

import sketch.environment.Ray;
import sketch.util.Vector;

public class BoundingBox {
  private final float minX, minY, maxX, maxY;

  public BoundingBox(float x1, float y1, float x2, float y2) {
    minX = Math.min(x1, x2);
    minY = Math.min(y1, y2);
    maxX = Math.max(x1, x2);
    maxY = Math.max(y1, y2);
  }

  public BoundingBox(Vector TL, Vector BR) {
    this(TL.x, TL.y, BR.x, BR.y);
  }

  private boolean raySegmentIntersects(Ray ray, Vector p1, Vector p2) {
    Vector v1 = ray.origin;
    Vector v2 = Vector.add(ray.origin, ray.direction);
    Vector v3 = p1;
    Vector v4 = p2;
    float denom = (v4.y - v3.y) * (v2.x - v1.x) - (v4.x - v3.x) * (v2.y - v1.y);
    if (denom == 0) {
      return false;
    }
    float ua = ((v4.x - v3.x) * (v1.y - v3.y) - (v4.y - v3.y) * (v1.x - v3.x)) / denom;
    float ub = ((v2.x - v1.x) * (v1.y - v3.y) - (v2.y - v1.y) * (v1.x - v3.x)) / denom;
    return ua >= 0 && ub >= 0 && ub <= 1;
  }

  public boolean intersects(Ray ray) {
    if (ray.origin.x >= minX && ray.origin.x <= maxX && ray.origin.y >= minY && ray.origin.y <= maxY) {
      return true;
    }

    Vector topLeft = new Vector(minX, minY);
    Vector topRight = new Vector(maxX, minY);
    Vector bottomLeft = new Vector(minX, maxY);
    Vector bottomRight = new Vector(maxX, maxY);
    return raySegmentIntersects(ray, topLeft, topRight) || raySegmentIntersects(ray, topRight, bottomRight)
        || raySegmentIntersects(ray, bottomLeft, bottomRight) || raySegmentIntersects(ray, topLeft, bottomLeft);
  }
}
