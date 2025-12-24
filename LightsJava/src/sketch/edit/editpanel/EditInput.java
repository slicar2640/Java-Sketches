package sketch.edit.editpanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import sketch.util.DrawUtils;

public abstract class EditInput implements MouseListener {
  protected EditPanel editPanel;

  public abstract Rectangle2D.Float getBounds();

  public abstract void controlValue();

  public abstract void show(DrawUtils drawUtils);

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }
}
