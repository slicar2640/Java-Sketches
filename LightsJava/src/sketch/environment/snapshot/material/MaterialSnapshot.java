package sketch.environment.snapshot.material;

import java.awt.Color;

import sketch.environment.Intersection;
import sketch.environment.colortype.ColorType;

public class MaterialSnapshot {
  public static enum MatType {
    LIGHT, GLASS;
  }

  public final ColorType colorType;
  public final MatType matType;

  public MaterialSnapshot(ColorType colorType, MatType matType) {
    this.colorType = colorType;
    this.matType = matType;
  }

  public Color getColor(Intersection intersection) {
    return colorType.getColor(intersection.factor);
  }

  public Color filter(Color through, Intersection intersection) {
    Color transmission = getColor(intersection);
    return new Color(through.getRed() * transmission.getRed() / 255, through.getGreen() * transmission.getGreen() / 255,
        through.getBlue() * transmission.getBlue() / 255);
  }
}
