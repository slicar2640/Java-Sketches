package sketch.environment.material;

import java.awt.Color;

import sketch.environment.HitColor;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.colortype.ColorType;
import sketch.util.Vector;

public class MirrorMaterial extends Material {
  public MirrorMaterial(ColorType colorType) {
    this.colorType = colorType;
    matColor = new Color(200, 200, 200);
  }

  @Override
  public HitColor getColor(Intersection intersection) {
    if (intersection.ray.depth > environment.maxDepth) {
      return new HitColor(Color.BLACK);
    }
    HitColor throughColor = new HitColor(Color.BLACK);
    Intersection throughIntersect = environment
        .intersect(new Ray(Vector.sub(intersection.position, intersection.ray.getDirection()),
            Vector.reflect(intersection.ray.getDirection(), intersection.normal), intersection.ray.depth + 1));
    if (throughIntersect != null) {
      throughColor = throughIntersect.color.copy();
      throughColor.depth++;
    }
    HitColor filter = new HitColor(colorType.getColor(intersection.factor));
    return throughColor.multiply(filter);
  }

  @Override
  public void getSaveString(StringBuilder sb) {
    sb.append("Mirror\n");
    colorType.getSaveString(sb);
  }
}
