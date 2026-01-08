package sketch.environment.shape;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import sketch.environment.colortype.*;
import sketch.environment.material.Material;
import sketch.util.DrawUtils;
import sketch.util.Vector;

public abstract class Curve<C extends Shape> extends IntersectionShape {
  public static final int GRADIENT_DETAIL = 20;

  private Class<C> clazz;
  public HashMap<SplitColor, C[]> splitSubcurves;
  public C[] gradientSubcurves;
  protected int sampleNum = 40;
  protected Vector[] samplePoints = new Vector[sampleNum];
  protected Rectangle2D.Float boundingBox;
  protected final Object subcurveLock = new Object();

  public Curve(Class<C> clazz) {
    this.clazz = clazz;
  }

  public abstract C subcurve(float t0, float t1);

  public abstract Vector pointAt(float t);

  public abstract Vector derivativeAt(float t);

  public abstract Vector normalAt(float t);

  protected abstract void setSamplePoints();

  protected abstract void calculateBoundingBox();

  protected abstract C getCurve();

  protected void recalculateGradientSubcurves() {
    for (int i = 0; i < GRADIENT_DETAIL; i++) {
      float before = (float) i / GRADIENT_DETAIL;
      float after = (float) (i + 1) / GRADIENT_DETAIL;
      gradientSubcurves[i] = subcurve(before, after);
    }
  }

  public void recalculateSplitSubcurves(SplitColor splitColor) {
    synchronized (subcurveLock) {
      ArrayList<C> subcurves = new ArrayList<>();
      for (int i = 0; i < splitColor.colors.size(); i++) {
        float before = i == 0 ? 0 : splitColor.thresholds.get(i - 1);
        float after = i == splitColor.thresholds.size() ? 1 : splitColor.thresholds.get(i);
        subcurves.add(subcurve(before, after));
      }

      @SuppressWarnings("unchecked")
      C[] array = (C[]) java.lang.reflect.Array.newInstance(clazz, subcurves.size());
      splitSubcurves.put(splitColor, subcurves.toArray(array));
    }
  }

  @Override
  public float distToPoint(float mx, float my) {
    float minDist = Float.MAX_VALUE;
    for (Vector v : samplePoints) {
      minDist = Math.min(minDist, v.distSq(mx, my));
    }
    return (float) Math.sqrt(minDist);
  }

  @Override
  public void show(Material mat, DrawUtils drawUtils) {
    ColorType colorType = mat.colorType;
    drawUtils.strokeWeight(4);
    drawUtils.noFill();
    if (colorType instanceof SolidColor c) {
      drawUtils.stroke(c.color);
      drawUtils.drawShape(getCurve());
    } else if (colorType instanceof GradientColor c) {
      synchronized (subcurveLock) {
        for (int i = 0; i < GRADIENT_DETAIL; i++) {
          C b = gradientSubcurves[i];
          drawUtils.stroke(DrawUtils.lerpColor(c.color1, c.color2, (float) i / GRADIENT_DETAIL));
          drawUtils.drawShape(b);
        }
      }
    } else if (colorType instanceof SplitColor c) {
      synchronized (subcurveLock) {
        if (!splitSubcurves.containsKey(c)) {
          recalculateSplitSubcurves(c);
          c.addCurve(this);
        }
        C[] subcurves = splitSubcurves.get(c);
        for (int i = 0; i < c.colors.size(); i++) {
          C b = subcurves[i];
          drawUtils.stroke(c.colors.get(i));
          drawUtils.drawShape(b);
        }
      }
    }
  }

  @Override
  public void showMaterial(Material mat, DrawUtils drawUtils) {
    drawUtils.stroke(mat.matColor);
    drawUtils.strokeWeight(8);
    drawUtils.noFill();
    drawUtils.drawShape(getCurve());
  }
}