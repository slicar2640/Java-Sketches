package sketch.environment;

import sketch.util.Vector;

public class Intersection {
  public final Ray ray;
  public final Vector position;
  public final Vector normal;
  public final float factor;
  public final EnvironmentObject object;
  public final HitColor color;

  public Intersection(Ray ray, Vector position, Vector normal, float factor, EnvironmentObject object, HitColor color) {
    this.ray = ray;
    this.position = position;
    this.normal = normal;
    this.factor = factor;
    this.object = object;
    this.color = color;
  }

  public static Intersection stepOne(Ray ray, Vector position, Vector normal, float factor) {
    return new Intersection(ray, position, normal, factor, null, null);
  }

  public static Intersection stepTwo(Intersection intersection, EnvironmentObject object, HitColor color) {
    return new Intersection(intersection.ray, intersection.position, intersection.normal, intersection.factor, object,
        color);
  }
}
