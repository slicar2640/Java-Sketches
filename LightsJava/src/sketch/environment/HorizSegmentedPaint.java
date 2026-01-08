package sketch.environment;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class HorizSegmentedPaint implements Paint {
  private float[] xValues;
  private Color[] colors;

  public HorizSegmentedPaint(float[] xValues, Color[] colors) {
    this.xValues = xValues;
    this.colors = colors;
  }

  @Override
  public int getTransparency() {
    return Transparency.TRANSLUCENT;
  }

  @Override
  public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds,
      AffineTransform xform, RenderingHints hints) {
    return new HorizSegmentedPaintContext();
  }

  private class HorizSegmentedPaintContext implements PaintContext {

    @Override
    public void dispose() {
    }

    @Override
    public ColorModel getColorModel() {
      return ColorModel.getRGBdefault();
    }

    @Override
    public Raster getRaster(int x, int y, int w, int h) {
      WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);
      int[] data = new int[(w * h * 4)];
      Color color = Color.BLACK;
      boolean colorChosen = false;
      float[] colorArr = new float[4];
      for (int px = 0; px < w; px++) {
        colorChosen = false;
        for (int i = 0; i < xValues.length; i++) {
          if (px + x < xValues[i]) {
            color = colors[i];
            colorChosen = true;
            break;
          }
          if (!colorChosen) {
            color = colors[colors.length - 1];
          }
        }
        for (int py = 0; py < h; py++) {
          color.getComponents(colorArr);
          int base = (py * w + px) * 4;
          data[base + 0] = (int) (colorArr[0] * 255);
          data[base + 1] = (int) (colorArr[1] * 255);
          data[base + 2] = (int) (colorArr[2] * 255);
          data[base + 3] = (int) (colorArr[3] * 255);
        }
      }
      raster.setPixels(0, 0, w, h, data);
      return raster;
    }
  }
}
