package sketch.environment.colortype;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import sketch.util.MathUtils;

public class SplitColor implements ColorType {
  public final Color[] colors;
  public final float[] thresholds;

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

  public ColorType copy() {
    Color[] copyColors = new Color[colors.length];
    for (int i = 0; i < colors.length; i++) {
      copyColors[i] = new Color(colors[i].getRGB());
    }
    float[] copyThresholds = Arrays.copyOf(thresholds, thresholds.length);
    return new SplitColor(copyColors, copyThresholds);
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
}
