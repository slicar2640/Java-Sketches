package sketch.environment.shape;

import java.util.ArrayList;

import sketch.edit.EditPoint;
import sketch.edit.EditRadius;
import sketch.edit.EditTool;
import sketch.environment.ConicalGradientPaint;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.colortype.*;
import sketch.environment.material.Material;
import sketch.util.DrawUtils;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class Circle extends IntersectionShape {
  private Vector center;
  private float radius;
  private EditPoint centerTool;
  private EditRadius radiusTool;

  public Circle(Vector center, float radius) {
    this.center = center;
    this.radius = radius;
    centerTool = new EditPoint(center.x, center.y, this::setCenter);
    radiusTool = new EditRadius(centerTool, radius, this::setRadius);
  }

  @Override
  public Intersection intersect(Ray ray) {
    Vector originToCenter = Vector.sub(center, ray.getOrigin());
    float dotProduct = originToCenter.dot(ray.getDirection());
    Vector projected = Vector.mult(ray.getDirection(), dotProduct);
    Vector projectedToCenter = Vector.sub(originToCenter, projected);
    float distanceSq = projectedToCenter.magSq();
    Vector hitPos;
    if (distanceSq > radius * radius) {
      return null;
    } else {
      float m = (float) Math.sqrt(radius * radius - distanceSq);
      float len1 = projected.mag() * Math.signum(projected.dot(ray.getDirection())) - m;
      float len2 = projected.mag() * Math.signum(projected.dot(ray.getDirection())) + m;
      Vector hit1 = Vector.add(ray.getOrigin(), Vector.mult(ray.getDirection(), len1));
      Vector hit2 = Vector.add(ray.getOrigin(), Vector.mult(ray.getDirection(), len2));
      float d1 = Vector.distSq(ray.getOrigin(), hit1); // squared because faster
      float d2 = Vector.distSq(ray.getOrigin(), hit2); // and still works
      if (d1 < d2 && len1 > 0) {
        hitPos = hit1;
      } else if (len2 > 0) {
        hitPos = hit2;
      } else {
        return null;
      }
    }
    if (Vector.sub(hitPos, ray.getOrigin()).dot(ray.getDirection()) < 0) {
      return null;
    }
    Vector atOrigin = Vector.sub(hitPos, center);
    Vector normal = atOrigin.copy().normalize();
    float a = (float) ((Math.toDegrees(atOrigin.heading()) + 360) % 360);
    float t = a / 360;
    return Intersection.stepOne(ray, hitPos, normal, t);
  }

  @Override
  public float distToPoint(float mx, float my) {
    return Math.abs(center.dist(mx, my) - radius);
  }

  private void setCenter(float x, float y) {
    center.set(x, y);
  }

  private void setRadius(float r) {
    radius = r;
  }

  @Override
  public ArrayList<EditTool> getEditTools() {
    ArrayList<EditTool> tools = new ArrayList<>();
    tools.add(centerTool);
    tools.add(radiusTool);
    return tools;
  }

  @Override
  public void showEditTools(DrawUtils drawUtils) {
    centerTool.show(drawUtils);
    radiusTool.show(drawUtils);
  }

  @Override
  public void show(Material mat, DrawUtils drawUtils) {
    ColorType colorType = mat.colorType;
    drawUtils.strokeWeight(4);
    drawUtils.noFill();
    if (colorType instanceof SolidColor c) {
      drawUtils.stroke(c.color);
      drawUtils.circle(center.x, center.y, radius);
    } else if (colorType instanceof GradientColor c) {
      drawUtils.stroke(new ConicalGradientPaint(center.toPoint(), 0, c.color1, 360, c.color2));
      drawUtils.circle(center.x, center.y, radius);
    } else if (colorType instanceof SplitColor c) {
      for (int i = 0; i < c.colors.size(); i++) {
        float before = i == 0 ? 0 : c.thresholds.get(i - 1);
        float after = i == c.thresholds.size() ? 1 : c.thresholds.get(i);
        drawUtils.stroke(c.colors.get(i));
        drawUtils.arc(center.x, center.y, radius, before * 360, after * 360);
      }
    }
  }

  @Override
  public void showMaterial(Material mat, DrawUtils drawUtils) {
    drawUtils.stroke(mat.matColor);
    drawUtils.strokeWeight(8);
    drawUtils.noFill();
    drawUtils.circle(center.x, center.y, radius);
  }

  @Override
  public void getSaveString(StringBuilder sb) {
    sb.append("Circle\n");
    sb.append(center.toStringPrecise());
    sb.append(' ');
    sb.append(radius);
  }

  public static Circle random(int width, int height) {
    return new Circle(new Vector(MathUtils.random(20, width - 20), MathUtils.random(20, height - 20)),
        MathUtils.random(5, 50));
  }
}
