package sketch.menu.item;

import sketch.DrawManager;
import sketch.DrawManager.TextAlign;
import sketch.menu.clickAction.*;

public class RadioButton extends MenuItem {
  int index;
  IconFunction icon;
  RadioSelector parent;
  boolean selected = false;

  public RadioButton(String id, float x, float y, float w, float h, String label, int index, RadioSelector parent,
      IconFunction icon) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.label = label;
    this.icon = icon;
    this.parent = parent;
    this.menu = parent.menu;
    this.index = index;
  }

  public ClickAction click(float mx, float my) {
    if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
      parent.select(index);
      return new Nothing();
    }
    return new NotClicked();
  }

  public void show(DrawManager dm) {
    dm.stroke(selected ? 75 : 100);
    dm.strokeWeight(2);
    dm.fill(selected ? 75 : 100);
    dm.rect(x, y, w, h);
    dm.fill(50);
    dm.noStroke();
    dm.rect(x + 1, y + 1, h - 2, h - 2);
    icon.run(x + 1, y + 1, h - 2, dm);
    dm.fill(255);
    dm.noStroke();
    dm.text(label, x + h, y + h / 2, h / 2, TextAlign.LEFT_CENTER);
  }
}
