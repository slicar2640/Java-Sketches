package sketch.menu.item;

import sketch.DrawManager;
import sketch.DrawManager.TextAlign;
import sketch.menu.Menu;
import sketch.menu.clickAction.*;

public class ToggleButton extends MenuItem {
  public boolean value;
  float labelHeight;
  ClickFunction<ToggleButton> clickFunction = button -> {
  };

  public ToggleButton(String id, float x, float y, float w, String label, boolean defaultValue, Menu menu) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = w / 2;
    this.label = label;
    labelHeight = label.equals("") ? 0 : 20;
    value = defaultValue;
    this.menu = menu;
  }

  public void setClickFunction(ClickFunction<ToggleButton> newFunction) {
    clickFunction = newFunction;
  }

  public ClickAction click(float mx, float my) {
    if (!visible)
      return new NotClicked();
    if (my >= y + labelHeight && my <= y + labelHeight + h && mx >= x && mx <= x + w) {
      value = !value;
      clickFunction.run(this);
      return new Nothing();
    }
    return new NotClicked();
  }

  public void show(DrawManager dm) {
    if (!visible)
      return;
    if (labelHeight > 0) {
      dm.fill(100);
      dm.noStroke();
      dm.rect(x, y, w, labelHeight);
      dm.fill(255);
      dm.noStroke();
      dm.text(label, x + 2, y + 2, labelHeight * 3 / 4, TextAlign.LEFT_TOP);
    }
    dm.fill(value ? 80 : 200);
    dm.rect(x, y + labelHeight, h, h);
    dm.noFill();
    dm.stroke(0);
    dm.strokeWeight(4);
    dm.circle(x + h / 2, y + labelHeight + h / 2, h / 4);
    dm.noStroke();
    dm.fill(value ? 200 : 80);
    dm.rect(x + h, y + labelHeight, h, h);
    dm.stroke(0);
    dm.strokeWeight(4);
    dm.line(x + h * 3 / 2, y + labelHeight + h / 4, x + h * 3 / 2, y + labelHeight + h * 3 / 4);
  }
}
