package sketch.environment.shape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import sketch.edit.EditPoint;
import sketch.edit.EditTool;
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

public class Bezier extends IntersectionShape {
  public static final int GRADIENT_DETAIL = 20;
  public static final float EPSILON = 1e-3f;
  public Vector p1, c1, c2, p2;
  private EditPoint p1Tool, c1Tool, c2Tool, p2Tool;
  public HashMap<SplitColor, Bezier[]> splitSubcurves = new HashMap<>();
  public HashMap<GradientColor, Bezier[]> gradientSubcurves = new HashMap<>();
  private int sampleNum = 30;
  private Vector[] samplePoints = new Vector[sampleNum];
  private boolean isParabola = false;

  public Bezier(Vector p1, Vector c1, Vector c2, Vector p2) {
    this.p1 = p1;
    this.c1 = c1;
    this.c2 = c2;
    this.p2 = p2;
    p1Tool = new EditPoint(p1.x, p1.y, this::setP1);
    c1Tool = new EditPoint(c1.x, c1.y, this::setC1);
    c2Tool = new EditPoint(c2.x, c2.y, this::setC2);
    p2Tool = new EditPoint(p2.x, p2.y, this::setP2);
    setSamplePoints();
  }

  public Bezier subcurve(float t0, float t1) {
    Vector newP1 = pointAt(t0);
    Vector newC1 = pointAt(t0).add(derivativeAt(t0).mult((t1 - t0) / 3));
    Vector newC2 = pointAt(t1).sub(derivativeAt(t1).mult((t1 - t0) / 3));
    Vector newP2 = pointAt(t1);
    return new Bezier(newP1, newC1, newC2, newP2);
  }

  public Vector pointAt(float t) {
    double x = Math.pow(1 - t, 3) * p1.x + 3 * Math.pow(1 - t, 2) * t * c1.x + 3 * (1 - t) * t * t * c2.x
        + t * t * t * p2.x;
    double y = Math.pow(1 - t, 3) * p1.y + 3 * Math.pow(1 - t, 2) * t * c1.y + 3 * (1 - t) * t * t * c2.y
        + t * t * t * p2.y;
    return new Vector((float) x, (float) y);
  }

  public Vector derivativeAt(float t) {
    double x = 3 * Math.pow(1 - t, 2) * (c1.x - p1.x) + 6 * (1 - t) * t * (c2.x - c1.x) + 3 * t * t * (p2.x - c2.x);
    double y = 3 * Math.pow(1 - t, 2) * (c1.y - p1.y) + 6 * (1 - t) * t * (c2.y - c1.y) + 3 * t * t * (p2.y - c2.y);
    return new Vector((float) x, (float) y);
  }

  public Vector normalAt(float t) {
    double dx = 3 * Math.pow(1 - t, 2) * (c1.x - p1.x) + 6 * (1 - t) * t * (c2.x - c1.x) + 3 * t * t * (p2.x - c2.x);
    double dy = 3 * Math.pow(1 - t, 2) * (c1.y - p1.y) + 6 * (1 - t) * t * (c2.y - c1.y) + 3 * t * t * (p2.y - c2.y);
    return new Vector((float) dy, (float) -dx).normalize();
  }

  public void setSamplePoints() {
    for (int i = 0; i < sampleNum; i++) {
      samplePoints[i] = pointAt((float) i / (sampleNum - 1));
    }
  }

  public Intersection intersect(Ray ray) {
    float A = -ray.direction.y;
    float B = ray.direction.x;
    float C = ray.origin.x * ray.direction.y - ray.origin.y * ray.direction.x;

    float[] bx = {-p1.x + 3 * c1.x - 3 * c2.x + p2.x, 3 * p1.x - 6 * c1.x + 3 * c2.x, -3 * p1.x + 3 * c1.x, p1.x};

    float[] by = {-p1.y + 3 * c1.y - 3 * c2.y + p2.y, 3 * p1.y - 6 * c1.y + 3 * c2.y, -3 * p1.y + 3 * c1.y, p1.y};

    float[] coeffs = {A * bx[0] + B * by[0], A * bx[1] + B * by[1], A * bx[2] + B * by[2], A * bx[3] + B * by[3] + C};

    ArrayList<Float> tValues = solveCubic(coeffs[0], coeffs[1], coeffs[2], coeffs[3]);
    ArrayList<Intersection> intersections = (ArrayList<Intersection>) tValues.stream()
        .map(t -> Intersection.stepOne(ray, pointAt(t), normalAt(t), t)).collect(Collectors.toList());
    intersections.removeIf(inter -> {
      Vector v = Vector.sub(inter.position, ray.origin);
      return v.dot(ray.direction) < 0;
    });
    if (intersections.size() == 0)
      return null;

    intersections.sort((a, b) -> (int) (Vector.distSq(a.position, ray.origin) - Vector.distSq(b.position, ray.origin)));
    return intersections.get(0);
  }

