package sketch.environment.material;

import java.awt.Color;
import java.util.Iterator;

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

  public static Material load(Iterator<String> iterator) {
    String material = iterator.next();
    return switch (material) {
    case "Light" -> LightMaterial.load(iterator);
    case "Glass" -> GlassMaterial.load(iterator);
    case "Mirror" -> MirrorMaterial.load(iterator);
    default -> throw new IllegalArgumentException(material + "is not a valid material");
    };
  }
}
