package sketch.environment;

import java.util.ArrayList;

import sketch.Sketch;
import sketch.environment.colortype.ColorType;
import sketch.environment.colortype.GradientColor;
import sketch.environment.colortype.SolidColor;
import sketch.environment.colortype.SplitColor;
import sketch.environment.material.GlassMaterial;
import sketch.environment.material.LightMaterial;
import sketch.environment.material.Material;
import sketch.environment.shape.Arc;
import sketch.environment.shape.IntersectionShape;
import sketch.environment.shape.Line;
import sketch.environment.snapshot.EnvironmentObjectSnapshot;
import sketch.environment.snapshot.EnvironmentSnapshot;
import sketch.util.DrawUtils;
import sketch.util.Vector;

public class Environment {
  private ArrayList<EnvironmentObject> objects = new ArrayList<>();
  public Sketch sketch;

  public Environment(Sketch sketch) {
    this.sketch = sketch;
  }

  public void addObject(EnvironmentObject object) {
    objects.add(object);
  }

  public void addRandomObject() {
    IntersectionShape shape = randomShape();
    Material material = randomMaterial();
    objects.add(new EnvironmentObject(shape, material));
  }

  public IntersectionShape randomShape() {
    int option = (int) (Math.random() * 2);
    switch (option) {
    case 0:
      return Line.random(sketch.windowManager.width, sketch.windowManager.height);
    case 1:
    default:
      return Arc.random(sketch.windowManager.width, sketch.windowManager.height);
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
      return new GlassMaterial(colorType, this);
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

  public Intersection intersect(Ray ray) {
    float minDist = Float.MAX_VALUE;
    Intersection intersection = null;
    for (EnvironmentObject obj : objects) {
      Intersection objIntersection = obj.intersect(ray);
      if (objIntersection == null)
        continue;
      float d = Vector.dist(ray.origin, objIntersection.position);
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

  public EnvironmentSnapshot getSnapshot() {
    EnvironmentObjectSnapshot[] objectSnapshots = new EnvironmentObjectSnapshot[objects.size()];
    for (int i = 0; i < objects.size(); i++) {
      objectSnapshots[i] = objects.get(i).getSnapshot();
    }
    return new EnvironmentSnapshot(objectSnapshots);
  }
}
