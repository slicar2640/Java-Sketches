abstract class MenuItem {
  float x, y, w, h;
  String id, label;
  Menu menu;

  abstract ClickAction click(float mx, float my);
  abstract void show();
}

class Button extends MenuItem {
  DrawFunction icon;
  ClickFunction clickFunction;
  boolean cancelled = false;
  public Button(String id, float x, float y, float w, float h, String label, Menu menu, ClickFunction clickFunction, DrawFunction show) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.label = label;
    this.icon = show;
    this.clickFunction = clickFunction;
    this.menu = menu;
  }

  public ClickAction click(float mx, float my) {
    if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
      clickFunction.run(this);
      if (cancelled) {
        cancelled = false;
        return new ClickAction(ActionType.NOTHING);
      } else {
        return new ClickAction(ActionType.EXIT);
      }
    }
    return new ClickAction(ActionType.NOTHING);
  }
  
  public void cancelClick() {
    cancelled = true;
  }

  public void show() {
    stroke(100);
    strokeWeight(2);
    fill(100);
    rect(x, y, w, h);
    fill(50);
    noStroke();
    rect(x + 1, y + 1, h - 2, h - 2);
    push();
    translate(x + h / 2, y + h / 2);
    scale(h, h);
    icon.run();
    pop();
    fill(255);
    noStroke();
    textSize(20);
    textAlign(LEFT, CENTER);
    text(label, x + h, y + h / 2);
  }
}

class LinkButton extends Button {
  String linkedMenu;
  public LinkButton(String id, float x, float y, float w, float h, String label, String linkedMenu, Menu menu, ClickFunction clickFunction, DrawFunction show) {
    super(id, x, y, w, h, label, menu, clickFunction, show);
    this.linkedMenu = linkedMenu;
  }

  @Override
    public ClickAction click(float mx, float my) {
    if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
      clickFunction.run(this);
      if (cancelled) {
        cancelled = false;
        return new ClickAction(ActionType.NOTHING);
      } else {
        return new ClickAction(ActionType.LINK, linkedMenu);
      }
    }
    return new ClickAction(ActionType.NOTHING);
  }
}

class ToggleButton extends MenuItem {
  boolean value;
  float labelHeight;
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
  
  public ClickAction click(float mx, float my) {
    if(my >= y + labelHeight && my <= y + labelHeight + h && mx >= x && mx <= x + w) {
      value = !value;
    }
    return new ClickAction(ActionType.NOTHING);
  }
  
  public void show() {
    if (labelHeight > 0) {
      fill(100);
      noStroke();
      rect(x, y, w, labelHeight);
      fill(255);
      noStroke();
      textSize(labelHeight * 3 / 4);
      textAlign(LEFT, TOP);
      text(label, x + 2, y + 2);
    }
    fill(value ? 80 : 200);
    rect(x, y + labelHeight, h, h);
    noFill();
    stroke(0);
    strokeWeight(4);
    ellipse(x + h / 2, y + labelHeight + h / 2, h * 2 / 4, h * 2 / 4);
    noStroke();
    fill(value ? 200 : 80);
    rect(x + h, y + labelHeight, h, h);
    stroke(0);
    strokeWeight(4);
    line(x + h * 3 / 2, y + labelHeight + h / 4, x + h * 3 / 2, y + labelHeight + h * 3 / 4);
  }
}

@FunctionalInterface
  interface DrawFunction {
  void run();
}

@FunctionalInterface
  interface ClickFunction {
  void run(MenuItem item);
}
