package sketch.environment.material;

import java.awt.Color;

import sketch.environment.Intersection;
import sketch.environment.colortype.ColorType;
import sketch.environment.snapshot.material.MaterialSnapshot;

public abstract class Material {
  public ColorType colorType;
  public Color matColor = Color.GRAY;

  public Color getColor(Intersection intersection) {
    return colorType.getColor(intersection.factor);
  }

  public abstract MaterialSnapshot getSnapshot();
}
