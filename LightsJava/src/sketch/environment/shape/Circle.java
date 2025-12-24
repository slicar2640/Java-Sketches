package sketch.environment.shape;

import java.util.ArrayList;

import sketch.edit.EditPoint;
import sketch.edit.EditRadius;
import sketch.edit.EditTool;
import sketch.environment.ConicalGradientPaint;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.colortype.ColorType;
import sketch.environment.colortype.GradientColor;
import sketch.environment.colortype.SolidColor;
import sketch.environment.colortype.SplitColor;
import sketch.environment.material.Material;
import sketch.util.DrawUtils;
import sketch.util.Vector;

public class Circle extends IntersectionShape {
  public Vector center;
  public float radius;
  private BoundingBox boundingBox;
  private EditPoint centerTool;
  private EditRadius radiusTool;

  public Circle(Vector center, float radius) {
    this.center = center;
    this.radius = radius;
    centerTool = new EditPoint(center.x, center.y, this::setCenter);
    radiusTool = new EditRadius(centerTool, radius, this::setRadius);
    this.boundingBox = new BoundingBox(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
  }

  public Intersection intersect(Ray ray) {
    if (!boundingBox.intersects(ray)) {
      return null;
    }
    Vector originToCenter = Vector.sub(center, ray.origin);
    float dotProduct = originToCenter.dot(ray.direction);
    Vector projected = Vector.mult(ray.direction, dotProduct);
    Vector projectedToCenter = Vector.sub(originToCenter, projected);
    float distanceSq = projectedToCenter.magSq();
    Vector hitPos;
    if (distanceSq > radius * radius) {
      return null;
    } else {
      float m = (float) Math.sqrt(radius * radius - distanceSq);
      float len1 = projected.mag() * Math.signum(projected.dot(ray.direction)) - m;
      float len2 = projected.mag() * Math.signum(projected.dot(ray.direction)) + m;
      Vector hit1 = Vector.add(ray.origin, Vector.mult(ray.direction, len1));
      Vector hit2 = Vector.add(ray.origin, Vector.mult(ray.direction, len2));
      float d1 = Vector.distSq(ray.origin, hit1); // squared because faster
      float d2 = Vector.distSq(ray.origin, hit2); // and still works
      if (d1 < d2 && len1 > 0) {
        hitPos = hit1;
      } else if (len2 > 0) {
        hitPos = hit2;
      } else {
        return null;
      }
    }
    if (Vector.sub(hitPos, ray.origin).dot(ray.direction) < 0) {
      return null;
    }
    Vector atOrigin = Vector.sub(hitPos, center);
    Vector normal = atOrigin.copy().normalize();
    float a = (float) ((Math.toDegrees(atOrigin.heading()) + 360) % 360);
    float t = a / 360;
    return Intersection.stepOne(ray, hitPos, normal, t);
  }

  public float distToPoint(float mx, float my) {
    return Math.abs(center.dist(mx, my) - radius);
  }

  public void setCenter(float x, float y) {
    center.set(x, y);
    boundingBox = new BoundingBox(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
  }

  public void setRadius(float r) {
    radius = r;
    boundingBox = new BoundingBox(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
  }

  public ArrayList<EditTool> getEditTools() {
    ArrayList<EditTool> tools = new ArrayList<>();
    tools.add(centerTool);
    tools.add(radiusTool);
    return tools;
  }

  public void showEditTools(DrawUtils drawUtils) {
    centerTool.show(drawUtils);
    radiusTool.show(drawUtils);
  }

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
      for (int i = 0; i < c.colors.length; i++) {
        float before = i == 0 ? 0 : c.thresholds[i - 1];
        float after = i == c.thresholds.length ? 1 : c.thresholds[i];
        drawUtils.stroke(c.colors[i]);
        drawUtils.arc(center.x, center.y, radius, before, after);
      }
    }
  }

  public void showMaterial(Material mat, DrawUtils drawUtils) {
    drawUtils.stroke(mat.matColor);
    drawUtils.strokeWeight(8);
    drawUtils.circle(center.x, center.y, radius);
  }
}
