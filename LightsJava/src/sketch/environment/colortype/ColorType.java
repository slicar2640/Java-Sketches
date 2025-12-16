package sketch.environment.colortype;

import java.awt.Color;

public interface ColorType {
  public Color getColor(float t);

  public ColorType copy();
}
