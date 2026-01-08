package sketch.edit.editpanel;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import sketch.util.DrawUtils;
import sketch.util.DrawUtils.TextAlign;
import sketch.util.DrawUtils.WeightedStroke;

public class EditCarousel extends EditInput<String> {
  private WeightedStroke stroke = new WeightedStroke(Color.BLACK, 2);
  private Paint middleBackgroundPaint = Color.LIGHT_GRAY;
  private Paint middleTextPaint = Color.BLACK;
  private Paint sideBackgroundPaint = Color.DARK_GRAY;
  private Paint sideTextPaint = Color.BLACK;

  private boolean open = false;
  private String[] options;
  private int chosenIndex;

  private float x, y;
  private float height;
  private HashMap<String, Rectangle2D.Float> buttonBounds = new HashMap<>();

  public EditCarousel(String[] options, int index, EditPanel editPanel) {
    this.options = options;
    this.chosenIndex = index;
    this.editPanel = editPanel;
    for (String option : options) {
      buttonBounds.put(option, new Rectangle2D.Float());
    }
  }

  public EditCarousel setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    calculateButtonBounds();
    return this;
  }

  public EditCarousel setHeight(float h) {
    height = h;
    calculateButtonWidths();
    calculateButtonBounds();
    return this;
  }

  public EditCarousel styleMiddle(Paint bg, Paint txt) {
    middleBackgroundPaint = bg;
    middleTextPaint = txt;
    return this;
  }

  public EditCarousel styleSides(Paint bg, Paint txt) {
    sideBackgroundPaint = bg;
    sideTextPaint = txt;
    return this;
  }

  public EditCarousel setStroke(WeightedStroke stroke) {
    this.stroke = stroke;
    return this;
  }

  public EditCarousel setControlling(Consumer<String> controlling) {
    this.controlling = controlling;
    return this;
  }

  private float getTextHeight() {
    return height - 16;
  }

  private void calculateButtonWidths() {
    for (String option : options) {
      Rectangle2D.Float bounds = this.buttonBounds.get(option);
      bounds.width = editPanel.getDrawUtils().stringWidth(" " + option + " ", getTextHeight());
      bounds.height = height;
      bounds.y = y;
    }
  }

  @Override
  public Rectangle2D.Float getBounds() {
    float minX = Float.MAX_VALUE;
    for (String option : options) {
      minX = Math.min(minX, buttonBounds.get(option).x);
    }
    float width = 0;
    for (String option : options) {
      width += buttonBounds.get(option).width;
    }
    return new Rectangle2D.Float(minX, y, width, height);
  }

  private Rectangle2D.Float getMiddleBounds() {
    return buttonBounds.get(options[chosenIndex]);
  }

  private void calculateButtonBounds() {
    ArrayList<String> optionOrder = new ArrayList<>(options.length);
    int middleIndex = (options.length - 1) / 2;
    for (String option : options) {
      if (!option.equals(options[chosenIndex])) {
        optionOrder.add(option);
      }
    }
    optionOrder.add(middleIndex, options[chosenIndex]);
    float offset = x - getMiddleBounds().width / 2;
    for (int i = middleIndex - 1; i >= 0; i--) {
      Rectangle2D.Float bounds = buttonBounds.get(optionOrder.get(i));
      offset -= bounds.width;
      bounds.x = offset;
    }
    offset = x - getMiddleBounds().width / 2;
    for (int i = middleIndex; i < options.length; i++) {
      Rectangle2D.Float bounds = buttonBounds.get(optionOrder.get(i));
      bounds.x = offset;
      offset += bounds.width;
    }
  }

  @Override
  public void controlValue() {
    if (controlling != null) {
      controlling.accept(options[chosenIndex]);
    }
  }

  @Override
  public void show(DrawUtils drawUtils) {
    if (open) {
      drawUtils.stroke(stroke);
      drawUtils.fill(sideBackgroundPaint);
      for (int i = 0; i < options.length; i++) {
        if (i != chosenIndex) {
          drawUtils.drawShape(buttonBounds.get(options[i]));
        }
      }
      drawUtils.fill(sideTextPaint);
      for (int i = 0; i < options.length; i++) {
        if (i != chosenIndex) {
          Rectangle2D.Float bounds = buttonBounds.get(options[i]);
          drawUtils.text(options[i], bounds.x + bounds.width / 2, y + height / 2, getTextHeight(),
              TextAlign.CENTER_CENTER);
        }
      }
    }
    drawUtils.fill(middleBackgroundPaint);
    drawUtils.stroke(stroke);
    drawUtils.drawShape(getMiddleBounds());
    drawUtils.fill(middleTextPaint);
    Rectangle2D.Float middleBounds = getMiddleBounds();
    drawUtils.text(options[chosenIndex], middleBounds.x + middleBounds.width / 2, y + height / 2, getTextHeight(),
        TextAlign.CENTER_CENTER);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (getMiddleBounds().contains(e.getPoint())) {
      open = !open;
      editPanel.updateVisuals();
    } else if (open) {
      for (int i = 0; i < options.length; i++) {
        if (i != chosenIndex) {
          if (buttonBounds.get(options[i]).contains(e.getPoint())) {
            chosenIndex = i;
            controlValue();
            calculateButtonBounds();
            open = false;
            editPanel.updateVisuals();
            break;
          }
        }
      }
    }
  }
}
