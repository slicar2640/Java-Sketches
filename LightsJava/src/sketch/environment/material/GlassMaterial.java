package sketch.environment.material;

import java.awt.Color;

import sketch.environment.HitColor;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.colortype.ColorType;
import sketch.util.Vector;

public class GlassMaterial extends Material {
  public GlassMaterial(ColorType colorType) {
    this.colorType = colorType;
    matColor = new Color(180, 0, 30);
  }

  public HitColor getColor(Intersection intersection) {
    if (intersection.ray.depth > environment.maxDepth) {
      return new HitColor(Color.BLACK);
    }
    HitColor throughColor = new HitColor(Color.BLACK);
    Intersection throughIntersect = environment
        .intersect(new Ray(Vector.add(intersection.position, intersection.ray.direction), intersection.ray.direction,
            intersection.ray.depth + 1));
    if (throughIntersect != null) {
      throughColor = throughIntersect.color.copy();
      throughColor.depth++;
    }
    HitColor filter = new HitColor(colorType.getColor(intersection.factor));
    return throughColor.multiply(filter);
  }
}
