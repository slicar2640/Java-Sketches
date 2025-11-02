class RadioSelector extends MenuItem {
  ArrayList<RadioButton> buttons = new ArrayList<>();
  float buttonHeight;
  int selectedIndex = -1;
  public RadioSelector(String id, float x, float y, float w, float ht, String label, Menu menu) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = 25;
    this.buttonHeight = ht;
    this.label = label;
    this.menu = menu;
  }

  public ClickAction click(float mx, float my) {
    for(RadioButton button : buttons) {
      button.click(mx, my);
    }
    return new ClickAction(ActionType.NOTHING);
  }
  
  public String value() {
    if(selectedIndex < 0 || selectedIndex >= buttons.size()) {
      return null;
    }
    return buttons.get(selectedIndex).id;
  }

  public void select(int index) {
    selectedIndex = index;
    for (int i = 0; i < buttons.size(); i++) {
      buttons.get(i).selected = i == index;
    }
  }
  
  public void reset() {
    if(selectedIndex >= 0 && selectedIndex < buttons.size()) {
      buttons.get(selectedIndex).selected = false;
      selectedIndex = -1;
    }
  }

  public void addButton(String id, String label, DrawFunction show) {
    buttons.add(new RadioButton(id, x, y + h, w, buttonHeight, label, buttons.size(), this, show));
    h += buttonHeight;
  }

  public void show() {
    stroke(75);
    strokeWeight(4);
    fill(75);
    rect(x, y, w, h);
    fill(255);
    noStroke();
    textSize(20);
    textAlign(LEFT, TOP);
    text(label, x + 2, y + 2);
    for(RadioButton button : buttons) {
      button.show();
    }
  }
}

class RadioButton extends MenuItem {
  int index;
  DrawFunction icon;
  RadioSelector parent;
  boolean selected = false;
  public RadioButton(String id, float x, float y, float w, float h, String label, int index, RadioSelector parent, DrawFunction show) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.label = label;
    this.icon = show;
    this.parent = parent;
    this.menu = parent.menu;
    this.index = index;
  }

  public ClickAction click(float mx, float my) {
    if(mx >= x && mx <= x + w && my >= y && my <= y + h) {
      parent.select(index);
    }
    return new ClickAction(ActionType.NOTHING);
  }

  public void show() {
    stroke(selected ? 75 : 100);
    strokeWeight(2);
    fill(selected ? 75 : 100);
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
    textSize(h / 2);
    textAlign(LEFT, CENTER);
    text(label, x + h, y + h / 2);
  }
}
