package sketch.environment.material;

import java.awt.Color;

import sketch.edit.editpanel.EditPanel;
import sketch.edit.editpanel.EditSlider;
import sketch.environment.HitColor;
import sketch.environment.Intersection;
import sketch.environment.colortype.ColorType;

public class LightMaterial extends Material {
  public float strength;

  public LightMaterial(ColorType colorType) {
    this.colorType = colorType;
    this.strength = 1;
    matColor = new Color(255, 255, 160);
  }

  public LightMaterial(ColorType colorType, float strength) {
    this.colorType = colorType;
    this.strength = strength;
    matColor = new Color(255, 255, 160);
  }

  public void setStrength(float s) {
    strength = s;
  }

  public HitColor getColor(Intersection intersection) {
    return new HitColor(colorType.getColor(intersection.factor)).multiply(strength);
  }

  public void setupEditPanel(EditPanel editPanel) {
    editPanel.addInput(new EditSlider(0, 3, strength, editPanel).setControlling(this::setStrength)
        .setPosition(10, editPanel.getNextAvailableY() + 20).setSize(editPanel.width - 20, 20).setHandleSize(20, 30));
    colorType.setupEditPanel(editPanel);
  }
}
