package sketch.edit.editpanel;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;

import sketch.util.DrawUtils;
import sketch.util.DrawUtils.WeightedStroke;
import sketch.util.MathUtils;

public class EditSlider extends EditInput implements MouseMotionListener {
  private Paint trackPaint = Color.DARK_GRAY;
  private WeightedStroke trackStroke = new WeightedStroke(Color.BLACK, 2);
  private Paint handlePaint = Color.LIGHT_GRAY;
  private WeightedStroke handleStroke = new WeightedStroke(Color.BLACK, 2);

  private float min, max;
  private float value;

  private float x, y;
  private float trackWidth, trackHeight;
  private float handleWidth, handleHeight;

  private Consumer<Float> controlling;

  public EditSlider(float min, float max, float value, EditPanel editPanel) {
    this.min = min;
    this.max = max;
    this.value = value;
    this.editPanel = editPanel;
  }

  public EditSlider setPosition(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public EditSlider setSize(float w, float h) {
    trackWidth = w;
    trackHeight = h;
    return this;
  }

  public EditSlider setHandleSize(float w, float h) {
    handleWidth = w;
    handleHeight = h;
    return this;
  }

  public EditSlider styleTrack(Paint bgPaint, WeightedStroke bgStroke) {
    trackPaint = bgPaint;
    trackStroke = bgStroke;
    return this;
  }

  public EditSlider styleHandle(Paint handlePaint, WeightedStroke handleStroke) {
    this.handlePaint = handlePaint;
    this.handleStroke = handleStroke;
    return this;
  }

  public EditSlider setControlling(Consumer<Float> controlling) {
    this.controlling = controlling;
    return this;
  }

  public Rectangle2D.Float getHandleBounds() {
    float hx = MathUtils.map(value, min, max, x, x + trackWidth) - handleWidth / 2;
    float hy = y + trackHeight / 2 - handleHeight / 2;
    return new Rectangle2D.Float(hx, hy, handleWidth, handleHeight);
  }

  public Rectangle2D.Float getTrackBounds() {
    return new Rectangle2D.Float(x, y, trackWidth, trackHeight);
  }

  public Rectangle2D.Float getBounds() {
    Rectangle2D.Float handleBounds = getHandleBounds();
    float minX = Math.min(x, handleBounds.x);
    float minY = Math.min(y, handleBounds.y);
    float maxX = Math.max(x + trackWidth, handleBounds.x + handleBounds.width);
    float maxY = Math.max(y + trackHeight, handleBounds.y + handleBounds.height);
    return new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY);
  }

  private void dragTo(float mx) {
    float realX = Math.clamp(mx, x, x + trackWidth);
    value = MathUtils.map(realX, x, x + trackWidth, min, max);
    controlValue();
    editPanel.updateVisuals();
  }

  public void setValue(float value) {
    this.value = value;
    editPanel.updateVisuals();
  }

  public void controlValue() {
    if (controlling != null) {
      controlling.accept(value);
    }
  }

  public void show(DrawUtils drawUtils) {
    drawUtils.stroke(trackStroke);
    drawUtils.fill(trackPaint);
    drawUtils.rect(x, y, trackWidth, trackHeight);
    drawUtils.stroke(handleStroke);
    drawUtils.fill(handlePaint);
    Rectangle2D.Float handleBounds = getHandleBounds();
    drawUtils.rect(handleBounds.x, handleBounds.y, handleBounds.width, handleBounds.height);
  }

  public void mousePressed(MouseEvent e) {
    if (getTrackBounds().contains(e.getPoint()) || getHandleBounds().contains(e.getPoint())) {
      dragTo(e.getX());
      editPanel.addMouseMotionListener(this);
    }
  }

  public void mouseDragged(MouseEvent e) {
    dragTo(e.getX());
  }

  public void mouseReleased(MouseEvent e) {
    editPanel.removeMouseMotionListener(this);
  }

  public void mouseMoved(MouseEvent e) {
  }
}
