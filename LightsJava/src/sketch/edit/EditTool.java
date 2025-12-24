package sketch.edit;

import sketch.util.DrawUtils;

public abstract class EditTool {
  public boolean highlighted = false;

  public abstract void drag(float mx, float my);

  public abstract void show(DrawUtils drawUtils);

  public abstract boolean isHovered(float mx, float my);
}
