package sketch.edit.editpanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;

import sketch.util.DrawUtils;

public abstract class EditInput<T> implements MouseListener {
  protected EditPanel editPanel;
  protected Consumer<T> controlling;

  public abstract Rectangle2D.Float getBounds();

  public abstract void controlValue();

  public abstract void show(DrawUtils drawUtils);

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }
}
