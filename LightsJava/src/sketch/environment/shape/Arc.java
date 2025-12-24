package sketch.environment.shape;

import java.util.ArrayList;

import sketch.edit.EditAngle;
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
import sketch.util.MathUtils;
import sketch.util.Vector;

public class Arc extends IntersectionShape {
  public Vector center;
  public float radius;
  public float startAngle, endAngle;
  private EditPoint centerTool;
  private EditRadius radiusTool;
  private EditAngle startAngleTool, endAngleTool;
  private BoundingBox boundingBox;

  public Arc(Vector center, float radius, float startAngle, float endAngle) {
    this.center = center;
    this.radius = radius;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
    if (endAngle < startAngle) {
      this.endAngle += 360;
    }
    centerTool = new EditPoint(center.x, center.y, this::setCenter);
    radiusTool = new EditRadius(centerTool, radius, this::setRadius);
    startAngleTool = new EditAngle(centerTool, radiusTool, startAngle, this::setStartAngle);
    endAngleTool = new EditAngle(centerTool, radiusTool, endAngle, this::setEndAngle);
    boundingBox = new BoundingBox(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
  }

  private boolean angleHits(float angle) {
    double degAngle = Math.toDegrees(angle);
    if (endAngle < 360) {
      return degAngle >= startAngle && degAngle <= endAngle;
    } else {
      return degAngle >= startAngle || degAngle <= endAngle - 360;
    }
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
      float a1 = (float) ((Vector.sub(hit1, center).heading() + Math.PI * 2) % (Math.PI * 2));
      float a2 = (float) ((Vector.sub(hit2, center).heading() + Math.PI * 2) % (Math.PI * 2));
      if (d1 < d2 && len1 > 0 && angleHits(a1)) {
        hitPos = hit1;
      } else if (len2 > 0 && angleHits(a2)) {
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
    float t;
    if (endAngle < 360) {
      t = MathUtils.map(a, startAngle, endAngle, 0, 1);
    } else {
      if (a < endAngle - 360) {
        a += 360;
      }
      t = MathUtils.map(a, startAngle, endAngle, 0, 1);
    }
    return Intersection.stepOne(ray, hitPos, normal, t);
  }

  public float distToPoint(float mx, float my) {
    if (angleHits((float) ((Math.atan2(my - center.y, mx - center.y) + 2 * Math.PI) % (2 * Math.PI)))) {
      return Math.abs(center.dist(mx, my) - radius);
    } else {
      return (float) Math
          .sqrt(Math.min(center.copy().add(Vector.fromAngle((float) Math.toRadians(startAngle), radius)).distSq(mx, my),
              center.copy().add(Vector.fromAngle((float) Math.toRadians(endAngle), radius)).distSq(mx, my)));
    }
  }

  public void setCenter(float x, float y) {
    center.set(x, y);
    boundingBox = new BoundingBox(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
  }

  public void setRadius(float r) {
    radius = r;
    boundingBox = new BoundingBox(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
  }

  public void setStartAngle(float a) {
    startAngle = a;
    if (endAngle < startAngle) {
      endAngle += 360;
    } else if (endAngle - 360 > startAngle) {
      endAngle -= 360;
    }
  }

  public void setEndAngle(float a) {
    endAngle = a < startAngle ? a + 360 : a;
  }

  public ArrayList<EditTool> getEditTools() {
    ArrayList<EditTool> tools = new ArrayList<>();
    tools.add(centerTool);
    tools.add(radiusTool);
    tools.add(startAngleTool);
    tools.add(endAngleTool);
    return tools;
  }

  public void showEditTools(DrawUtils drawUtils) {
    centerTool.show(drawUtils);
    radiusTool.show(drawUtils);
    startAngleTool.show(drawUtils);
    endAngleTool.show(drawUtils);
  }

  public void show(Material mat, DrawUtils drawUtils) {
    ColorType colorType = mat.colorType;
    drawUtils.strokeWeight(4);
    drawUtils.noFill();
    if (colorType instanceof SolidColor c) {
      drawUtils.stroke(c.color);
      drawUtils.arc(center.x, center.y, radius, startAngle, endAngle);
    } else if (colorType instanceof GradientColor c) {
      drawUtils.stroke(new ConicalGradientPaint(center.toPoint(), startAngle, c.color1, endAngle, c.color2));
      drawUtils.arc(center.x, center.y, radius, startAngle, endAngle);

      // fix end weirdness
      drawUtils.stroke(c.color1);
      drawUtils.point(center.x + radius * (float) Math.cos(Math.toRadians(startAngle)),
          center.y + radius * (float) Math.sin(Math.toRadians(startAngle)));
      drawUtils.stroke(c.color2);
      drawUtils.point(center.x + radius * (float) Math.cos(Math.toRadians(endAngle)),
          center.y + radius * (float) Math.sin(Math.toRadians(endAngle)));
    } else if (colorType instanceof SplitColor c) {
      for (int i = 0; i < c.colors.length; i++) {
        float before = i == 0 ? 0 : c.thresholds[i - 1];
        float after = i == c.thresholds.length ? 1 : c.thresholds[i];
        drawUtils.stroke(c.colors[i]);
        drawUtils.arc(center.x, center.y, radius, MathUtils.lerp(startAngle, endAngle, before),
            MathUtils.lerp(startAngle, endAngle, after));
      }
    }
  }

  public void showMaterial(Material mat, DrawUtils drawUtils) {
    drawUtils.stroke(mat.matColor);
    drawUtils.strokeWeight(8);
    drawUtils.arc(center.x, center.y, radius, startAngle, endAngle);
  }

  public static Arc random(float width, float height) {
    return new Arc(new Vector(MathUtils.random(20, width - 20), MathUtils.random(20, height - 20)),
        MathUtils.random(5, 50), MathUtils.random(360), MathUtils.random(360));
  }
}
