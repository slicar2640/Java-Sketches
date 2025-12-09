package sketch.menu.item;

import java.awt.Font;
import java.util.ArrayList;

import sketch.DrawManager;
import sketch.DrawManager.TextAlign;
import sketch.menu.Menu;
import sketch.menu.clickAction.*;

public class RadioSelector extends MenuItem {
  ArrayList<RadioButton> buttons = new ArrayList<>();
  ClickFunction<RadioSelector> clickFunction = selector -> {
  };
  float buttonHeight;
  int selectedIndex = -1;
  int previousIndex = -1;
  int textSize = 20;

  public RadioSelector(String id, float x, float y, float w, float ht, String label, Menu menu) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = 25;
    this.buttonHeight = ht;
    this.label = label;
    this.menu = menu;
    resizeToFit();
  }

  public void resizeToFit() {
    String longest = "";
    for (RadioButton b : buttons) {
      if (b.label.length() > longest.length()) {
        longest = b.label;
      }
    }
    float labelWidth = menu.manager.sketch.graphics.getFontMetrics(new Font("Arial", Font.PLAIN, textSize))
        .stringWidth(label);
    float longestWidth = menu.manager.sketch.graphics
        .getFontMetrics(new Font("Arial", Font.PLAIN, (int) buttonHeight / 2)).stringWidth(longest);
    w = buttonHeight + Math.max(labelWidth, longestWidth) + 2;
    for (RadioButton b : buttons) {
      b.w = w;
    }
  }

  public void setClickFunction(ClickFunction<RadioSelector> newFunction) {
    clickFunction = newFunction;
  }

  public ClickAction click(float mx, float my) {
    if (!visible)
      return new NotClicked();
    boolean clicked = false;
    for (RadioButton button : buttons) {
      if (!(button.click(mx, my) instanceof NotClicked)) {
        clicked = true;
        break;
      }
    }
    if (clicked) {
      clickFunction.run(this);
      return new Nothing();
    } else {
      return new NotClicked();
    }
  }

  public String value() {
    if (selectedIndex < 0 || selectedIndex >= buttons.size()) {
      return null;
    }
    return buttons.get(selectedIndex).id;
  }

  public String previousValue() {
    if (previousIndex < 0 || previousIndex >= buttons.size()) {
      return null;
    }
    return buttons.get(previousIndex).id;
  }

  public void select(int index) {
    previousIndex = selectedIndex;
    selectedIndex = index;
    for (int i = 0; i < buttons.size(); i++) {
      buttons.get(i).selected = i == index;
    }
  }

  public void reset() {
    if (selectedIndex >= 0 && selectedIndex < buttons.size()) {
      buttons.get(selectedIndex).selected = false;
      selectedIndex = -1;
    }
  }

  public void addButton(String id, String label, IconFunction show) {
    buttons.add(new RadioButton(id, x, y + h, w, buttonHeight, label, buttons.size(), this, show));
    h += buttonHeight;
    resizeToFit();
  }

  public void show(DrawManager dm) {
    if (!visible)
      return;
    dm.stroke(75);
    dm.strokeWeight(4);
    dm.fill(75);
    dm.rect(x, y, w, h);
    dm.fill(255);
    dm.noStroke();
    dm.text(label, x + 2, y + 2, textSize, TextAlign.LEFT_TOP);
    for (RadioButton button : buttons) {
      button.show(dm);
    }
  }
}