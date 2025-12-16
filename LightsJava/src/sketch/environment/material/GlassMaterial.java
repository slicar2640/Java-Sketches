package sketch.environment.material;

import java.awt.Color;

import sketch.environment.Environment;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.colortype.ColorType;
import sketch.environment.snapshot.material.MaterialSnapshot;

public class GlassMaterial extends Material {
  private Environment environment;

  public GlassMaterial(ColorType colorType, Environment environment) {
    this.colorType = colorType;
    this.environment = environment;
    matColor = new Color(180, 0, 30);
  }

  public Color getColor(Intersection intersection) {
    Color throughColor = Color.BLACK;
    Intersection throughIntersect = environment
        .intersect(new Ray(intersection.position.add(intersection.ray.direction), intersection.ray.direction));
    if (throughIntersect != null) {
      throughColor = throughIntersect.color;
    }
    Color transmission = colorType.getColor(intersection.factor);
    return new Color(throughColor.getRed() * transmission.getRed() / 255,
        throughColor.getGreen() * transmission.getGreen() / 255, throughColor.getBlue() * transmission.getBlue() / 255);
  }

  public MaterialSnapshot getSnapshot() {
    return new MaterialSnapshot(colorType, MaterialSnapshot.MatType.GLASS);
  }
}
