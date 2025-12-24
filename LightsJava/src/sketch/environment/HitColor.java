package sketch.environment;

import java.awt.Color;

public class HitColor {
  private float[] components = new float[3];
  public int depth = 0;

  public HitColor(float r, float g, float b, float max) {
    components[0] = r / max;
    components[1] = g / max;
    components[2] = b / max;
  }

  public HitColor(float r, float g, float b, float max, int depth) {
    components[0] = r / max;
    components[1] = g / max;
    components[2] = b / max;
    this.depth = depth;
  }

  public HitColor(Color color) {
    color.getColorComponents(components);
  }

  public HitColor(Color color, int depth) {
    color.getColorComponents(components);
    this.depth = depth;
  }

  public float getRed(float max) {
    return components[0] * max;
  }

  public float getGreen(float max) {
    return components[1] * max;
  }

  public float getBlue(float max) {
    return components[2] * max;
  }

  public HitColor add(HitColor other) {
    components[0] += other.getRed(1);
    components[1] += other.getGreen(1);
    components[2] += other.getBlue(1);
    return this;
  }

  public HitColor multiply(HitColor other) {
    components[0] *= other.getRed(1);
    components[1] *= other.getGreen(1);
    components[2] *= other.getBlue(1);
    return this;
  }

  public HitColor multiply(float f) {
    components[0] *= f;
    components[1] *= f;
    components[2] *= f;
    return this;
  }

  public HitColor divide(HitColor other) {
    components[0] /= other.getRed(1);
    components[1] /= other.getGreen(1);
    components[2] /= other.getBlue(1);
    return this;
  }

  public HitColor divide(float f) {
    components[0] /= f;
    components[1] /= f;
    components[2] /= f;
    return this;
  }

  public HitColor copy() {
    return new HitColor(components[0], components[1], components[2], 1, depth);
  }

  public Color toColor() {
    return new Color(Math.clamp(components[0], 0, 1), Math.clamp(components[1], 0, 1), Math.clamp(components[2], 0, 1));
  }
}
