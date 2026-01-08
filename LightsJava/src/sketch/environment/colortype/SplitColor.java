package sketch.environment.colortype;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import sketch.edit.editpanel.EditMultiSlider;
import sketch.edit.editpanel.EditPageSelect;
import sketch.edit.editpanel.EditPanel;
import sketch.edit.editpanel.EditSlider;
import sketch.environment.HorizSegmentedPaint;
import sketch.environment.shape.Curve;
import sketch.util.DrawUtils.WeightedStroke;
import sketch.util.DynamicPaint;
import sketch.util.MathUtils;

public class SplitColor implements ColorType {
  public ArrayList<Color> colors;
  public ArrayList<Float> thresholds;
  private float sliderStartX = 10, sliderEndX;
  private int currentColorEditIndex = 0;
  private HashSet<Curve<?>> dependentCurves = new HashSet<>();
  private EditSlider[] colorSliders;
  private EditPageSelect pageSelect;

  public SplitColor(ArrayList<Color> colors, ArrayList<Float> thresholds) {
    this.colors = colors;
    this.thresholds = thresholds;
  }

  @Override
  public Color getColor(float t) {
    for (int i = 0; i < thresholds.size(); i++) {
      if (t < thresholds.get(i)) {
        return colors.get(i);
      }
    }
    return colors.get(colors.size() - 1);
  }

  private void setRed(float r) {
    colors.set(currentColorEditIndex, new Color(r / 255f, colors.get(currentColorEditIndex).getGreen() / 255f,
        colors.get(currentColorEditIndex).getBlue() / 255f));
  }

  private void setGreen(float g) {
    colors.set(currentColorEditIndex, new Color(colors.get(currentColorEditIndex).getRed() / 255f, g / 255f,
        colors.get(currentColorEditIndex).getBlue() / 255f));
  }

  private void setBlue(float b) {
    colors.set(currentColorEditIndex, new Color(colors.get(currentColorEditIndex).getRed() / 255f,
        colors.get(currentColorEditIndex).getGreen() / 255f, b / 255f));
  }

  private void setThresholds(ArrayList<Float> thresholds) {
    for (int i = 0; i < thresholds.size(); i++) {
      this.thresholds.set(i, thresholds.get(i));
    }
    for (Curve<?> curve : dependentCurves) {
      curve.recalculateSplitSubcurves(this);
    }
  }

