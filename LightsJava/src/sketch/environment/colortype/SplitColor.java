package sketch.environment.colortype;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import sketch.edit.editpanel.EditMultiSlider;
import sketch.edit.editpanel.EditPageSelect;
import sketch.edit.editpanel.EditPanel;
import sketch.edit.editpanel.EditSlider;
import sketch.environment.shape.Bezier;
import sketch.util.MathUtils;

public class SplitColor implements ColorType {
  public Color[] colors;
  public float[] thresholds;
  private float sliderStartX = 10, sliderEndX;
  private int currentColorEditIndex = 0;
  private HashSet<Bezier> beziers = new HashSet<>();
  private EditSlider[] colorSliders;

  public SplitColor(Color[] colors, float[] thresholds) {
    this.colors = colors;
    this.thresholds = thresholds;
  }

  public Color getColor(float t) {
    for (int i = 0; i < thresholds.length; i++) {
      if (t < thresholds[i]) {
        return colors[i];
      }
    }
    return colors[colors.length - 1];
  }

  public void setRed(float r) {
    colors[currentColorEditIndex] = new Color(r / 255f, colors[currentColorEditIndex].getGreen() / 255f,
        colors[currentColorEditIndex].getBlue() / 255f);
  }

  public void setGreen(float g) {
    colors[currentColorEditIndex] = new Color(colors[currentColorEditIndex].getRed() / 255f, g / 255f,
        colors[currentColorEditIndex].getBlue() / 255f);
  }

  public void setBlue(float b) {
    colors[currentColorEditIndex] = new Color(colors[currentColorEditIndex].getRed() / 255f,
        colors[currentColorEditIndex].getGreen() / 255f, b / 255f);
  }

  public void setThresholds(float[] thresholds) {
    for (int i = 0; i < thresholds.length; i++) {
      this.thresholds[i] = thresholds[i];
    }
    for (Bezier bezier : beziers) {
      bezier.recalculateSubcurves(this);
    }
  }

  public Paint getRedSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0),
        new Color(0, colors[currentColorEditIndex].getGreen(), colors[currentColorEditIndex].getBlue()),
        new Point((int) sliderEndX, 0),
        new Color(255, colors[currentColorEditIndex].getGreen(), colors[currentColorEditIndex].getBlue()));
  }

  public Paint getGreenSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0),
        new Color(colors[currentColorEditIndex].getRed(), 0, colors[currentColorEditIndex].getBlue()),
        new Point((int) sliderEndX, 0),
        new Color(colors[currentColorEditIndex].getRed(), 255, colors[currentColorEditIndex].getBlue()));
  }

  public Paint getBlueSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0),
        new Color(colors[currentColorEditIndex].getRed(), colors[currentColorEditIndex].getGreen(), 0),
        new Point((int) sliderEndX, 0),
        new Color(colors[currentColorEditIndex].getRed(), colors[currentColorEditIndex].getGreen(), 255));
  }

  public void setCurrentColorEditIndex(int index) {
    currentColorEditIndex = index;
    colorSliders[0].setValue(colors[currentColorEditIndex].getRed());
    colorSliders[1].setValue(colors[currentColorEditIndex].getGreen());
    colorSliders[2].setValue(colors[currentColorEditIndex].getBlue());
  }

  public void setupEditPanel(EditPanel editPanel) {
    currentColorEditIndex = 0;
    sliderEndX = editPanel.width - sliderStartX;
    editPanel.addInput(new EditMultiSlider(0, 1, Arrays.copyOf(thresholds, thresholds.length), editPanel)
        .setControlling(this::setThresholds).setPosition(10, editPanel.getNextAvailableY() + 20)
        .setSize(editPanel.width - 20, 20).setHandleSize(10, 30));
    editPanel.addInput(new EditPageSelect(0, colors.length - 1, currentColorEditIndex, editPanel)
        .setPosition(10, editPanel.getNextAvailableY() + 20).setButtonSize(30, 30)
        .setControlling(this::setCurrentColorEditIndex));
    colorSliders = ColorType.addColorSliders(editPanel, colors[0], this::setRed, this::setGreen, this::setBlue,
        this::getRedSliderPaint, this::getGreenSliderPaint, this::getBlueSliderPaint);
  }

  public ColorType copy() {
    Color[] copyColors = new Color[colors.length];
    for (int i = 0; i < colors.length; i++) {
      copyColors[i] = new Color(colors[i].getRGB());
    }
    float[] copyThresholds = Arrays.copyOf(thresholds, thresholds.length);
    return new SplitColor(copyColors, copyThresholds);
  }

  public static SplitColor random() {
    int numColors = (int) MathUtils.random(2, 5);
    Color[] colors = new Color[numColors];
    for (int i = 0; i < numColors; i++) {
      colors[i] = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
    }
    float[] thresholds = new float[numColors - 1];
    float t = 0;
    for (int i = 0; i < numColors - 1; i++) {
      t += MathUtils.random(0.5f / numColors, 1.5f / numColors);
      thresholds[i] = (float) Math.min(t, 1 - 0.5 / numColors);
    }
    float offset = (float) Math.random();
    return SplitColor.withOffset(colors, thresholds, offset);
  }

  public static SplitColor withOffset(Color[] colors, float[] thresholds, float offset) {
    ArrayList<Float> newThresholds = new ArrayList<>();
    ArrayList<Color> newColors = new ArrayList<>();
    for (int i = 0; i < thresholds.length; i++) {
      thresholds[i] += offset;
    }
    newThresholds.add(offset);
    newColors.add(colors[colors.length - 1]);
    int firstAboveOneIndex = thresholds.length;
    boolean addLastColor = true;
    for (int i = 0; i < thresholds.length; i++) {
      if (thresholds[i] > 1) {
        firstAboveOneIndex = i;
        break;
      }
      if (thresholds[i] == 1) {
        newColors.add(colors[i]);
        addLastColor = false;
        continue;
      }
      newThresholds.add(thresholds[i]);
      newColors.add(colors[i]);
    }
    if (addLastColor) {
      newColors.add(colors[firstAboveOneIndex]);
    }
    if (firstAboveOneIndex >= 0) {
      for (int i = thresholds.length - 1; i >= firstAboveOneIndex; i--) {
        newThresholds.addFirst(thresholds[i] - 1);
        newColors.addFirst(colors[i]);
      }
    }
    thresholds = new float[newThresholds.size()];
    for (int i = 0; i < thresholds.length; i++) {
      thresholds[i] = newThresholds.get(i);
    }
    colors = newColors.toArray(new Color[newColors.size()]);
    return new SplitColor(colors, thresholds);
  }

  public void addBezier(Bezier bezier) {
    beziers.add(bezier);
  }
}
