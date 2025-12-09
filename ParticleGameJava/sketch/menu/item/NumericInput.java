package sketch.menu.item;

import sketch.DrawManager;
import sketch.DrawManager.TextAlign;
import sketch.menu.Menu;
import sketch.menu.clickAction.*;

public class NumericInput extends MenuItem {
  public int value;
  int startValue;
  int min, max;
  float baseHeight;
  float labelHeight;

  public NumericInput(String id, float x, float y, float w, float h, String label, int startValue, int min, int max,
      Menu menu) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.w = w;
    labelHeight = label.equals("") ? 0 : 20;
    this.h = h + labelHeight;
    this.label = label;
    this.menu = menu;
    value = startValue;
    this.startValue = startValue;
    this.min = min;
    this.max = max;
  }

  public void reset() {
    value = startValue;
  }

  public ClickAction click(float mx, float my) {
    if (!visible)
      return new NotClicked();
    if (mx >= x && mx <= x + h && my >= y + labelHeight && my <= y + h) {
      if (menu.manager.sketch.ctrlPressed) {
        value = min;
      } else {
        value = Math.max(min, value - (menu.manager.sketch.shiftPressed ? 10 : 1));
      }
      return new Nothing();
    }
    if (mx >= x + w - h && mx <= x + w && my >= y + labelHeight && my <= y + h) {
      if (menu.manager.sketch.ctrlPressed) {
        value = max;
      } else {
        value = Math.min(max, value + (menu.manager.sketch.shiftPressed ? 10 : 1));
      }
      return new Nothing();
    }
    return new NotClicked();
  }

  public String getValueString() {
    return Integer.toString(value);
  }

  public void show(DrawManager dm) {
    if (!visible)
      return;

    String valueString = getValueString();
    int valueStringWidth = dm.stringWidth(valueString, (h - labelHeight) * 3 / 4) + 4;
    int labelWidth = labelHeight <= 0 ? 0 : dm.stringWidth(label, labelHeight * 3 / 4) + 4;
    w = (int) Math.max((h - labelHeight) * 3, Math.max(valueStringWidth + (h - labelHeight) * 2, labelWidth));

    dm.stroke(100);
    dm.strokeWeight(2);
    dm.fill(100);
    dm.rect(x, y, w, h);
    if (labelHeight > 0) {
      dm.fill(255);
      dm.noStroke();
      dm.text(label, x + 2, y + 2, labelHeight * 3 / 4, TextAlign.LEFT_TOP);
    }
    dm.noStroke();
    dm.fill(0);
    dm.rect(x, y + labelHeight, w, h - labelHeight);
    dm.fill(180);
    dm.rect(x, y + labelHeight, h - labelHeight, h - labelHeight);
    dm.rect(x + w - (h - labelHeight), y + labelHeight, h - labelHeight, h - labelHeight);
    dm.stroke(0);
    dm.strokeWeight(4);
    dm.line(x + 6, y + labelHeight + (h - labelHeight) / 2, x + h - labelHeight - 6,
        y + labelHeight + (h - labelHeight) / 2);
    dm.line(x + w - (h - labelHeight) + 6, y + labelHeight + (h - labelHeight) / 2, x + w - 6,
        y + labelHeight + (h - labelHeight) / 2);
    dm.line(x + w - (h - labelHeight) / 2, y + labelHeight + 6, x + w - (h - labelHeight) / 2, y + h - 6);
    dm.fill(255);
    dm.noStroke();
    dm.text(valueString, x + w / 2, y + labelHeight + (h - labelHeight) / 2, (h - labelHeight) * 3 / 4,
        TextAlign.CENTER_CENTER);
  }
}
