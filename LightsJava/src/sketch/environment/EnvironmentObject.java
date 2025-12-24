package sketch.environment;

import java.util.ArrayList;

import sketch.edit.EditTool;
import sketch.edit.editpanel.EditPanel;
import sketch.environment.material.Material;
import sketch.environment.shape.IntersectionShape;
import sketch.util.DrawUtils;

public class EnvironmentObject {
  public Environment environment;
  public IntersectionShape shape;
  public Material material;

  public EnvironmentObject(IntersectionShape shape, Material material) {
    this.shape = shape;
    this.material = material;
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
    shape.environment = environment;
    material.environment = environment;
  }

  public void setupEditPanel(EditPanel editPanel) {
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
