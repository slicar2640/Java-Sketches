package sketch.environment.colortype;

import java.awt.Color;

import sketch.util.DrawUtils;

public class GradientColor implements ColorType {
  public final Color color1, color2;

  public GradientColor(Color c1, Color c2) {
    color1 = c1;
    color2 = c2;
  }

  public Color getColor(float t) {
    return DrawUtils.lerpColor(color1, color2, t);
  }

  public static GradientColor random() {
    return new GradientColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()),
        new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
  }

  public ColorType copy() {
    return new GradientColor(new Color(color1.getRGB()), new Color(color2.getRGB()));
  }
}
