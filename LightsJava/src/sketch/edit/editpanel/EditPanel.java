package sketch.edit.editpanel;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JFrame;

import sketch.Sketch;
import sketch.edit.EditManager;
import sketch.util.DrawUtils;

public class EditPanel extends Canvas implements WindowListener, KeyListener {
  private JDialog dialog;
  public Sketch sketch;
  private EditManager editManager;
  private Graphics2D graphics;
  private DrawUtils drawUtils;
  public int width, height;

  public boolean shiftPressed = false;

  private ArrayList<EditInput<?>> inputs = new ArrayList<>();
  private float nextAvailableY = 0;

  public EditPanel(Sketch sketch, EditManager editManager, int width, int height) {
    this.sketch = sketch;
    this.editManager = editManager;
    this.width = width;
    this.height = height;

    setPreferredSize(new Dimension(width, height));
    setFocusable(true);

    JFrame windowManagerFrame = sketch.windowManager.getFrame();
    dialog = new JDialog(windowManagerFrame, "Edit Object");
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.add(this);
    dialog.pack();

    dialog.setLocation(windowManagerFrame.getX() + windowManagerFrame.getWidth(), windowManagerFrame.getY() - 32);

    dialog.setResizable(false);
    dialog.setVisible(true);

    dialog.addWindowListener(this);
    addKeyListener(this);

  }

  @Override
  public void addNotify() {
    setIgnoreRepaint(true);
    super.addNotify();
    createBufferStrategy(2);
    drawUtils = new DrawUtils(this, sketch);
    graphics = (Graphics2D) getBufferStrategy().getDrawGraphics();
    drawUtils.setGraphics(graphics);
  }

  public JDialog getDialog() {
    return dialog;
  }

  public DrawUtils getDrawUtils() {
    return drawUtils;
  }

  public void clearInputs() {
    inputs.clear();
    nextAvailableY = 0;
    Arrays.asList(getMouseListeners()).forEach(this::removeMouseListener);
    Arrays.asList(getMouseMotionListeners()).forEach(this::removeMouseMotionListener);
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
    drawUtils.background(Color.GRAY);
    for (EditInput<?> input : inputs) {
      input.show(drawUtils);
    }
  }

  public <T extends EditInput<?>> T addInput(T input) {
    inputs.add(input);
    addMouseListener(input);
    nextAvailableY = Math.max(nextAvailableY, (float) input.getBounds().getMaxY());
    return input;
  }

  public float getNextAvailableY() {
    return nextAvailableY;
  }

  public void addBlankSpace(float blankSpace) {
    nextAvailableY += blankSpace;
  }

  public void dispose() {
    dialog.dispose();
  }

  @Override
  public void windowClosed(WindowEvent e) {
    editManager.editPanel = null;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
      shiftPressed = true;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
      shiftPressed = false;
    }
  }

  @Override
  public void windowOpened(WindowEvent e) {
  }

  @Override
  public void windowClosing(WindowEvent e) {
  }

  @Override
  public void windowIconified(WindowEvent e) {
  }

  @Override
  public void windowDeiconified(WindowEvent e) {
  }

  @Override
  public void windowActivated(WindowEvent e) {
  }

  @Override
  public void windowDeactivated(WindowEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }
}
