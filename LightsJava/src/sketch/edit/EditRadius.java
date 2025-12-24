package sketch.edit;

import java.awt.Color;
import java.util.function.Consumer;

import sketch.util.DrawUtils;
import sketch.util.Vector;

public class EditRadius extends EditTool {
  private Consumer<Float> controlling;
  private EditPoint center;
  private float radius;
  private float hoverDist = 10;

  public EditRadius(EditPoint center, float radius, Consumer<Float> controlling) {
    this.center = center;
    this.radius = radius;
    this.controlling = controlling;
  }

  public float getRadius() {
    return radius;
  }

  public void drag(float mx, float my) {
    radius = Vector.dist(mx, my, center.getX(), center.getY());
    controlling.accept(radius);
  }

  public void show(DrawUtils drawUtils) {
    if (highlighted) {
      drawUtils.stroke(Color.ORANGE);
    } else {
      drawUtils.stroke(Color.WHITE);
    }
    drawUtils.strokeDash(2, 5, 5, 0);
    drawUtils.circle(center.getX(), center.getY(), radius);
  }

  public boolean isHovered(float mx, float my) {
    return Math.abs(Vector.dist(mx, my, center.getX(), center.getY()) - radius) < hoverDist;
  }
}
