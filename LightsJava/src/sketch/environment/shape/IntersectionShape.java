package sketch.environment.shape;

import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.material.Material;
import sketch.environment.snapshot.shape.IntersectionShapeSnapshot;
import sketch.util.DrawUtils;

public interface IntersectionShape {
  public Intersection intersect(Ray ray);

  public void show(Material mat, DrawUtils drawUtils);

  public void showMaterial(Material mat, DrawUtils drawUtils);

  public IntersectionShapeSnapshot getShapshot();
}
