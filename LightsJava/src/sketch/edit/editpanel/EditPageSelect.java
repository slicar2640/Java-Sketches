package sketch.edit.editpanel;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;

import sketch.util.DrawUtils;
import sketch.util.DrawUtils.TextAlign;
import sketch.util.DrawUtils.WeightedStroke;

public class EditPageSelect extends EditInput<Integer> {
  private Paint buttonBackgroundPaint = Color.LIGHT_GRAY;
  private WeightedStroke buttonSymbolStroke = new WeightedStroke(Color.GRAY, 2);
  private WeightedStroke stroke = new WeightedStroke(Color.BLACK, 2);
  private Paint numberBackgroundPaint = Color.BLACK;
  private Paint numberPaint = Color.WHITE;
  private float numberSize;

  private int min, max;

  private int value;

  private float x, y;
  private float buttonWidth, numberBoxWidth, height;

  public EditPageSelect(int min, int max, int value, EditPanel editPanel) {
    this.min = min;
    this.max = max;
    this.value = value;
    this.editPanel = editPanel;
  }

  public EditPageSelect setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public EditPageSelect setButtonSize(float w, float h) {
    buttonWidth = w;
    height = h;
    numberSize = h - 8;
    calculateNumberBoxWidth();
    return this;
  }

  public EditPageSelect styleButtons(Paint bg, WeightedStroke symbolStroke) {
    buttonBackgroundPaint = bg;
    buttonSymbolStroke = symbolStroke;
    return this;
  }

  public EditPageSelect styleNumberBox(Paint bg, Paint numPaint, float numSize) {
    numberBackgroundPaint = bg;
    numberPaint = numPaint;
    numberSize = numSize;
    return this;
  }

  public EditPageSelect setStroke(WeightedStroke stroke) {
    this.stroke = stroke;
    return this;
  }

  public EditPageSelect setControlling(Consumer<Integer> controlling) {
    this.controlling = controlling;
    return this;
  }

  private void calculateNumberBoxWidth() {
    this.numberBoxWidth = editPanel.getDrawUtils().stringWidth(" " + Integer.toString(value) + " ", numberSize);
  }

  @Override
  public Rectangle2D.Float getBounds() {
    return new Rectangle2D.Float(x, y, numberBoxWidth + buttonWidth * 2, height);
  }

  private Rectangle2D.Float getLeftButtonBounds() {
    return new Rectangle2D.Float(x, y, buttonWidth, height);
  }

  private Rectangle2D.Float getRightButtonBounds() {
    return new Rectangle2D.Float(x + buttonWidth + numberBoxWidth, y, buttonWidth, height);
  }

  @Override
  public void controlValue() {
    if (controlling != null) {
      controlling.accept(value);
    }
  }

  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
    if (value < min) {
      value = min;
      controlValue();
      editPanel.updateVisuals();
      calculateNumberBoxWidth();
    }
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
    if (value > max) {
      value = max;
      controlValue();
      editPanel.updateVisuals();
      calculateNumberBoxWidth();
    }
  }

  @Override
  public void show(DrawUtils drawUtils) {
    drawUtils.fill(buttonBackgroundPaint);
    drawUtils.noStroke();
    drawUtils.rect(x, y, buttonWidth, height);
    drawUtils.rect(x + buttonWidth + numberBoxWidth, y, buttonWidth, height);
    drawUtils.stroke(buttonSymbolStroke);
    drawUtils.line(x + buttonWidth * 2 / 3, y + height / 3, x + buttonWidth / 3, y + height / 2);
    drawUtils.line(x + buttonWidth * 2 / 3, y + height * 2 / 3, x + buttonWidth / 3, y + height / 2);
    drawUtils.line(x + buttonWidth + numberBoxWidth + buttonWidth / 3, y + height / 3,
        x + buttonWidth + numberBoxWidth + buttonWidth * 2 / 3, y + height / 2);
    drawUtils.line(x + buttonWidth + numberBoxWidth + buttonWidth / 3, y + height * 2 / 3,
        x + buttonWidth + numberBoxWidth + buttonWidth * 2 / 3, y + height / 2);

    drawUtils.fill(numberBackgroundPaint);
    drawUtils.noStroke();
    drawUtils.rect(x + buttonWidth, y, numberBoxWidth, height);
    drawUtils.fill(numberPaint);
    drawUtils.text(Integer.toString(value), x + buttonWidth + numberBoxWidth / 2, y + height / 2, numberSize,
        TextAlign.CENTER_CENTER);

    drawUtils.stroke(stroke);
    drawUtils.line(x + buttonWidth, y, x + buttonWidth, y + height);
    drawUtils.line(x + buttonWidth + numberBoxWidth, y, x + buttonWidth + numberBoxWidth, y + height);
    drawUtils.noFill();
    drawUtils.rect(x, y, numberBoxWidth + buttonWidth * 2, height);
  }

  public void mousePressed(MouseEvent e) {
    if (getLeftButtonBounds().contains(e.getPoint()) && value > min) {
      value--;
    } else if (getRightButtonBounds().contains(e.getPoint()) && value < max) {
      value++;
    } else {
      return;
    }
    controlValue();
    editPanel.updateVisuals();
    calculateNumberBoxWidth();
  }
}