  private ArrayList<Float> solveCubic(float a, float b, float c, float d) {
    if (isParabola || Math.abs(a) < EPSILON) {
      return solveQuadratic(b, c, d);
    }

    b /= a;
    c /= a;
    d /= a;

    var q = (3 * c - b * b) / 9;
    var r = (9 * b * c - 27 * d - 2 * b * b * b) / 54;
    var discriminant = q * q * q + r * r;
    ArrayList<Float> roots = new ArrayList<Float>();

    if (discriminant > EPSILON) {
      float sqrtDiscriminant = (float) Math.sqrt(discriminant);
      float s = (float) Math.cbrt(r + sqrtDiscriminant);
      float t = (float) Math.cbrt(r - sqrtDiscriminant);
      roots.add(-b / 3 + (s + t));
    } else if (Math.abs(discriminant) < EPSILON) {
      float s = (float) Math.cbrt(r);
      roots.add(-b / 3 + 2 * s);
      roots.add(-b / 3 - s);
    } else {
      float theta = (float) Math.acos(r / Math.sqrt(-q * q * q));
      float sqrtQ = (float) Math.sqrt(-q);
      roots.add(2 * sqrtQ * (float) Math.cos(theta / 3) - b / 3);
      roots.add(2 * sqrtQ * (float) Math.cos((theta + 2 * Math.PI) / 3) - b / 3);
      roots.add(2 * sqrtQ * (float) Math.cos((theta + 4 * Math.PI) / 3) - b / 3);
    }
    roots.removeIf(root -> root < 0 || root > 1);
    return roots;
  }

  private ArrayList<Float> solveQuadratic(float a, float b, float c) {
    ArrayList<Float> roots = new ArrayList<Float>();
    if (Math.abs(a) < EPSILON) {
      if (Math.abs(b) < EPSILON) {
        return roots;
      }
      roots.add(-c / b);
      return roots;
    }

    float discriminant = b * b - 4 * a * c;
    if (discriminant < -EPSILON) {
    } else if (Math.abs(discriminant) < EPSILON) {
      roots.add(-b / (2 * a));
    } else {
      float sqrtDiscriminant = (float) Math.sqrt(discriminant);
      roots.add((-b + sqrtDiscriminant) / (2 * a));
      roots.add((-b - sqrtDiscriminant) / (2 * a));
    }
    roots.removeIf(root -> root < 0 || root > 1);
    return roots;
  }

  public float distToPoint(float mx, float my) {
    float minDist = Float.MAX_VALUE;
    for (Vector v : samplePoints) {
      minDist = Math.min(minDist, v.distSq(mx, my));
    }
    return (float) Math.sqrt(minDist);
  }

  public void setP1(float x, float y) {
    p1.set(x, y);
    splitSubcurves.clear();
    gradientSubcurves.clear();
    setSamplePoints();
    isParabola = false;
  }

  public void setC1(float x, float y) {
    c1.set(x, y);
    splitSubcurves.clear();
    gradientSubcurves.clear();
    setSamplePoints();
    isParabola = false;
  }

  public void setC2(float x, float y) {
    c2.set(x, y);
    splitSubcurves.clear();
    gradientSubcurves.clear();
    setSamplePoints();
    isParabola = false;
  }

  public void setP2(float x, float y) {
    p2.set(x, y);
    splitSubcurves.clear();
    gradientSubcurves.clear();
    setSamplePoints();
    isParabola = false;
  }

  public ArrayList<EditTool> getEditTools() {
    ArrayList<EditTool> tools = new ArrayList<>();
    tools.add(p1Tool);
    tools.add(c1Tool);
    tools.add(c2Tool);
    tools.add(p2Tool);
    return tools;
  }

  public void showEditTools(DrawUtils drawUtils) {
    p1Tool.show(drawUtils);
    c1Tool.show(drawUtils);
    c2Tool.show(drawUtils);
    p2Tool.show(drawUtils);

    drawUtils.stroke(Color.WHITE);
    drawUtils.strokeDash(2, 5, 5, 0);
    drawUtils.line(p1.x, p1.y, c1.x, c1.y);
    drawUtils.line(p2.x, p2.y, c2.x, c2.y);
  }

