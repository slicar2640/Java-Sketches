class NumericInput extends MenuItem {
  int value;
  int startValue;
  int min, max;
  float baseHeight;
  float labelHeight;
  public NumericInput(String id, float x, float y, float w, float h, String label, int startValue, int min, int max, Menu menu) {
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
    if (mx >= x && mx <= x + h && my >= y + labelHeight && my <= y + h) {
      value = max(min, value - 1);
    }
    if (mx >= x + w - h && mx <= x + w && my >= y + labelHeight && my <= y + h) {
      value = min(max, value + 1);
    }
    return new ClickAction(ActionType.NOTHING);
  }

  public void show() {
    stroke(100);
    strokeWeight(2);
    fill(100);
    rect(x, y, w, h);
    if (labelHeight > 0) {
      fill(255);
      noStroke();
      textSize(labelHeight * 3 / 4);
      textAlign(LEFT, TOP);
      text(label, x + 2, y + 2);
    }
    noStroke();
    fill(0);
    rect(x, y + labelHeight, w, h - labelHeight);
    fill(180);
    rect(x, y + labelHeight, h - labelHeight, h - labelHeight);
    rect(x + w - (h - labelHeight), y + labelHeight, h - labelHeight, h - labelHeight);
    stroke(0);
    strokeWeight(4);
    line(x + 6, y + labelHeight + (h - labelHeight) / 2, x + h - labelHeight - 6, y + labelHeight + (h - labelHeight) / 2);
    line(x + w - (h - labelHeight) + 6, y + labelHeight + (h - labelHeight) / 2, x + w - 6, y + labelHeight + (h - labelHeight) / 2);
    line(x + w - (h - labelHeight) / 2, y + labelHeight + 6, x + w - (h - labelHeight) / 2, y + h - 6);
    fill(255);
    noStroke();
    textAlign(CENTER, CENTER);
    textSize((h - labelHeight) * 3 / 4);
    text(value, x + w / 2, y + labelHeight + (h - labelHeight) / 2);
  }
}
