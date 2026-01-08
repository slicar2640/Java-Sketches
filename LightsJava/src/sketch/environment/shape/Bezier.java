package sketch.environment.shape;

import java.awt.Color;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import sketch.edit.EditPoint;
import sketch.edit.EditTool;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.util.DrawUtils;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class Bezier extends Curve<CubicCurve2D.Float> {
  private Vector p1, c1, c2, p2;
  private EditPoint p1Tool, c1Tool, c2Tool, p2Tool;

  public Bezier(Vector p1, Vector c1, Vector c2, Vector p2) {
    super(CubicCurve2D.Float.class);
    this.p1 = p1;
    this.c1 = c1;
    this.c2 = c2;
    this.p2 = p2;
    p1Tool = new EditPoint(p1.x, p1.y, this::setP1);
    c1Tool = new EditPoint(c1.x, c1.y, this::setC1);
    c2Tool = new EditPoint(c2.x, c2.y, this::setC2);
    p2Tool = new EditPoint(p2.x, p2.y, this::setP2);
    setSamplePoints();
    splitSubcurves = new HashMap<>();
    gradientSubcurves = new CubicCurve2D.Float[GRADIENT_DETAIL];
    recalculateGradientSubcurves();
    boundingBox = new Rectangle2D.Float();
    calculateBoundingBox();
  }

  @Override
  public CubicCurve2D.Float subcurve(float t0, float t1) {
    Vector newP1 = pointAt(t0);
    Vector newC1 = pointAt(t0).add(derivativeAt(t0).mult((t1 - t0) / 3));
    Vector newC2 = pointAt(t1).sub(derivativeAt(t1).mult((t1 - t0) / 3));
    Vector newP2 = pointAt(t1);
    return new CubicCurve2D.Float(newP1.x, newP1.y, newC1.x, newC1.y, newC2.x, newC2.y, newP2.x, newP2.y);
  }

  @Override
  public Vector pointAt(float t) {
    float x = (1 - t) * (1 - t) * (1 - t) * p1.x + 3 * (1 - t) * (1 - t) * t * c1.x + 3 * (1 - t) * t * t * c2.x
        + t * t * t * p2.x;
    float y = (1 - t) * (1 - t) * (1 - t) * p1.y + 3 * (1 - t) * (1 - t) * t * c1.y + 3 * (1 - t) * t * t * c2.y
        + t * t * t * p2.y;
    return new Vector(x, y);
  }

  @Override
  public Vector derivativeAt(float t) {
    float x = 3 * (1 - t) * (1 - t) * (c1.x - p1.x) + 6 * (1 - t) * t * (c2.x - c1.x) + 3 * t * t * (p2.x - c2.x);
    float y = 3 * (1 - t) * (1 - t) * (c1.y - p1.y) + 6 * (1 - t) * t * (c2.y - c1.y) + 3 * t * t * (p2.y - c2.y);
    return new Vector(x, y);
  }

  @Override
  public Vector normalAt(float t) {
    return derivativeAt(t).rot90().normalize();
  }

  @Override
  public void setSamplePoints() {
    for (int i = 0; i < sampleNum; i++) {
      samplePoints[i] = pointAt((float) i / (sampleNum - 1));
    }
  }

  @Override
  public Intersection intersect(Ray ray) {
    if (!boundingBox.intersectsLine(ray.getLine())) {
      return null;
    }
    float A = -ray.getDirection().y;
    float B = ray.getDirection().x;
    float C = ray.getOrigin().x * ray.getDirection().y - ray.getOrigin().y * ray.getDirection().x;

    float[] bx = {-p1.x + 3 * c1.x - 3 * c2.x + p2.x, 3 * p1.x - 6 * c1.x + 3 * c2.x, -3 * p1.x + 3 * c1.x, p1.x};
    float[] by = {-p1.y + 3 * c1.y - 3 * c2.y + p2.y, 3 * p1.y - 6 * c1.y + 3 * c2.y, -3 * p1.y + 3 * c1.y, p1.y};

    double[] coeffs = {A * bx[3] + B * by[3] + C, A * bx[2] + B * by[2], A * bx[1] + B * by[1], A * bx[0] + B * by[0]};

    double[] tValuesArr = new double[3];
    int numT = CubicCurve2D.solveCubic(coeffs, tValuesArr);
    ArrayList<Float> tValues = new ArrayList<>();
    for (int i = 0; i < numT; i++) {
      if (tValuesArr[i] >= 0 && tValuesArr[i] <= 1) {
        tValues.add((float) tValuesArr[i]);
      }
    }
    ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    for (float t : tValues) {
      Vector position = pointAt(t);
      if (Vector.sub(position, ray.getOrigin()).dot(ray.getDirection()) >= 0) {
        intersections.add(Intersection.stepOne(ray, position, normalAt(t), t));
      }
    }
    if (intersections.size() == 0)
      return null;
    if (intersections.size() == 1)
      return intersections.get(0);

    float minDist = Float.MAX_VALUE;
    Intersection closest = null;
    for (Intersection inter : intersections) {
      float distSq = Vector.distSq(inter.position, ray.getOrigin());
      if (distSq < minDist) {
        minDist = distSq;
        closest = inter;
      }
    }
    return closest;
  }

  @Override
  protected void calculateBoundingBox() {
    boundingBox.setRect(new CubicCurve2D.Float(p1.x, p1.y, c1.x, c1.y, c2.x, c2.y, p2.x, p2.y).getBounds2D());
  }

  private void updateAfterChange() {
    splitSubcurves.clear();
    recalculateGradientSubcurves();
    setSamplePoints();
    calculateBoundingBox();
  }

  private void setP1(float x, float y) {
    p1.set(x, y);
    updateAfterChange();
  }

  private void setC1(float x, float y) {
    c1.set(x, y);
    updateAfterChange();
  }

  private void setC2(float x, float y) {
    c2.set(x, y);
    updateAfterChange();
  }

  private void setP2(float x, float y) {
    p2.set(x, y);
    updateAfterChange();
  }

  @Override
  public ArrayList<EditTool> getEditTools() {
    ArrayList<EditTool> tools = new ArrayList<>();
    tools.add(p1Tool);
    tools.add(c1Tool);
    tools.add(c2Tool);
    tools.add(p2Tool);
    return tools;
  }

  @Override
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

  @Override
  protected CubicCurve2D.Float getCurve() {
    return new CubicCurve2D.Float(p1.x, p1.y, c1.x, c1.y, c2.x, c2.y, p2.x, p2.y);
  }

  public static Bezier random(float width, float height) {
    return new Bezier(new Vector(MathUtils.random(width), MathUtils.random(height)),
        new Vector(MathUtils.random(width), MathUtils.random(height)),
        new Vector(MathUtils.random(width), MathUtils.random(height)),
        new Vector(MathUtils.random(width), MathUtils.random(height)));
  }

  @Override
  public void getSaveString(StringBuilder sb) {
    sb.append("Bezier\n");
    sb.append(p1.toStringPrecise());
    sb.append(' ');
    sb.append(c1.toStringPrecise());
    sb.append(' ');
    sb.append(c2.toStringPrecise());
    sb.append(' ');
    sb.append(p2.toStringPrecise());
  }
}
