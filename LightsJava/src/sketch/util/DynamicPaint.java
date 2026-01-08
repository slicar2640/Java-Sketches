package sketch.util;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.function.Supplier;

public class DynamicPaint implements Paint {
  private Supplier<Paint> supplier;

  public DynamicPaint(Supplier<Paint> supplier) {
    this.supplier = supplier;
  }

  @Override
  public int getTransparency() {
    return supplier.get().getTransparency();
  }

  @Override
  public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds,
      AffineTransform xform, RenderingHints hints) {
    return supplier.get().createContext(cm, deviceBounds, userBounds, xform, hints);
  }
}
