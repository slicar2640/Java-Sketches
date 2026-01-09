package sketch.environment.shape;

import java.util.ArrayList;
import java.util.Iterator;

import sketch.edit.EditTool;
import sketch.environment.Environment;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.material.Material;
import sketch.util.DrawUtils;

public abstract class IntersectionShape {
  public Environment environment;

  public abstract Intersection intersect(Ray ray);

  public abstract float distToPoint(float mx, float my);

  public abstract ArrayList<EditTool> getEditTools();

  public abstract void showEditTools(DrawUtils drawUtils);

  public abstract void show(Material mat, DrawUtils drawUtils);

  public abstract void showMaterial(Material mat, DrawUtils drawUtils);

  public abstract void getSaveString(StringBuilder sb);

  public static IntersectionShape load(Iterator<String> iterator) {
    String shape = iterator.next();
    return switch (shape) {
    case "Line" -> Line.load(iterator);
    case "Circle" -> Circle.load(iterator);
    case "Arc" -> Arc.load(iterator);
    case "Bezier" -> Bezier.load(iterator);
    case "Parabola" -> Parabola.load(iterator);
    default -> throw new IllegalArgumentException(shape + "is not a valid shape");
    };
  }
}
