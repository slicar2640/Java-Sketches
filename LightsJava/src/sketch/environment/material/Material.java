package sketch.environment.material;

import java.awt.Color;

import sketch.edit.editpanel.EditPanel;
import sketch.environment.Environment;
import sketch.environment.HitColor;
import sketch.environment.Intersection;
import sketch.environment.colortype.ColorType;

public abstract class Material {
  public Environment environment;
  public ColorType colorType;
  public Color matColor = Color.GRAY;

  public HitColor getColor(Intersection intersection) {
    return new HitColor(colorType.getColor(intersection.factor));
  }

  public void setupEditPanel(EditPanel editPanel) {
    colorType.setupEditPanel(editPanel);
  }

  public abstract void getSaveString(StringBuilder sb);
}
