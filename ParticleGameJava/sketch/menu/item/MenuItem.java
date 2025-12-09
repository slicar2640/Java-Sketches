package sketch.menu.item;

import sketch.DrawManager;
import sketch.menu.Menu;
import sketch.menu.clickAction.ClickAction;

public abstract class MenuItem {
  public float x, y, w, h;
  public String id, label;
  public Menu menu;
  public boolean visible = true;

  public abstract ClickAction click(float mx, float my);

  public abstract void show(DrawManager dm);
}