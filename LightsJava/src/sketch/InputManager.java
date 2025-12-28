package sketch;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import sketch.util.Vector;

public class InputManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
  public Sketch sketch;
  public Vector mouse = new Vector(0, 0);

  public InputManager(Sketch sketch) {
    this.sketch = sketch;
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {

  }

  @Override
  public void mouseDragged(MouseEvent e) {
    mouse.set(e.getX(), e.getY());
    if (sketch.getState() == Sketch.State.EDIT) {
      sketch.editManager.drag(e.getX(), e.getY());
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    mouse.set(e.getX(), e.getY());
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (sketch.getState() == Sketch.State.EDIT) {
      sketch.editManager.mousePressed(e);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (sketch.getState() == Sketch.State.EDIT) {
      sketch.editManager.deselectTool();
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyChar() == ' ') {
      sketch.stateChange(Sketch.State.DEBUG);
    } else if (e.getKeyChar() == 'E') {
      sketch.stateChange(Sketch.State.EDIT);
    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      sketch.editManager.tryToOpenEditPanel();
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

}
