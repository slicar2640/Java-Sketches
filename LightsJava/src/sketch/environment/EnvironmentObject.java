package sketch.environment;

import java.awt.Color;

import sketch.environment.material.Material;
import sketch.environment.shape.IntersectionShape;
import sketch.environment.snapshot.EnvironmentObjectSnapshot;
import sketch.util.DrawUtils;

public class EnvironmentObject {
  private IntersectionShape shape;
  private Material material;

  public EnvironmentObject(IntersectionShape shape, Material material) {
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

  public void show(DrawUtils drawUtils) {
    shape.show(material, drawUtils);
  }

  public void showMaterial(DrawUtils drawUtils) {
    shape.showMaterial(material, drawUtils);
  }

  public EnvironmentObjectSnapshot getSnapshot() {
    return new EnvironmentObjectSnapshot(shape.getShapshot(), material.getSnapshot());
  }
}
