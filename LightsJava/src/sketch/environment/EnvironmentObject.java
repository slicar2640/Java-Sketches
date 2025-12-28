package sketch.environment;

import java.util.ArrayList;

import sketch.edit.EditTool;
import sketch.edit.editpanel.EditCarousel;
import sketch.edit.editpanel.EditPanel;
import sketch.environment.colortype.GradientColor;
import sketch.environment.colortype.SolidColor;
import sketch.environment.colortype.SplitColor;
import sketch.environment.material.GlassMaterial;
import sketch.environment.material.LightMaterial;
import sketch.environment.material.Material;
import sketch.environment.material.MirrorMaterial;
import sketch.environment.shape.IntersectionShape;
import sketch.util.DrawUtils;

public class EnvironmentObject {
  public Environment environment;
  public IntersectionShape shape;
  public Material material;
  private EditPanel currentEditPanel;

  public EnvironmentObject(IntersectionShape shape, Material material) {
    this.shape = shape;
    this.material = material;
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
    shape.environment = environment;
    material.environment = environment;
  }

  private int getMaterialIndex() {
    return switch (material) {
    case LightMaterial light -> 0;
    case GlassMaterial glass -> 1;
    case MirrorMaterial mirr -> 2;
    default -> -1;
    };
  }

  private void changeMaterial(String newMat) {
    material = switch (newMat) {
    case "Light" -> new LightMaterial(material.colorType);
    case "Glass" -> new GlassMaterial(material.colorType);
    case "Mirror" -> new MirrorMaterial(material.colorType);
    default -> material;
    };
    material.environment = environment;
    if (currentEditPanel != null) {
      currentEditPanel.clearInputs();
      setupEditPanel(currentEditPanel);
    }
  }

  private int getColorTypeIndex() {
    return switch (material.colorType) {
    case SolidColor solid -> 0;
    case GradientColor grad -> 1;
    case SplitColor split -> 2;
    default -> -1;
    };
  }

  private void changeColorType(String newColorType) {
    material.colorType = switch (newColorType) {
    case "Solid" -> SolidColor.random();
    case "Gradient" -> GradientColor.random();
    case "Split" -> SplitColor.random();
    default -> material.colorType;
    };
    if (currentEditPanel != null) {
      currentEditPanel.clearInputs();
      setupEditPanel(currentEditPanel);
    }
  }

  public void setupEditPanel(EditPanel editPanel) {
    currentEditPanel = editPanel;
    editPanel.addInput(new EditCarousel(new String[] {"Light", "Glass", "Mirror"}, getMaterialIndex(), editPanel)
        .setControlling(this::changeMaterial).setPosition(editPanel.width / 2f, editPanel.getNextAvailableY() + 10)
        .setHeight(30));
    editPanel.addInput(new EditCarousel(new String[] {"Solid", "Gradient", "Split"}, getColorTypeIndex(), editPanel)
        .setControlling(this::changeColorType).setPosition(editPanel.width / 2f, editPanel.getNextAvailableY() + 5)
        .setHeight(30));
    material.setupEditPanel(editPanel);
  }

  public HitColor getColor(Intersection intersection) {
    return material.getColor(intersection);
  }

  public Intersection intersect(Ray ray) {
    Intersection intersect = shape.intersect(ray);
    if (intersect != null) {
      return Intersection.stepTwo(intersect, this, getColor(intersect));
    } else {
      return null;
    }
  }

  public void show(DrawUtils drawUtils) {
    shape.show(material, drawUtils);
  }

  public void showMaterial(DrawUtils drawUtils) {
    shape.showMaterial(material, drawUtils);
  }

  public void showEditTools(DrawUtils drawUtils) {
    shape.showEditTools(drawUtils);
  }

  public ArrayList<EditTool> getTools() {
    return shape.getEditTools();
  }
}
