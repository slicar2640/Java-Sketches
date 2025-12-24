package sketch.edit.editpanel;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.function.Consumer;

import sketch.util.DrawUtils;
import sketch.util.DrawUtils.WeightedStroke;
import sketch.util.MathUtils;

public class EditMultiSlider extends EditInput implements MouseMotionListener {
  private Paint trackPaint = Color.DARK_GRAY;
  private WeightedStroke trackStroke = new WeightedStroke(Color.BLACK, 2);
  private Paint handlePaint = Color.LIGHT_GRAY;
  private WeightedStroke handleStroke = new WeightedStroke(Color.BLACK, 2);

  private float min, max;
  private float[] values;
  private int currentHandle = -1;

  private float x, y;
  private float trackWidth, trackHeight;
  private float handleWidth, handleHeight;

  private Consumer<float[]> controlling;

  public EditMultiSlider(float min, float max, float[] values, EditPanel editPanel) {
    this.min = min;
    this.max = max;
    this.values = values;
    this.editPanel = editPanel;
  }

  public EditMultiSlider setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public EditMultiSlider setSize(float w, float h) {
    trackWidth = w;
    trackHeight = h;
    return this;
  }

  public EditMultiSlider setHandleSize(float w, float h) {
    handleWidth = w;
    handleHeight = h;
    return this;
  }

  public EditMultiSlider styleTrack(Paint bgPaint, WeightedStroke bgStroke) {
    trackPaint = bgPaint;
    trackStroke = bgStroke;
    return this;
  }

  public EditMultiSlider styleHandle(Paint handlePaint, WeightedStroke handleStroke) {
    this.handlePaint = handlePaint;
    this.handleStroke = handleStroke;
    return this;
  }

  public EditMultiSlider setControlling(Consumer<float[]> controlling) {
    this.controlling = controlling;
    return this;
  }

  public Rectangle2D.Float getHandleBounds(int i) {
    float hx = MathUtils.map(values[i], min, max, x, x + trackWidth) - handleWidth / 2;
    float hy = y + trackHeight / 2 - handleHeight / 2;
    return new Rectangle2D.Float(hx, hy, handleWidth, handleHeight);
  }

  public Rectangle2D.Float getTrackBounds() {
    return new Rectangle2D.Float(x, y, trackWidth, trackHeight);
  }

  public Rectangle2D.Float getBounds() {
    Rectangle2D.Float handle1Bounds = getHandleBounds(0);
    Rectangle2D.Float handle2Bounds = getHandleBounds(values.length - 1);
    float minX = Math.min(x, handle1Bounds.x);
    float minY = Math.min(y, handle1Bounds.y);
    float maxX = Math.max(x + trackWidth, handle2Bounds.x + handle2Bounds.width);
    float maxY = Math.max(y + trackHeight, handle2Bounds.y + handle2Bounds.height);
    return new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY);
  }

  public boolean pointHits(int handleIndex, Point2D p) {
    return getHandleBounds(handleIndex).contains(p);
  }

  private void dragTo(float mx) {
    float realX = Math.clamp(mx, x, x + trackWidth);
    values[currentHandle] = MathUtils.map(realX, x, x + trackWidth, min, max);
    float currentValue = values[currentHandle];
    Arrays.sort(values);
    controlValue();
    currentHandle = Arrays.binarySearch(values, currentValue);
    editPanel.updateVisuals();
  }

  public void setValue(float value) {
    values[currentHandle] = value;
  }

  public void controlValue() {
    if (controlling != null) {
      controlling.accept(values);
    }
  }

  public void show(DrawUtils drawUtils) {
    drawUtils.stroke(trackStroke);
    drawUtils.fill(trackPaint);
    drawUtils.rect(x, y, trackWidth, trackHeight);
    drawUtils.stroke(handleStroke);
    drawUtils.fill(handlePaint);
    for (int i = 0; i < values.length; i++) {
      Rectangle2D.Float handleBounds = getHandleBounds(i);
      drawUtils.rect(handleBounds.x, handleBounds.y, handleBounds.width, handleBounds.height);
    }
  }

  public void mousePressed(MouseEvent e) {
    for (int i = 0; i < values.length; i++) {
      if (pointHits(i, e.getPoint())) {
        currentHandle = i;
        dragTo(e.getX());
        editPanel.addMouseMotionListener(this);
        break;
      }
    }
  }

  public void mouseDragged(MouseEvent e) {
    dragTo(e.getX());
  }

  public void mouseReleased(MouseEvent e) {
    editPanel.removeMouseMotionListener(this);
    currentHandle = -1;
  }

  public void mouseMoved(MouseEvent e) {
  }
}
