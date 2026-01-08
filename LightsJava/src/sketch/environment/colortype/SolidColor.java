package sketch.environment.colortype;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;

import sketch.edit.editpanel.EditPanel;

public class SolidColor implements ColorType {
  public Color color;
  private float sliderStartX = 10, sliderEndX;

  public SolidColor(Color c) {
    color = c;
  }

  @Override
  public Color getColor(float t) {
    return color;
  }

  private void setRed(float r) {
    color = new Color(r / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
  }

  private void setGreen(float g) {
    color = new Color(color.getRed() / 255f, g / 255f, color.getBlue() / 255f);
  }

  private void setBlue(float b) {
    color = new Color(color.getRed() / 255f, color.getGreen() / 255f, b / 255f);
  }

  private Paint getRedSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(0, color.getGreen(), color.getBlue()),
        new Point((int) sliderEndX, 0), new Color(255, color.getGreen(), color.getBlue()));
  }

  private Paint getGreenSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(color.getRed(), 0, color.getBlue()),
        new Point((int) sliderEndX, 0), new Color(color.getRed(), 255, color.getBlue()));
  }

  private Paint getBlueSliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(color.getRed(), color.getGreen(), 0),
        new Point((int) sliderEndX, 0), new Color(color.getRed(), color.getGreen(), 255));
  }

  @Override
  public void setupEditPanel(EditPanel editPanel) {
    sliderEndX = editPanel.width - sliderStartX;
    ColorType.addColorSliders(editPanel, color, this::setRed, this::setGreen, this::setBlue, this::getRedSliderPaint,
        this::getGreenSliderPaint, this::getBlueSliderPaint);
  }

  @Override
  public ColorType copy() {
    return new SolidColor(new Color(color.getRGB()));
  }

  @Override
  public void getSaveString(StringBuilder sb) {
    sb.append("Solid\n");
    sb.append(String.format("#%06X", (color.getRGB() & 0xFFFFFF)));
  }

  public static SolidColor random() {
    return new SolidColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
  }
}
