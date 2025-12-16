package sketch.environment.snapshot;

import java.awt.Color;

import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.snapshot.material.MaterialSnapshot;
import sketch.environment.snapshot.shape.IntersectionShapeSnapshot;

public class EnvironmentObjectSnapshot {
  public final IntersectionShapeSnapshot shape;
  public final MaterialSnapshot material;

  public EnvironmentObjectSnapshot(IntersectionShapeSnapshot shape, MaterialSnapshot material) {
    this.shape = shape;
    this.material = material;
  }

  public Color getColor(Intersection intersection) {
    return material.getColor(intersection);
  }

  public Intersection intersect(Ray ray) {
    Intersection intersect = shape.intersect(ray);
    if (intersect != null) {
      return Intersection.stepTwo(intersect, this, getColor(intersect));
    } else {
      return null;
    }
  }
}
