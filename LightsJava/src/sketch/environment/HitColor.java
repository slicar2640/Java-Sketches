package sketch.environment;

import java.awt.Color;

public class HitColor {
  private float red, green, blue;
  public int depth = 0;

  public HitColor(float r, float g, float b, float max) {
    red = r / max;
    green = g / max;
    blue = b / max;
  }

  public HitColor(float r, float g, float b, float max, int depth) {
    red = r / max;
    green = g / max;
    blue = b / max;
    this.depth = depth;
  }

  public HitColor(Color color) {
    red = color.getRed() / 255f;
    green = color.getGreen() / 255f;
    blue = color.getBlue() / 255f;
  }

  public HitColor(Color color, int depth) {
    red = color.getRed() / 255f;
    green = color.getGreen() / 255f;
    blue = color.getBlue() / 255f;
    this.depth = depth;
  }

  public float getRed(float max) {
    return red * max;
  }

  public float getGreen(float max) {
    return green * max;
  }

  public float getBlue(float max) {
    return blue * max;
  }

  public HitColor add(HitColor other) {
    red += other.getRed(1);
    green += other.getGreen(1);
    blue += other.getBlue(1);
    return this;
  }

  public HitColor multiply(HitColor other) {
    red *= other.getRed(1);
    green *= other.getGreen(1);
    blue *= other.getBlue(1);
    return this;
  }

  public HitColor multiply(float f) {
    red *= f;
    green *= f;
    blue *= f;
    return this;
  }

  public HitColor divide(HitColor other) {
    red /= other.getRed(1);
    green /= other.getGreen(1);
    blue /= other.getBlue(1);
    return this;
  }

  public HitColor divide(float f) {
    red /= f;
    green /= f;
    blue /= f;
    return this;
  }

  public HitColor copy() {
    return new HitColor(red, green, blue, 1, depth);
  }

  public Color toColor() {
    return new Color(Math.clamp(red, 0, 1), Math.clamp(green, 0, 1), Math.clamp(blue, 0, 1));
  }
}
