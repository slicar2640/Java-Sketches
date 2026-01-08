package sketch.environment;

import java.awt.geom.Line2D;

import sketch.util.Vector;

public class Ray {
  public final int depth;
  private Vector origin, direction;
  private Line2D.Float line;

  public Ray(Vector origin, Vector direction, int depth) {
    this.origin = origin;
    this.direction = direction;
    this.depth = depth;
    line = new Line2D.Float(origin.x, origin.y, origin.x + direction.x * 10000, origin.y + direction.y * 10000);
  }

  public Vector getOrigin() {
    return origin;
  }

  public Vector getDirection() {
    return direction;
  }

  public void setOrigin(Vector v) {
    origin.set(v);
    recalculateLine();
  }

  public void setOrigin(float x, float y) {
    origin.set(x, y);
    recalculateLine();
  }

  public void setDirection(Vector v) {
    direction.set(v);
    recalculateLine();
  }

  public void setDirection(float x, float y) {
    direction.set(x, y);
    recalculateLine();
  }

  private void recalculateLine() {
    line.setLine(origin.x, origin.y, origin.x + direction.x * 10000, origin.y + direction.y * 10000);
  }

  public Line2D.Float getLine() {
    return this.line;
  }
}
