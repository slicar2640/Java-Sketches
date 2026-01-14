package sketch.edit;

import java.awt.Color;
import java.util.function.BiConsumer;

import sketch.util.DrawUtils;
import sketch.util.Vector;

public class EditPoint extends EditTool {
  private BiConsumer<Float, Float> controlling;
  private float x, y;
  private float hoverDist = 5;

  public EditPoint(float x, float y, BiConsumer<Float, Float> controlling) {
    this.x = x;
    this.y = y;
    this.controlling = controlling;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  @Override
  public void drag(float mx, float my) {
    x = mx;
    y = my;
    controlling.accept(x, y);
  }

  @Override
  public void show(DrawUtils drawUtils) {
    if (highlighted) {
      drawUtils.stroke(Color.ORANGE);
    } else {
      drawUtils.stroke(Color.WHITE);
    }
    drawUtils.strokeWeight(2);
    drawUtils.circle(x, y, hoverDist);
  }

  @Override
  public boolean isHovered(float mx, float my) {
    return Vector.dist(x, y, mx, my) <= hoverDist;
  }
}
