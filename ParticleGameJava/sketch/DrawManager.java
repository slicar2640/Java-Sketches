package sketch;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;

public class DrawManager {
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
  private Graphics2D g;
  private ParticleGameJava sketch;
  private Paint strokePaint;
  private Paint fillPaint;

  public DrawManager(ParticleGameJava sketch) {
    this.sketch = sketch;
  }

  public void setGraphics(Graphics2D g) {
    this.g = g;
  }

  public void background(Paint p) {
    g.setPaint(p);
    g.fillRect(0, 0, sketch.width, sketch.height);
  }

  public void background(int r, int g, int b, int a) {
    r = Math.clamp(r, 0, 255);
    g = Math.clamp(g, 0, 255);
    b = Math.clamp(b, 0, 255);
    a = Math.clamp(a, 0, 255);
    this.g.setPaint(new Color((int) r, (int) g, (int) b, (int) a));
    this.g.fillRect(0, 0, sketch.width, sketch.height);
  }

  public void background(int r, int g, int b) {
    background(r, b, b, 255);
  }

  public void background(int v, int a) {
    background(v, v, v, a);
  }

  public void background(int v) {
    background(v, v, v, 255);
  }

  public void stroke(Paint p) {
    strokePaint = p;
  }

  public void stroke(float r, float g, float b, float a) {
    r = Math.clamp(r, 0, 255);
    g = Math.clamp(g, 0, 255);
    b = Math.clamp(b, 0, 255);
    a = Math.clamp(a, 0, 255);
    strokePaint = new Color((int) r, (int) g, (int) b, (int) a);
  }

  public void stroke(float r, float g, float b) {
    stroke(r, g, b, 255);
  }

  public void stroke(float v, float a) {
    stroke(v, v, v, a);
  }

  public void stroke(float v) {
    stroke(v, v, v, 255);
  }

  public void noStroke() {
    strokePaint = null;
  }

  public void strokeWeight(float w) {
    g.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
  }

  public void strokeStyle(float w, int cap, int join) {
    g.setStroke(new BasicStroke(w, cap, join));
  }

  public void fill(Paint p) {
    fillPaint = p;
  }

  public void fill(float r, float g, float b, float a) {
    r = Math.clamp(r, 0, 255);
    g = Math.clamp(g, 0, 255);
    b = Math.clamp(b, 0, 255);
    a = Math.clamp(a, 0, 255);
    fillPaint = new Color((int) r, (int) g, (int) b, (int) a);
  }

  public void fill(float r, float g, float b) {
    fill(r, g, b, 255);
  }

  public void fill(float v, float a) {
    fill(v, v, v, a);
  }

  public void fill(float v) {
    fill(v, v, v, 255);
  }

  public void noFill() {
    fillPaint = null;
  }

  public void point(float x, float y) {
    if (strokePaint == null)
      return;
    g.setPaint(strokePaint);
    g.drawLine((int) x, (int) y, (int) x, (int) y);
  }

  public void line(float x1, float y1, float x2, float y2) {
    if (strokePaint == null)
      return;
    g.setPaint(strokePaint);
    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
  }

  public void circle(float x, float y, float r) {
    if (fillPaint != null) {
      g.setPaint(fillPaint);
      g.fillArc((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2), 0, 360);
    }
    if (strokePaint != null) {
      g.setPaint(strokePaint);
      g.drawArc((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2), 0, 360);
    }
  }

  public void rect(float x, float y, float w, float h) {
    if (fillPaint != null) {
      g.setPaint(fillPaint);
      g.fillRect((int) x, (int) y, (int) w, (int) h);
    }
    if (strokePaint != null) {
      g.setPaint(strokePaint);
      g.drawRect((int) x, (int) y, (int) w, (int) h);
    }
  }

  public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
    Polygon triangle = new Polygon(new int[] {(int) x1, (int) x2, (int) x3}, new int[] {(int) y1, (int) y2, (int) y3},
        3);
    if (fillPaint != null) {
      g.setPaint(fillPaint);
      g.fill(triangle);
    }
    if (strokePaint != null) {
      g.setPaint(strokePaint);
      g.draw(triangle);
    }
  }

  public void polygon(float... vertices) {
    int[] x = new int[vertices.length / 2];
    int[] y = new int[vertices.length / 2];
    for (int i = 0; i < vertices.length / 2; i++) {
      x[i] = (int) vertices[i * 2];
      y[i] = (int) vertices[i * 2 + 1];
    }
    Polygon polygon = new Polygon(x, y, x.length);
    if (fillPaint != null) {
      g.setPaint(fillPaint);
      g.fill(polygon);
    }
    if (strokePaint != null) {
      g.setPaint(strokePaint);
      g.draw(polygon);
    }
  }

  public int stringWidth(String string, float size) {
    Font font = baseFont.deriveFont(size);
    return g.getFontMetrics(font).stringWidth(string);
  }

  public void text(String txt, float x, float y, float size, TextAlign align) {
    Font font = baseFont.deriveFont(size);
    g.setFont(font);
    float textWidth = g.getFontMetrics().stringWidth(txt);
    float realX = x - align.horiz * textWidth / 2;
    float realY = y + align.vert * size * 72 / 96 / 2;
    if (fillPaint != null) {
      g.setPaint(fillPaint);
      g.drawString(txt, realX, realY);
    }
  }
}
