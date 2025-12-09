package sketch.menu.item;

import java.awt.Font;

import sketch.DrawManager;
import sketch.DrawManager.TextAlign;
import sketch.menu.Menu;
import sketch.menu.clickAction.*;

public class Button extends MenuItem {
  IconFunction icon;
  int textSize = 20;
  ClickFunction<Button> clickFunction;
  boolean cancelled = false;

  public Button(String id, float x, float y, float w, float h, String label, Menu menu,
      ClickFunction<Button> clickFunction, IconFunction show) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.label = label;
    this.icon = show;
    this.clickFunction = clickFunction;
    this.menu = menu;
    resizeToFit();
  }

  public void resizeToFit() {
    float stringWidth = menu.manager.sketch.graphics.getFontMetrics(new Font("Arial", Font.PLAIN, textSize))
        .stringWidth(label);
    w = h + stringWidth + 4;
  }

  public ClickAction click(float mx, float my) {
    if (!visible)
      return new NotClicked();
    if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
      clickFunction.run(this);
      if (cancelled) {
        cancelled = false;
        return new Nothing();
      } else {
        return new Exit();
      }
    }
    return new NotClicked();
  }

  public void cancelClick() {
    cancelled = true;
  }

  public void show(DrawManager dm) {
    if (!visible)
      return;
    dm.stroke(100);
    dm.strokeWeight(2);
    dm.fill(100);
    dm.rect(x, y, w, h);
    dm.fill(50);
    dm.noStroke();
    dm.rect(x + 1, y + 1, h - 2, h - 2);
    icon.run(x + 1, y + 1, h - 2, dm);
    dm.fill(255);
    dm.noStroke();
    dm.text(label, x + h, y + h / 2, textSize, TextAlign.LEFT_CENTER);
  }
}
