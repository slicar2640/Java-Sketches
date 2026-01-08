package sketch.environment.colortype;

import java.awt.Color;
import java.awt.Paint;
import java.util.function.Consumer;
import java.util.function.Supplier;

import sketch.util.DrawUtils.WeightedStroke;
import sketch.edit.editpanel.EditPanel;
import sketch.edit.editpanel.EditSlider;
import sketch.util.DynamicPaint;

public interface ColorType {
  public Color getColor(float t);

  public void setupEditPanel(EditPanel editPanel);

  public ColorType copy();

  public static EditSlider[] addColorSliders(EditPanel editPanel, Color initialColor, Consumer<Float> redConsumer,
      Consumer<Float> greenConsumer, Consumer<Float> blueConsumer, Supplier<Paint> redSliderPaint,
      Supplier<Paint> greenSliderPaint, Supplier<Paint> blueSliderPaint) {
    EditSlider[] sliders = new EditSlider[3];
    sliders[0] = editPanel.addInput(new EditSlider(0, 255, initialColor.getRed(), editPanel).setControlling(redConsumer)
        .setPosition(10, editPanel.getNextAvailableY() + 20).setSize(editPanel.width - 20, 20).setHandleSize(20, 30)
        .styleTrack(new DynamicPaint(redSliderPaint), new WeightedStroke(Color.BLACK, 2)));
    sliders[1] = editPanel
        .addInput(new EditSlider(0, 255, initialColor.getGreen(), editPanel).setControlling(greenConsumer)
            .setPosition(10, editPanel.getNextAvailableY() + 10).setSize(editPanel.width - 20, 20).setHandleSize(20, 30)
            .styleTrack(new DynamicPaint(greenSliderPaint), new WeightedStroke(Color.BLACK, 2)));
    sliders[2] = editPanel
        .addInput(new EditSlider(0, 255, initialColor.getBlue(), editPanel).setControlling(blueConsumer)
            .setPosition(10, editPanel.getNextAvailableY() + 10).setSize(editPanel.width - 20, 20).setHandleSize(20, 30)
            .styleTrack(new DynamicPaint(blueSliderPaint), new WeightedStroke(Color.BLACK, 2)));
    return sliders;
  }

  public void getSaveString(StringBuilder sb);
}
