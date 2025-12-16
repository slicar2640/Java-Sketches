package sketch.environment.snapshot.shape;

import sketch.environment.Intersection;
import sketch.environment.Ray;

public interface IntersectionShapeSnapshot {
  public Intersection intersect(Ray ray);
}
