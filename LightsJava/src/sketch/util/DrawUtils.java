package sketch.util;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

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

  private final Font baseFont = new Font("Arial", Font.PLAIN, 1);
  private Canvas canvas;
  private Graphics2D graphics;

  private Paint strokePaint;
  private Paint fillPaint;

  private HashMap<String, Cursor> cursorMap = new HashMap<>();

  public DrawUtils(Canvas canvas) {
    this.canvas = canvas;
  }

  public void setGraphics(Graphics2D graphics) {
    this.graphics = graphics;
  }

  public Graphics2D getGraphics() {
    return graphics;
  }

  public void setCursorIcon(String name, Image icon) {
    cursorMap.put(name, Toolkit.getDefaultToolkit().createCustomCursor(icon, new Point(16, 16), name));
  }

  public void setCursor(int cursorType) {
    canvas.setCursor(Cursor.getPredefinedCursor(cursorType));
  }

  public void setCursor(String cursorName) {
    canvas.setCursor(cursorMap.get(cursorName));
  }

  public void background(Paint p) {
    graphics.setPaint(p);
    graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
  }

  public void stroke(Paint p) {
    strokePaint = p;
  }

  public void stroke(WeightedStroke s) {
    stroke(s.color());
    strokeWeight(s.weight());
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
    graphics.draw(new Line2D.Float(x1, y1, x2, y2));
  }

  public void rect(float x, float y, float w, float h) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fill(new Rectangle2D.Float(x, y, w, h));
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.draw(new Rectangle2D.Float(x, y, w, h));
    }
  }

  public void circle(float x, float y, float r) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fill(new Ellipse2D.Float(x - r, y - r, r * 2, r * 2));
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.draw(new Ellipse2D.Float(x - r, y - r, r * 2, r * 2));
    }
  }

  public void arc(float x, float y, float r, float startAngle, float endAngle) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fill(new Arc2D.Float(x - r, y - r, r * 2, r * 2, -startAngle, startAngle - endAngle, Arc2D.OPEN));
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.draw(new Arc2D.Float(x - r, y - r, r * 2, r * 2, -startAngle, startAngle - endAngle, Arc2D.OPEN));
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

  public void drawShape(Shape shape) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fill(shape);
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.draw(shape);
    }
  }

  public void bezier(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fill(new CubicCurve2D.Float(x1, y1, cx1, cy1, cx2, cy2, x2, y2));
    }
    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.draw(new CubicCurve2D.Float(x1, y1, cx1, cy1, cx2, cy2, x2, y2));
    }
  }

  public int stringWidth(String string, float sizePx) {
    Font font = baseFont.deriveFont((float) (sizePx * 96f / 72f));
    FontRenderContext frc = new FontRenderContext(null, true, true);
    Rectangle2D bounds = font.getStringBounds(string, frc);
    return (int) Math.round(bounds.getWidth());
  }

  public void text(String txt, float x, float y, float sizePx, TextAlign align) {
    Font font = baseFont.deriveFont(sizePx * 96 / 72);
    graphics.setFont(font);
    float textWidth = graphics.getFontMetrics().stringWidth(txt);
    float realX = x - align.horiz * textWidth / 2;
    float realY = y + align.vert * sizePx / 2;
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

  public void strokeDash(float weight, float dashLength, float dashGap, float dashOffset) {
    graphics.setStroke(new BasicStroke(weight, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
        new float[] {dashLength, dashGap}, dashOffset));
  }

  public record WeightedStroke(Paint color, float weight) {
  }
}
