package sketch.environment.shape;

import java.awt.GradientPaint;
import java.util.ArrayList;
import java.util.Iterator;

import sketch.edit.EditPoint;
import sketch.edit.EditTool;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.colortype.*;
import sketch.environment.material.Material;
import sketch.util.DrawUtils;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class Line extends IntersectionShape {
  private Vector p1, p2;
  private Vector normal;
  private EditPoint p1Tool, p2Tool;

  public Line(Vector p1, Vector p2) {
    this.p1 = p1;
    this.p2 = p2;
    p1Tool = new EditPoint(p1.x, p1.y, this::setP1);
    p2Tool = new EditPoint(p2.x, p2.y, this::setP2);
    this.normal = Vector.sub(p2, p1).rot90().normalize();
  }

  @Override
  public Intersection intersect(Ray ray) {
    Vector v1 = ray.getOrigin();
    Vector v2 = Vector.add(ray.getOrigin(), ray.getDirection());
    Vector v3 = p1;
    Vector v4 = p2;
    float denom = (v4.y - v3.y) * (v2.x - v1.x) - (v4.x - v3.x) * (v2.y - v1.y);
    if (denom == 0) {
      return null;
    }
    float ua = ((v4.x - v3.x) * (v1.y - v3.y) - (v4.y - v3.y) * (v1.x - v3.x)) / denom;
    float ub = ((v2.x - v1.x) * (v1.y - v3.y) - (v2.y - v1.y) * (v1.x - v3.x)) / denom;
    if (ua < 0 || ub < 0 || ub > 1) {
      return null;
    }
    return Intersection.stepOne(ray, Vector.lerp(p1, p2, ub), normal, ub);
  }

  @Override
  public float distToPoint(float mx, float my) {
    Vector m = new Vector(mx, my);
    float l2 = Vector.distSq(p1, p2);
    if (l2 == 0.0)
      return Vector.dist(p1, m);
    float t = Math.max(0, Math.min(1, Vector.dot(Vector.sub(m, p1), Vector.sub(p2, p1)) / l2));
    Vector projection = Vector.add(p1, Vector.sub(p2, p1).mult(t));
    return Vector.dist(m, projection);
  }

  private void setP1(float x, float y) {
    p1.set(x, y);
    this.normal = Vector.sub(p2, p1).rot90().normalize();
  }

  private void setP2(float x, float y) {
    p2.set(x, y);
    this.normal = Vector.sub(p2, p1).rot90().normalize();
  }

  @Override
  public ArrayList<EditTool> getEditTools() {
    ArrayList<EditTool> tools = new ArrayList<>();
    tools.add(p1Tool);
    tools.add(p2Tool);
    return tools;
  }

  @Override
  public void showEditTools(DrawUtils drawUtils) {
    p1Tool.show(drawUtils);
    p2Tool.show(drawUtils);
  }

  @Override
  public void show(Material mat, DrawUtils drawUtils) {
    ColorType colorType = mat.colorType;
    drawUtils.strokeWeight(4);
    if (colorType instanceof SolidColor c) {
      drawUtils.stroke(c.color);
      drawUtils.line(p1.x, p1.y, p2.x, p2.y);
    } else if (colorType instanceof GradientColor c) {
      drawUtils.stroke(new GradientPaint(p1.toPoint(), c.color1, p2.toPoint(), c.color2));
      drawUtils.line(p1.x, p1.y, p2.x, p2.y);
    } else if (colorType instanceof SplitColor c) {
      for (int i = 0; i < c.colors.size(); i++) {
        float before = i == 0 ? 0 : c.thresholds.get(i - 1);
        float after = i == c.thresholds.size() ? 1 : c.thresholds.get(i);
        drawUtils.stroke(c.colors.get(i));
        Vector m1 = Vector.lerp(p1, p2, before);
        Vector m2 = Vector.lerp(p1, p2, after);
        drawUtils.line(m1.x, m1.y, m2.x, m2.y);
      }
    }
  }

  @Override
  public void showMaterial(Material mat, DrawUtils drawUtils) {
    drawUtils.stroke(mat.matColor);
    drawUtils.strokeWeight(8);
    drawUtils.line(p1.x, p1.y, p2.x, p2.y);
  }

  @Override
  public void getSaveString(StringBuilder sb) {
    sb.append("Line\n");
    sb.append(p1.toStringPrecise());
    sb.append(' ');
    sb.append(p2.toStringPrecise());
  }

  public static Line random(float width, float height) {
    return new Line(new Vector(MathUtils.random(width), MathUtils.random(height)),
        new Vector(MathUtils.random(width), MathUtils.random(height)));
  }

  public static Line load(Iterator<String> iterator) {
    String pointsLine = iterator.next();
    String p1Point = pointsLine.substring(0, pointsLine.indexOf(')') + 1);
    String p2Point = pointsLine.substring(pointsLine.indexOf('(', 1));
    return new Line(Vector.fromString(p1Point), Vector.fromString(p2Point));
  }
}