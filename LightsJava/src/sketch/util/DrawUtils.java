package sketch.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;

import sketch.WindowManager;

public class DrawUtils {
  public static enum TextAlign {
    LEFT_TOP(0, 2), LEFT_CENTER(0, 1), LEFT_BOTTOM(0, 0), CENTER_TOP(1, 2), CENTER_CENTER(1, 1), CENTER_BOTTOM(1, 0),
    RIGHT_TOP(2, 2), RIGHT_CENTER(2, 1), RIGHT_BOTTOM(2, 0);

    public int horiz, vert;

    TextAlign(int horiz, int vert) {
      this.horiz = horiz;
      this.vert = vert;
    }
  }

  private final Font baseFont = new Font("Arial", Font.PLAIN, 16);
  private WindowManager windowManager;
  private Graphics2D graphics;

  private Paint strokePaint;
  private Paint fillPaint;

  public DrawUtils(WindowManager windowManager) {
    this.windowManager = windowManager;
  }

  public void setGraphics(Graphics2D graphics) {
    this.graphics = graphics;
  }

  public Graphics2D getGraphics() {
    return graphics;
  }

  public void background(Paint p) {
    graphics.setPaint(p);
    graphics.fillRect(0, 0, windowManager.width, windowManager.height);
  }

  public void stroke(Paint p) {
    strokePaint = p;
  }

  public void noStroke() {
    strokePaint = null;
  }

  public void strokeWeight(float w) {
    graphics.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
  }

  public void strokeStyle(Stroke style) {
    graphics.setStroke(style);
  }

  public void fill(Paint p) {
    fillPaint = p;
  }

  public void noFill() {
    fillPaint = null;
  }

  public void point(float x, float y) {
    if (strokePaint == null)
      return;
    graphics.setPaint(strokePaint);
    graphics.drawLine((int) x, (int) y, (int) x, (int) y);
  }

  public void line(float x1, float y1, float x2, float y2) {
    if (strokePaint == null)
      return;
    graphics.setPaint(strokePaint);
    graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
  }

  public void rect(float x, float y, float w, float h) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fillRect((int) x, (int) y, (int) w, (int) h);
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.drawRect((int) x, (int) y, (int) w, (int) h);
    }
  }

  public void circle(float x, float y, float r) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fillArc((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2), 0, 360);
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.drawArc((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2), 0, 360);
    }
  }

  public void arc(float x, float y, float r, float startAngle, float endAngle) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fillArc((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2), 360 - (int) startAngle,
          (int) -(endAngle - startAngle));
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.drawArc((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2), 360 - (int) startAngle,
          (int) -(endAngle - startAngle));
    }
  }

  public void polygon(Point... vertices) {
    int[] x = new int[vertices.length];
    int[] y = new int[vertices.length];
    for (int i = 0; i < vertices.length; i++) {
      x[i] = vertices[i].x;
      y[i] = vertices[i].y;
    }
    Polygon polygon = new Polygon(x, y, vertices.length);
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fill(polygon);
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.draw(polygon);
    }
  }

  public int stringWidth(String string, float size) {
    Font font = baseFont.deriveFont(size);
    return graphics.getFontMetrics(font).stringWidth(string);
  }

  public void text(String txt, float x, float y, float size, TextAlign align) {
    Font font = baseFont.deriveFont(size);
    graphics.setFont(font);
    float textWidth = graphics.getFontMetrics().stringWidth(txt);
    float realX = x - align.horiz * textWidth / 2;
    float realY = y + align.vert * size * 72 / 96 / 2;
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.drawString(txt, realX, realY);
    }
  }

  public static Color rgbFromArray(float... arr) {
    float r = Math.clamp(arr[0], 0, 255);
    float g = Math.clamp(arr[1], 0, 255);
    float b = Math.clamp(arr[2], 0, 255);
    if (r > 1 || g > 1 || b > 1) {
      r /= 255;
      g /= 255;
      b /= 255;
    }
    return new Color(r, g, b);
  }

  public static Color lerpColor(Color color1, Color color2, float t) {
    float[] components = MathUtils.lerp(color1.getColorComponents(null), color2.getColorComponents(null), t);
    return rgbFromArray(components);
  }
}
