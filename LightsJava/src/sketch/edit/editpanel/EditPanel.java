package sketch.edit.editpanel;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;

import sketch.Sketch;
import sketch.edit.EditManager;
import sketch.util.DrawUtils;

public class EditPanel extends Canvas implements WindowListener {
  private JDialog dialog;
  public Sketch sketch;
  private EditManager editManager;
  private Graphics2D graphics;
  private DrawUtils drawUtils;
  private ArrayList<EditInput> inputs = new ArrayList<>();
  private float nextAvailableY = 0;

  public EditPanel(Sketch sketch, EditManager editManager, int width, int height) {
    this.sketch = sketch;
    this.editManager = editManager;
    setPreferredSize(new Dimension(width, height));
    JFrame windowManagerFrame = sketch.windowManager.getFrame();
    dialog = new JDialog(windowManagerFrame, "Edit Object");
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.add(this);
    dialog.pack();
    dialog.setVisible(true);
    dialog.setResizable(false);
    dialog.setLocation(windowManagerFrame.getX() + windowManagerFrame.getWidth(), windowManagerFrame.getY() - 32);
    setIgnoreRepaint(true);
    createBufferStrategy(2);
    dialog.addWindowListener(this);
    setFocusable(true);
    requestFocus();
    drawUtils = new DrawUtils(this);
  }

  public JDialog getDialog() {
    return dialog;
  }

  public void updateVisuals() {
    BufferStrategy bs = getBufferStrategy();
    graphics = (Graphics2D) bs.getDrawGraphics();
    do {
      do {
        graphics = (Graphics2D) bs.getDrawGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawUtils.setGraphics(graphics);
        draw();
      } while (bs.contentsRestored());
      bs.show();
      Toolkit.getDefaultToolkit().sync(); // prevent tearing on some systems
    } while (bs.contentsLost());
  }

  private void draw() {
    drawUtils.background(Color.PINK);
    for (EditInput input : inputs) {
      input.show(drawUtils);
    }
  }

  public <T extends EditInput> T addInput(T input) {
    inputs.add(input);
    addMouseListener(input);
    nextAvailableY = Math.max(nextAvailableY, (float) input.getBounds().getMaxY());
    return input;
  }

  public void dispose() {
    dialog.dispose();
  }

  public void windowOpened(WindowEvent e) {
  }

  public void windowClosing(WindowEvent e) {
  }

  public void windowClosed(WindowEvent e) {
    editManager.editPanel = null;
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }

  public float getNextAvailableY() {
    return nextAvailableY;
  }

  public void addBlankSpace(float blankSpace) {
    nextAvailableY += blankSpace;
  }
}
