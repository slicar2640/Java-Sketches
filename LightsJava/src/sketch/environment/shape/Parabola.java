package sketch.environment.shape;

import java.awt.Color;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sketch.edit.EditPoint;
import sketch.edit.EditTool;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.util.DrawUtils;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class Parabola extends Curve<QuadCurve2D.Float> {
  private Vector focus, vertex;
  private float extent;
  private Vector p1, c, p2;
  private EditPoint focusTool, vertexTool;

  public Parabola(Vector focus, Vector vertex, float extent) {
    super(QuadCurve2D.Float.class);
    this.focus = focus;
    this.vertex = vertex;
    this.extent = extent;
    focusTool = new EditPoint(focus.x, focus.y, this::setFocus);
    vertexTool = new EditPoint(vertex.x, vertex.y, this::setVertex);
    p1 = new Vector(0, 0);
    c = new Vector(0, 0);
    p2 = new Vector(0, 0);
    calculateCurve();
    setSamplePoints();
    splitSubcurves = new HashMap<>();
    gradientSubcurves = new QuadCurve2D.Float[GRADIENT_DETAIL];
    recalculateGradientSubcurves();
    boundingBox = new Rectangle2D.Float();
    calculateBoundingBox();
  }

  @Override
  public QuadCurve2D.Float subcurve(float t0, float t1) {
    Vector newP1 = pointAt(t0);
    Vector newC = pointAt(t0).add(derivativeAt(t0).mult((t1 - t0) / 2));
    Vector newP2 = pointAt(t1);
    return new QuadCurve2D.Float(newP1.x, newP1.y, newC.x, newC.y, newP2.x, newP2.y);
  }

  @Override
  public Vector pointAt(float t) {
    float x = (1 - t) * (1 - t) * p1.x + 2 * (1 - t) * t * c.x + t * t * p2.x;
    float y = (1 - t) * (1 - t) * p1.y + 2 * (1 - t) * t * c.y + t * t * p2.y;
    return new Vector(x, y);
  }

  @Override
  public Vector derivativeAt(float t) {
    float x = 2 * (1 - t) * (c.x - p1.x) + 2 * t * (p2.x - c.x);
    float y = 2 * (1 - t) * (c.y - p1.y) + 2 * t * (p2.y - c.y);
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

    float[] bx = {p1.x - 2 * c.x + p2.x, 2 * (c.x - p1.x), p1.x};
    float[] by = {p1.y - 2 * c.y + p2.y, 2 * (c.y - p1.y), p1.y};

    double[] coeffs = {A * bx[2] + B * by[2] + C, A * bx[1] + B * by[1], A * bx[0] + B * by[0]};

    double[] tValuesArr = new double[2];
    int numT = QuadCurve2D.solveQuadratic(coeffs, tValuesArr);

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
    boundingBox.setRect(getCurve().getBounds2D());
  }

  private void updateAfterChange() {
    splitSubcurves.clear();
    recalculateGradientSubcurves();
    calculateCurve();
    setSamplePoints();
    calculateBoundingBox();
  }

  private void setFocus(float x, float y) {
    focus.set(x, y);
    updateAfterChange();
  }

  private void setVertex(float x, float y) {
    vertex.set(x, y);
    updateAfterChange();
  }

  @Override
  public ArrayList<EditTool> getEditTools() {
    ArrayList<EditTool> tools = new ArrayList<>();
    tools.add(focusTool);
    tools.add(vertexTool);
    return tools;
  }

  @Override
  public void showEditTools(DrawUtils drawUtils) {
    focusTool.show(drawUtils);
    vertexTool.show(drawUtils);

    drawUtils.stroke(Color.WHITE);
    drawUtils.strokeDash(2, 5, 5, 0);
    drawUtils.line(focus.x, focus.y, vertex.x, vertex.y);
  }

  private void calculateCurve() {
    Vector vf = Vector.sub(focus, vertex);
    float vfMag = vf.mag();
    Vector vfNorm = Vector.div(vf, vfMag);
    Vector vfNormRot = vfNorm.copy().rot90();
    Vector endPointsMidpoint = Vector.add(vertex, Vector.mult(vfNorm, extent));
    Vector endPointOffset = Vector.mult(vfNormRot, 2 * (float) Math.sqrt(vfMag * extent));
    p1.set(endPointsMidpoint).sub(endPointOffset);
    p2.set(endPointsMidpoint).add(endPointOffset);
    c.set(vertex.copy().mult(2).sub(endPointsMidpoint));
  }

  @Override
  protected QuadCurve2D.Float getCurve() {
    return new QuadCurve2D.Float(p1.x, p1.y, c.x, c.y, p2.x, p2.y);
  }

  public static Parabola random(float width, float height) {
    return new Parabola(new Vector(MathUtils.random(width), MathUtils.random(height)),
        new Vector(MathUtils.random(width), MathUtils.random(height)), MathUtils.random(50, 300));
  }

  @Override
  public void getSaveString(StringBuilder sb) {
    sb.append("Parabola\n");
    sb.append(focus.toStringPrecise());
    sb.append(' ');
    sb.append(vertex.toStringPrecise());
    sb.append(' ');
    sb.append(extent);
  }

  public static Parabola load(Iterator<String> iterator) {
    String pointsLine = iterator.next();
    String focusPoint = pointsLine.substring(0, pointsLine.indexOf(')') + 1);
    String vertexPoint = pointsLine.substring(pointsLine.indexOf('(', 1));
    float extent = Float.valueOf(iterator.next());
    return new Parabola(Vector.fromString(focusPoint), Vector.fromString(vertexPoint), extent);
  }
}