  public void recalculateSubcurves(SplitColor splitColor) {
    Bezier[] subcurves = new Bezier[splitColor.colors.length];
    for (int i = 0; i < splitColor.colors.length; i++) {
      float before = i == 0 ? 0 : splitColor.thresholds[i - 1];
      float after = i == splitColor.thresholds.length ? 1 : splitColor.thresholds[i];
      subcurves[i] = subcurve(before, after);
    }
    splitSubcurves.put(splitColor, subcurves);
  }

  public void show(Material mat, DrawUtils drawUtils) {
    ColorType colorType = mat.colorType;
    drawUtils.strokeWeight(4);
    drawUtils.noFill();
    if (colorType instanceof SolidColor c) {
      drawUtils.stroke(c.color);
      drawUtils.bezier(p1.x, p1.y, c1.x, c1.y, c2.x, c2.y, p2.x, p2.y);
    } else if (colorType instanceof GradientColor c) {
      if (!gradientSubcurves.containsKey(c)) {
        Bezier[] subcurves = new Bezier[GRADIENT_DETAIL];
        for (int i = 0; i < GRADIENT_DETAIL; i++) {
          float before = (float) i / GRADIENT_DETAIL;
          float after = (float) (i + 1) / GRADIENT_DETAIL;
          subcurves[i] = subcurve(before, after);
        }
        gradientSubcurves.put(c, subcurves);
      }
      Bezier[] subcurves = gradientSubcurves.get(c);
      for (int i = 0; i < GRADIENT_DETAIL; i++) {
        Bezier b = subcurves[i];
        drawUtils.stroke(DrawUtils.lerpColor(c.color1, c.color2, (float) i / GRADIENT_DETAIL));
        drawUtils.bezier(b.p1.x, b.p1.y, b.c1.x, b.c1.y, b.c2.x, b.c2.y, b.p2.x, b.p2.y);
      }
    } else if (colorType instanceof SplitColor c) {
      if (!splitSubcurves.containsKey(c)) {
        Bezier[] subcurves = new Bezier[c.colors.length];
        for (int i = 0; i < c.colors.length; i++) {
          float before = i == 0 ? 0 : c.thresholds[i - 1];
          float after = i == c.thresholds.length ? 1 : c.thresholds[i];
          subcurves[i] = subcurve(before, after);
        }
        splitSubcurves.put(c, subcurves);
        c.addBezier(this);
      }
      Bezier[] subcurves = splitSubcurves.get(c);
      for (int i = 0; i < c.colors.length; i++) {
        Bezier b = subcurves[i];
        drawUtils.stroke(c.colors[i]);
        drawUtils.bezier(b.p1.x, b.p1.y, b.c1.x, b.c1.y, b.c2.x, b.c2.y, b.p2.x, b.p2.y);
      }
    }
  }

  public void showMaterial(Material mat, DrawUtils drawUtils) {
    drawUtils.stroke(mat.matColor);
    drawUtils.strokeWeight(8);
    drawUtils.bezier(p1.x, p1.y, c1.x, c1.y, c2.x, c2.y, p2.x, p2.y);
  }

  public static Bezier parabola(Vector focus, Vector vertex, float extent) {
    Vector f_v = Vector.sub(vertex, focus);
    Vector perp = f_v.copy().normalize().rot90();
    Vector offset = f_v.copy().normalize().mult(-extent * extent / (4 * f_v.mag()));
    Vector p1 = vertex.copy().sub(Vector.mult(perp, extent)).add(offset);
    Vector c1 = vertex.copy().sub(Vector.mult(perp, extent / 3)).sub(Vector.div(offset, 3));
    Vector c2 = vertex.copy().add(Vector.mult(perp, extent / 3)).sub(Vector.div(offset, 3));
    Vector p2 = vertex.copy().add(Vector.mult(perp, extent)).add(offset);
    Bezier b = new Bezier(p1, c1, c2, p2);
    b.isParabola = true;
    return b;
  }

  public static Bezier random(float width, float height) {
    return new Bezier(new Vector(MathUtils.random(width), MathUtils.random(height)),
        new Vector(MathUtils.random(width), MathUtils.random(height)),
        new Vector(MathUtils.random(width), MathUtils.random(height)),
        new Vector(MathUtils.random(width), MathUtils.random(height)));
  }
}
