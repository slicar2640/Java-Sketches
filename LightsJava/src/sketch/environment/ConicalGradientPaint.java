package sketch.environment;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sketch.util.MathUtils;

//Based on answer from user MadProgrammer on StackOverflow: https://stackoverflow.com/questions/14422025/draw-an-arc-and-gradient-it

public class ConicalGradientPaint implements Paint {

  private final Point2D CENTER;
  private final float ANGLE1, ANGLE2;
  private final Color COLOR1, COLOR2;

  public ConicalGradientPaint(Point2D center, float angle1, Color color1, float angle2, Color color2) {
    CENTER = center;
    if (angle1 < angle2) {
      ANGLE1 = angle1;
      COLOR1 = color1;
      ANGLE2 = angle2;
      COLOR2 = color2;
    } else {
      ANGLE1 = angle2;
      COLOR1 = color2;
      ANGLE2 = angle1;
      COLOR2 = color1;
    }
  }

  @Override
  public PaintContext createContext(final ColorModel COLOR_MODEL, final Rectangle DEVICE_BOUNDS,
      final Rectangle2D USER_BOUNDS, final AffineTransform TRANSFORM, final RenderingHints HINTS) {
    final Point2D TRANSFORMED_CENTER = TRANSFORM.transform(CENTER, null);
    return new ConicalGradientPaintContext(TRANSFORMED_CENTER);
  }

  @Override
  public int getTransparency() {
    return Transparency.TRANSLUCENT;
  }

  private final class ConicalGradientPaintContext implements PaintContext {

    final private Point2D CENTER;

    public ConicalGradientPaintContext(final Point2D CENTER) {
      this.CENTER = new Point2D.Double(CENTER.getX(), CENTER.getY());
    }

    @Override
    public void dispose() {
    }

    @Override
    public ColorModel getColorModel() {
      return ColorModel.getRGBdefault();
    }

    @Override
    public Raster getRaster(final int X, final int Y, final int TILE_WIDTH, final int TILE_HEIGHT) {
      final double ROTATION_CENTER_X = -X + CENTER.getX();
      final double ROTATION_CENTER_Y = -Y + CENTER.getY();

      // Create raster for given colormodel
      final WritableRaster RASTER = getColorModel().createCompatibleWritableRaster(TILE_WIDTH, TILE_HEIGHT);

      // Create data array with place for red, green, blue and alpha values
      int[] data = new int[(TILE_WIDTH * TILE_HEIGHT * 4)];

      double dx;
      double dy;
      double angle;
      double lerpFactor;
      float[] startColor = COLOR1.getComponents(null);
      float[] endColor = COLOR2.getComponents(null);
      float[] lerpColor = new float[] {0, 0, 0, 0};

      for (int py = 0; py < TILE_HEIGHT; py++) {
        for (int px = 0; px < TILE_WIDTH; px++) {

          // Calculate the distance between the current position and the rotation angle
          dx = px - ROTATION_CENTER_X;
          dy = py - ROTATION_CENTER_Y;
          angle = (Math.toDegrees(Math.atan2(dy, dx)) + 360) % 360;
          if (ANGLE2 >= 360 && angle < ANGLE2 - 360) {
            angle += 360;
          }
          lerpFactor = Math.clamp((angle - ANGLE1) / (ANGLE2 - ANGLE1), 0, 1);

          MathUtils.lerp(startColor, endColor, (float) lerpFactor, lerpColor);

          // Fill data array with calculated color values
          final int BASE = (py * TILE_WIDTH + px) * 4;
          data[BASE + 0] = (int) (lerpColor[0] * 255);
          data[BASE + 1] = (int) (lerpColor[1] * 255);
          data[BASE + 2] = (int) (lerpColor[2] * 255);
          data[BASE + 3] = (int) (lerpColor[3] * 255);
        }
      }

      // Fill the raster with the data
      RASTER.setPixels(0, 0, TILE_WIDTH, TILE_HEIGHT, data);

      return RASTER;
    }
  }
}
