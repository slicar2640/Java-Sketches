package sketch.edit.editpanel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import sketch.util.DrawUtils;
import sketch.util.DrawUtils.WeightedStroke;
import sketch.util.MathUtils;

public class EditMultiSlider extends EditInput<ArrayList<Float>> implements MouseMotionListener {
  private Paint trackPaint = Color.DARK_GRAY;
  private WeightedStroke trackStroke = new WeightedStroke(Color.BLACK, 2);
  private Paint handlePaint = Color.LIGHT_GRAY;
  private WeightedStroke handleStroke = new WeightedStroke(Color.BLACK, 2);

  private float min, max;
  private ArrayList<Float> values;
  private int currentHandle = -1;

  private float x, y;
  private float trackWidth, trackHeight;
  private float handleWidth, handleHeight;

  private Consumer<Float> addControl;
  private Consumer<Integer> removeControl;
  private ScheduledExecutorService cursorSetter;

  public EditMultiSlider(float min, float max, ArrayList<Float> values, EditPanel editPanel) {
    this.min = min;
    this.max = max;
    this.values = values;
    this.editPanel = editPanel;

    cursorSetter = Executors.newScheduledThreadPool(1);
    cursorSetter.scheduleAtFixedRate(this::changeCursor, 0, 10, TimeUnit.MILLISECONDS);
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

  public EditMultiSlider setControlling(Consumer<ArrayList<Float>> controlling) {
    this.controlling = controlling;
    return this;
  }

  public EditMultiSlider setAddControl(Consumer<Float> addControl) {
    this.addControl = addControl;
    return this;
  }

  public EditMultiSlider setRemoveControl(Consumer<Integer> removeControl) {
    this.removeControl = removeControl;
    return this;
  }

  private Rectangle2D.Float getHandleBounds(int i) {
    float hx = MathUtils.map(values.get(i), min, max, x, x + trackWidth) - handleWidth / 2;
    float hy = y + trackHeight / 2 - handleHeight / 2;
    return new Rectangle2D.Float(hx, hy, handleWidth, handleHeight);
  }

  private boolean handleContainsPoint(int handleIndex, Point2D p) {
    return getHandleBounds(handleIndex).contains(p);
  }

  private Rectangle2D.Float getTrackBounds() {
    return new Rectangle2D.Float(x, y, trackWidth, trackHeight);
  }

  @Override
  public Rectangle2D.Float getBounds() {
    Rectangle2D.Float handle1Bounds = getHandleBounds(0);
    Rectangle2D.Float handle2Bounds = getHandleBounds(values.size() - 1);
    float minX = Math.min(x, handle1Bounds.x);
    float minY = Math.min(y, handle1Bounds.y);
    float maxX = Math.max(x + trackWidth, handle2Bounds.x + handle2Bounds.width);
    float maxY = Math.max(y + trackHeight, handle2Bounds.y + handle2Bounds.height);
    return new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY);
  }

  private void dragTo(float mx) {
    values.set(currentHandle, MathUtils.clampMap(mx, x, x + trackWidth, min, max));
    sortValues();
    controlValue();
    editPanel.updateVisuals();
  }

  private void sortValues() {
    for (int i = 0; i < currentHandle; i++) {
      if (values.get(currentHandle) < values.get(i)) {
        float val = values.remove(currentHandle);
        currentHandle = i;
        values.add(currentHandle, val);
        return;
      }
    }
    for (int i = values.size() - 1; i > currentHandle; i--) {
      if (values.get(currentHandle) > values.get(i)) {
        float val = values.remove(currentHandle);
        currentHandle = i;
        values.add(currentHandle, val);
        return;
      }
    }
  }

  public void setValue(float value) {
    values.set(currentHandle, value);
    editPanel.updateVisuals();
  }

  public void addValue(float value) {
    boolean added = false;
    for (int i = values.size() - 1; i >= 0; i--) {
      if (value > values.get(i)) {
        values.add(i + 1, value);
        added = true;
        break;
      }
    }
    if (!added) {
      values.add(0, value);
    }
    addControl.accept(value);
    editPanel.updateVisuals();
  }

  public void removeValue(int index) {
    values.remove(index);
    if (removeControl != null) {
      removeControl.accept(index);
    }
    editPanel.updateVisuals();
  }

  @Override
  public void controlValue() {
    if (controlling != null) {
      controlling.accept(values);
    }
  }

  private void changeCursor() {
    if (currentHandle != -1 || !editPanel.shiftPressed) {
      editPanel.getDrawUtils().setCursor(Cursor.DEFAULT_CURSOR);
      return;
    }
    boolean hoveringHandle = false;
    Point mouse = editPanel.getMousePosition();
    if (mouse != null) {
      for (int i = 0; i < values.size(); i++) {
        if (handleContainsPoint(i, mouse)) {
          hoveringHandle = true;
        }
      }
    }
    if (hoveringHandle) {
      editPanel.getDrawUtils().setCursor("delete-hand");
    } else if (mouse != null && getTrackBounds().contains(mouse)) {
      editPanel.getDrawUtils().setCursor("add-hand");
    } else {
      editPanel.getDrawUtils().setCursor(Cursor.DEFAULT_CURSOR);
    }
  }

  @Override
  public void show(DrawUtils drawUtils) {
    drawUtils.stroke(trackStroke);
    drawUtils.fill(trackPaint);
    drawUtils.rect(x, y, trackWidth, trackHeight);
    drawUtils.stroke(handleStroke);
    drawUtils.fill(handlePaint);
    for (int i = 0; i < values.size(); i++) {
      Rectangle2D.Float handleBounds = getHandleBounds(i);
      drawUtils.rect(handleBounds.x, handleBounds.y, handleBounds.width, handleBounds.height);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.isShiftDown()) {
      for (int i = 0; i < values.size(); i++) {
        if (handleContainsPoint(i, e.getPoint())) {
          if (values.size() > 1) {
            removeValue(i);
          }
          return;
        }
      }
      if (getTrackBounds().contains(e.getPoint())) {
        addValue(MathUtils.clampMap(e.getX(), x, x + trackWidth, min, max));
      }
    } else {
      for (int i = 0; i < values.size(); i++) {
        if (handleContainsPoint(i, e.getPoint())) {
          currentHandle = i;
          dragTo(e.getX());
          editPanel.addMouseMotionListener(this);
          break;
        }
      }
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    dragTo(e.getX());
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    editPanel.removeMouseMotionListener(this);
    currentHandle = -1;
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }
}
