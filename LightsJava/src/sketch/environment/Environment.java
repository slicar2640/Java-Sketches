package sketch.environment;

import java.util.ArrayList;

import sketch.Sketch;
import sketch.environment.colortype.*;
import sketch.environment.material.GlassMaterial;
import sketch.environment.material.LightMaterial;
import sketch.environment.material.Material;
import sketch.environment.shape.Arc;
import sketch.environment.shape.Bezier;
import sketch.environment.shape.Circle;
import sketch.environment.shape.IntersectionShape;
import sketch.environment.shape.Line;
import sketch.util.DrawUtils;
import sketch.util.Vector;

public class Environment {
  private ArrayList<EnvironmentObject> objects = new ArrayList<>();
  public Sketch sketch;
  public int maxDepth;

  public Environment(Sketch sketch, int maxDepth) {
    this.sketch = sketch;
    this.maxDepth = maxDepth;
  }

  public EnvironmentObject addObject(EnvironmentObject object) {
    objects.add(object);
    object.setEnvironment(this);
    return object;
  }

  public EnvironmentObject addRandomObject() {
    IntersectionShape shape = randomShape();
    Material material = randomMaterial();
    return addObject(new EnvironmentObject(shape, material));
  }

  public ArrayList<EnvironmentObject> getObjects() {
    return objects;
  }

  public void clearObjects() {
    objects.clear();
  }

  public IntersectionShape randomShape() {
    int option = (int) (Math.random() * 4);
    switch (option) {
    case 0:
      return Line.random(sketch.windowManager.width, sketch.windowManager.height);
    case 1:
      return Arc.random(sketch.windowManager.width, sketch.windowManager.height);
    case 2:
      return Circle.random(sketch.windowManager.width, sketch.windowManager.height);
    case 3:
    default:
      return Bezier.random(sketch.windowManager.width, sketch.windowManager.height);
    }
  }

  public Material randomMaterial() {
    ColorType colorType = randomColorType();
    int option = (int) (Math.random() * 2);
    switch (option) {
    case 0:
      return new LightMaterial(colorType);
    case 1:
    default:
      return new GlassMaterial(colorType);
    }
  }

  public ColorType randomColorType() {
    int option = (int) (Math.random() * 3);
    switch (option) {
    case 0:
      return SolidColor.random();
    case 1:
      return GradientColor.random();
    case 2:
    default:
      return SplitColor.random();
    }
  }

  public float closestDist(float px, float py) {
    float minDist = Float.MAX_VALUE;
    for (EnvironmentObject obj : objects) {
      minDist = Math.min(minDist, obj.shape.distToPoint(px, py));
    }
    return minDist;
  }

  public Intersection intersect(Ray ray) {
    float minDist = Float.MAX_VALUE;
    Intersection intersection = null;
    for (EnvironmentObject obj : objects) {
      Intersection objIntersection = obj.intersect(ray);
      if (objIntersection == null)
        continue;
      float d = Vector.distSq(ray.getOrigin(), objIntersection.position); // squared distance
      if (d < minDist) {
        minDist = d;
        intersection = objIntersection;
      }
    }
    return intersection;
  }

  public void show(DrawUtils drawUtils) {
    for (EnvironmentObject o : objects) {
      o.show(drawUtils);
    }
  }

  public void showMaterials(DrawUtils drawUtils) {
    for (EnvironmentObject o : objects) {
      o.showMaterial(drawUtils);
    }
  }

  public String getSaveString() {
    StringBuilder sb = new StringBuilder();
    for (EnvironmentObject object : objects) {
      object.getSaveString(sb);
      sb.append("\n\n");
    }
    return sb.toString();
  }
}