  private void addThreshold(float threshold) {
    boolean added = false;
    for (int i = thresholds.size() - 1; i >= 0; i--) {
      if (threshold > thresholds.get(i)) {
        thresholds.add(i + 1, threshold);
        colors.add(i + 2, new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
        added = true;
        break;
      }
    }
    if (!added) {
      thresholds.add(0, threshold);
      colors.add(1, new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
    }
    pageSelect.setMax(pageSelect.getMax() + 1);
    for (Curve<?> curve : dependentCurves) {
      curve.recalculateSplitSubcurves(this);
    }
  }

  private void removeThreshold(int index) {
    thresholds.remove(index);
    colors.remove(index + 1);
    for (Curve<?> curve : dependentCurves) {
      curve.recalculateSplitSubcurves(this);
    }
    pageSelect.setMax(pageSelect.getMax() - 1);
    for (Curve<?> curve : dependentCurves) {
      curve.recalculateSplitSubcurves(this);
    }
  }

  private Paint getRedSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0),
        new Color(0, colors.get(currentColorEditIndex).getGreen(), colors.get(currentColorEditIndex).getBlue()),
        new Point((int) sliderEndX, 0),
        new Color(255, colors.get(currentColorEditIndex).getGreen(), colors.get(currentColorEditIndex).getBlue()));
  }

  private Paint getGreenSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0),
        new Color(colors.get(currentColorEditIndex).getRed(), 0, colors.get(currentColorEditIndex).getBlue()),
        new Point((int) sliderEndX, 0),
        new Color(colors.get(currentColorEditIndex).getRed(), 255, colors.get(currentColorEditIndex).getBlue()));
  }

  private Paint getBlueSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0),
        new Color(colors.get(currentColorEditIndex).getRed(), colors.get(currentColorEditIndex).getGreen(), 0),
        new Point((int) sliderEndX, 0),
        new Color(colors.get(currentColorEditIndex).getRed(), colors.get(currentColorEditIndex).getGreen(), 255));
  }

  private Paint getThresholdSliderPaint() {
    float[] thresholdArr = new float[thresholds.size()];
    for (int i = 0; i < thresholds.size(); i++) {
      thresholdArr[i] = MathUtils.map(thresholds.get(i), 0, 1, sliderStartX, sliderEndX);
    }
    return new HorizSegmentedPaint(thresholdArr, colors.toArray(new Color[colors.size()]));
  }

  private void setCurrentColorEditIndex(int index) {
    currentColorEditIndex = index;
    colorSliders[0].setValue(colors.get(currentColorEditIndex).getRed());
    colorSliders[1].setValue(colors.get(currentColorEditIndex).getGreen());
    colorSliders[2].setValue(colors.get(currentColorEditIndex).getBlue());
  }

  @Override
  public void setupEditPanel(EditPanel editPanel) {
    currentColorEditIndex = 0;
    sliderEndX = editPanel.width - sliderStartX;
    editPanel.addInput(new EditMultiSlider(0, 1, new ArrayList<Float>(thresholds), editPanel)
        .setControlling(this::setThresholds).setAddControl(this::addThreshold).setRemoveControl(this::removeThreshold)
        .setPosition(10, editPanel.getNextAvailableY() + 20).setSize(editPanel.width - 20, 20).setHandleSize(10, 30)
        .styleTrack(new DynamicPaint(this::getThresholdSliderPaint), new WeightedStroke(Color.BLACK, 2)));
    pageSelect = editPanel.addInput(new EditPageSelect(0, colors.size() - 1, currentColorEditIndex, editPanel)
        .setControlling(this::setCurrentColorEditIndex).setPosition(10, editPanel.getNextAvailableY() + 20)
        .setButtonSize(30, 30));
    colorSliders = ColorType.addColorSliders(editPanel, colors.get(0), this::setRed, this::setGreen, this::setBlue,
        this::getRedSliderPaint, this::getGreenSliderPaint, this::getBlueSliderPaint);
  }

  public void addCurve(Curve<?> curve) {
    dependentCurves.add(curve);
  }

  @Override
  public ColorType copy() {
    ArrayList<Color> copyColors = new ArrayList<Color>(colors.size());
    for (int i = 0; i < colors.size(); i++) {
      copyColors.set(i, new Color(colors.get(i).getRGB()));
    }
    ArrayList<Float> copyThresholds = new ArrayList<Float>(thresholds);
    return new SplitColor(copyColors, copyThresholds);
  }

  @Override
  public void getSaveString(StringBuilder sb) {
    sb.append("Split\n");
    String thresholdsString = thresholds.toString();
    sb.append(thresholdsString.substring(1, thresholdsString.length() - 1));
    sb.append('\n');
    sb.append(colors.stream().map(color -> String.format("#%06X", (color.getRGB() & 0xFFFFFF)))
        .collect(Collectors.joining(", ")));
  }

  public static SplitColor random() {
    int numColors = (int) MathUtils.random(2, 5);
    ArrayList<Color> colors = new ArrayList<Color>();
    for (int i = 0; i < numColors; i++) {
      colors.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
    }
    ArrayList<Float> thresholds = new ArrayList<Float>();
    float t = 0;
    for (int i = 0; i < numColors - 1; i++) {
      t += MathUtils.random(0.5f / numColors, 1.5f / numColors);
      thresholds.add((float) Math.min(t, 1 - 0.5 / numColors));
    }
    float offset = (float) Math.random();
    return SplitColor.withOffset(colors, thresholds, offset);
  }

  public static SplitColor withOffset(ArrayList<Color> colors, ArrayList<Float> thresholds, float offset) {
    ArrayList<Float> newThresholds = new ArrayList<>();
    ArrayList<Color> newColors = new ArrayList<>();
    for (int i = 0; i < thresholds.size(); i++) {
      thresholds.set(i, thresholds.get(i) + offset);
    }
    newThresholds.add(offset);
    newColors.add(colors.get(colors.size() - 1));
    int firstAboveOneIndex = thresholds.size();
    boolean addLastColor = true;
    for (int i = 0; i < thresholds.size(); i++) {
      if (thresholds.get(i) > 1) {
        firstAboveOneIndex = i;
        break;
      }
      if (thresholds.get(i) == 1) {
        newColors.add(colors.get(i));
        addLastColor = false;
        continue;
      }
      newThresholds.add(thresholds.get(i));
      newColors.add(colors.get(i));
    }
    if (addLastColor) {
      newColors.add(colors.get(firstAboveOneIndex));
    }
    if (firstAboveOneIndex >= 0) {
      for (int i = thresholds.size() - 1; i >= firstAboveOneIndex; i--) {
        newThresholds.addFirst(thresholds.get(i) - 1);
        newColors.addFirst(colors.get(i));
      }
    }
    thresholds = newThresholds;
    colors = newColors;
    return new SplitColor(colors, thresholds);
  }
}
