package sketch.environment;

import sketch.util.Vector;

public class Ray {
  public final Vector origin, direction;

  public Ray(Vector origin, Vector direction) {
    this.origin = origin;
    this.direction = direction;
  }
}
