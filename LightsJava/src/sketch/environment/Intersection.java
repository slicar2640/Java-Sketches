package sketch.environment;

import java.awt.Color;

import sketch.environment.snapshot.EnvironmentObjectSnapshot;
import sketch.util.Vector;

public class Intersection {
  public final Ray ray;
  public final Vector position;
  public final Vector normal;
  public final float factor;
  public final EnvironmentObject object;
  public final EnvironmentObjectSnapshot objectSnapshot;
  public final Color color;

  public Intersection(Ray ray, Vector position, Vector normal, float factor, EnvironmentObject object, Color color) {
    this.ray = ray;
    this.position = position;
    this.normal = normal;
    this.factor = factor;
    this.object = object;
    this.objectSnapshot = null;
    this.color = color;
  }

  public Intersection(Ray ray, Vector position, Vector normal, float factor, EnvironmentObjectSnapshot objectSnapshot,
      Color color) {
    this.ray = ray;
    this.position = position;
    this.normal = normal;
    this.factor = factor;
    this.object = null;
    this.objectSnapshot = objectSnapshot;
    this.color = color;
  }

  private Intersection(Ray ray, Vector position, Vector normal, float factor) {
    this.ray = ray;
    this.position = position;
    this.normal = normal;
    this.factor = factor;
    this.object = null;
    this.objectSnapshot = null;
    this.color = null;
  }

  public static Intersection stepOne(Ray ray, Vector position, Vector normal, float factor) {
    return new Intersection(ray, position, normal, factor);
  }

  public static Intersection stepTwo(Intersection intersection, EnvironmentObject object, Color color) {
    return new Intersection(intersection.ray, intersection.position, intersection.normal, intersection.factor, object,
        color);
  }

  public static Intersection stepTwo(Intersection intersection, EnvironmentObjectSnapshot objectSnapshot, Color color) {
    return new Intersection(intersection.ray, intersection.position, intersection.normal, intersection.factor,
        objectSnapshot, color);
  }
}
