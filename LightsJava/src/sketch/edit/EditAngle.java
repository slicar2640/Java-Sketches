package sketch.edit;

import java.awt.Color;
import java.util.function.Consumer;

import sketch.util.DrawUtils;
import sketch.util.Vector;

public class EditAngle extends EditTool {
  private Consumer<Float> controlling;
  private EditPoint center;
  private EditRadius radius;
  private float angle;
  private float hoverDist = 10;

  public EditAngle(EditPoint center, EditRadius radius, float angle, Consumer<Float> controlling) {
    this.center = center;
    this.radius = radius;
    this.angle = angle;
    this.controlling = controlling;
  }

  public void drag(float mx, float my) {
    angle = (float) (Math.toDegrees(Math.atan2(my - center.getY(), mx - center.getX())) + 360) % 360;
    controlling.accept(angle);
  }

  public void show(DrawUtils drawUtils) {
    if (highlighted) {
      drawUtils.stroke(Color.ORANGE);
    } else {
      drawUtils.stroke(Color.WHITE);
    }
    drawUtils.strokeDash(2, 5, 5, 0);
    drawUtils.line(center.getX(), center.getY(),
        center.getX() + radius.getRadius() * (float) Math.cos(Math.toRadians(angle)),
        center.getY() + radius.getRadius() * (float) Math.sin(Math.toRadians(angle)));
  }

  public boolean isHovered(float mx, float my) {
    return pointToSegmentDistance(center.getX(), center.getY(),
        center.getX() + radius.getRadius() * (float) Math.cos(Math.toRadians(angle)),
        center.getY() + radius.getRadius() * (float) Math.sin(Math.toRadians(angle)), mx, my) < hoverDist;
  }

  private float pointToSegmentDistance(float x1, float y1, float x2, float y2, float px, float py) {
    Vector p1 = new Vector(x1, y1);
    Vector p2 = new Vector(x2, y2);
    Vector p = new Vector(px, py);
    float l2 = Vector.distSq(p1, p2);
    if (l2 == 0.0)
      return Vector.dist(p1, p);
    float t = Math.max(0, Math.min(1, Vector.dot(Vector.sub(p, p1), Vector.sub(p2, p1)) / l2));
    Vector projection = Vector.add(p1, Vector.sub(p2, p1).mult(t));
    return Vector.dist(p, projection);
  }
}
