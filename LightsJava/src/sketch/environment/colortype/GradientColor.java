package sketch.environment.colortype;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;

import sketch.edit.editpanel.EditPanel;
import sketch.util.DrawUtils;

public class GradientColor implements ColorType {
  public Color color1, color2;
  private float sliderStartX = 10, sliderEndX;

  public GradientColor(Color c1, Color c2) {
    color1 = c1;
    color2 = c2;
  }

  public Color getColor(float t) {
    return DrawUtils.lerpColor(color1, color2, t);
  }

  public void setRed1(float r) {
    color1 = new Color(r / 255f, color1.getGreen() / 255f, color1.getBlue() / 255f);
  }

  public void setGreen1(float g) {
    color1 = new Color(color1.getRed() / 255f, g / 255f, color1.getBlue() / 255f);
  }

  public void setBlue1(float b) {
    color1 = new Color(color1.getRed() / 255f, color1.getGreen() / 255f, b / 255f);
  }

  public void setRed2(float r) {
    color2 = new Color(r / 255f, color2.getGreen() / 255f, color2.getBlue() / 255f);
  }

  public void setGreen2(float g) {
    color2 = new Color(color2.getRed() / 255f, g / 255f, color2.getBlue() / 255f);
  }

  public void setBlue2(float b) {
    color2 = new Color(color2.getRed() / 255f, color2.getGreen() / 255f, b / 255f);
  }

  public Paint getRed1SliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(0, color1.getGreen(), color1.getBlue()),
        new Point((int) sliderEndX, 0), new Color(255, color1.getGreen(), color1.getBlue()));
  }

  public Paint getGreen1SliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(color1.getRed(), 0, color1.getBlue()),
        new Point((int) sliderEndX, 0), new Color(color1.getRed(), 255, color1.getBlue()));
  }

  public Paint getBlue1SliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(color1.getRed(), color1.getGreen(), 0),
        new Point((int) sliderEndX, 0), new Color(color1.getRed(), color1.getGreen(), 255));
  }

  public Paint getRed2SliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(0, color2.getGreen(), color2.getBlue()),
        new Point((int) sliderEndX, 0), new Color(255, color2.getGreen(), color2.getBlue()));
  }

  public Paint getGreen2SliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(color2.getRed(), 0, color2.getBlue()),
        new Point((int) sliderEndX, 0), new Color(color2.getRed(), 255, color2.getBlue()));
  }

  public Paint getBlue2SliderPaint() {
    return new GradientPaint(new Point((int) sliderStartX, 0), new Color(color2.getRed(), color2.getGreen(), 0),
        new Point((int) sliderEndX, 0), new Color(color2.getRed(), color2.getGreen(), 255));
  }

  public void setupEditPanel(EditPanel editPanel) {
    sliderEndX = editPanel.getWidth() - sliderStartX;
    ColorType.addColorSliders(editPanel, color1, this::setRed1, this::setGreen1, this::setBlue1,
        this::getRed1SliderPaint, this::getGreen1SliderPaint, this::getBlue1SliderPaint);
    editPanel.addBlankSpace(20);
    ColorType.addColorSliders(editPanel, color2, this::setRed2, this::setGreen2, this::setBlue2,
        this::getRed2SliderPaint, this::getGreen2SliderPaint, this::getBlue2SliderPaint);
  }

  public ColorType copy() {
    return new GradientColor(new Color(color1.getRGB()), new Color(color2.getRGB()));
  }

  public static GradientColor random() {
    return new GradientColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()),
        new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
  }
}
