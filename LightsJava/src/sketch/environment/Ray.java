package sketch.environment;

import sketch.util.Vector;

public class Ray {
  public final Vector origin, direction;
  public final int depth;

  public Ray(Vector origin, Vector direction, int depth) {
    this.origin = origin;
    this.direction = direction;
    this.depth = depth;
  }
}
