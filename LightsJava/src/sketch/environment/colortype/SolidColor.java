package sketch.environment.colortype;

import java.awt.Color;

public class SolidColor implements ColorType {
  public final Color color;

  public SolidColor(Color c) {
    color = c;
  }

  public Color getColor(float t) {
    return color;
  }

  public static SolidColor random() {
    return new SolidColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
  }

  public ColorType copy() {
    return new SolidColor(new Color(color.getRGB()));
  }
}
