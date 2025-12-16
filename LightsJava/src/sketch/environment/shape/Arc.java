package sketch.environment.shape;

import sketch.environment.ConicalGradientPaint;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.colortype.ColorType;
import sketch.environment.colortype.GradientColor;
import sketch.environment.colortype.SolidColor;
import sketch.environment.colortype.SplitColor;
import sketch.environment.material.Material;
import sketch.environment.snapshot.shape.ArcSnapshot;
import sketch.environment.snapshot.shape.IntersectionShapeSnapshot;
import sketch.util.DrawUtils;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class Arc implements IntersectionShape {
  public Vector center;
  public float radius;
  public float startAngle, endAngle;
  private BoundingBox boundingBox;

  public Arc(Vector center, float radius, float startAngle, float endAngle) {
    this.center = center;
    this.radius = radius;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
    if (endAngle < startAngle) {
      this.endAngle += 360;
    }
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
    float distance = projectedToCenter.mag();
    Vector hitPos;
    if (distance > radius) {
      return null;
    } else {
      float m = (float) Math.sqrt(radius * radius - distance * distance);
      float len1 = projected.mag() * Math.signum(projected.dot(ray.direction)) - m;
      float len2 = projected.mag() * Math.signum(projected.dot(ray.direction)) + m;
      Vector hit1 = Vector.add(ray.origin, Vector.mult(ray.direction, len1));
      Vector hit2 = Vector.add(ray.origin, Vector.mult(ray.direction, len2));
      float d1 = Vector.dist(ray.origin, hit1);
      float d2 = Vector.dist(ray.origin, hit2);
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
      float lastThreshold = 0;
      for (int i = 0; i < c.thresholds.length; i++) {
        float thisThreshold = c.thresholds[i];
        drawUtils.stroke(c.colors[i]);
        drawUtils.arc(center.x, center.y, radius, MathUtils.lerp(startAngle, endAngle, lastThreshold),
            MathUtils.lerp(startAngle, endAngle, thisThreshold));
        lastThreshold = thisThreshold;
      }
      drawUtils.stroke(c.colors[c.colors.length - 1]);
      drawUtils.arc(center.x, center.y, radius, MathUtils.lerp(startAngle, endAngle, lastThreshold), endAngle);
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

  public IntersectionShapeSnapshot getShapshot() {
    return new ArcSnapshot(center.copy(), radius, startAngle, endAngle);
  }
}
