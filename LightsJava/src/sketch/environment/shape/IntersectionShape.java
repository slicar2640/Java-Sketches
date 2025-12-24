package sketch.environment.shape;

import java.util.ArrayList;

import sketch.edit.EditTool;
import sketch.environment.Environment;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.material.Material;
import sketch.util.DrawUtils;

public abstract class IntersectionShape {
  public Environment environment;

  public abstract Intersection intersect(Ray ray);

  public abstract void show(Material mat, DrawUtils drawUtils);

  public abstract void showMaterial(Material mat, DrawUtils drawUtils);

  public abstract ArrayList<EditTool> getEditTools();

  public abstract void showEditTools(DrawUtils drawUtils);

  public abstract float distToPoint(float mx, float my);
}
