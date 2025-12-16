package sketch.util;

public class MathUtils {
  public static final float SQRT3_2 = (float) Math.sqrt(3) / 2;

  public static float lerp(float a, float b, float t) {
    return a + (b - a) * t;
  }

  public static float[] lerp(float[] a, float[] b, float t) {
    float[] ret = new float[a.length];
    for (int i = 0; i < a.length; i++) {
      ret[i] = a[i] + (b[i] - a[i]) * t;
    }
    return ret;
  }

  public static float[] lerp(float[] a, float[] b, float t, float[] destination) {
    for (int i = 0; i < a.length; i++) {
      destination[i] = a[i] + (b[i] - a[i]) * t;
    }
    return destination;
  }

  public static float map(float v, float fromMin, float fromMax, float toMin, float toMax) {
    return toMin + (v - fromMin) * (toMax - toMin) / (fromMax - fromMin);
  }

  public static int firstOddBefore(int x) {
    return (int) ((x - 1) / 2) * 2 + 1;
  }

  public static float random(float max) {
    return (float) Math.random() * max;
  }

  public static float random(float min, float max) {
    return min + (float) Math.random() * (max - min);
  }

  public static float radians(float degrees) {
    return degrees / 180 * (float) Math.PI;
  }
}
