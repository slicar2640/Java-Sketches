package sketch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class WindowManager extends Canvas implements Runnable {
  public Sketch sketch;
  private JFrame frame;
  private boolean running = false;
  private Thread animationThread;
  private int targetFPS = 120;
  public Graphics2D graphics;
  public int width, height;
  public int frameCount;
  public float frameRate;

  public WindowManager(Sketch sketch, int width, int height) {
    this.sketch = sketch;
    this.width = width;
    this.height = height;
    setPreferredSize(new Dimension(width, height));
    frame = new JFrame("Lights");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setResizable(false);
    setIgnoreRepaint(true);
    createBufferStrategy(3);
    setFocusable(true);
    requestFocus();
  }

  public synchronized void start() {
    if (running)
      return;
    running = true;

    animationThread = new Thread(this, "AnimationThread");
    animationThread.start();
  }

  public synchronized void stop() {
    running = false;
    try {
      if (animationThread != null)
        animationThread.join();
    } catch (InterruptedException ignored) {
    }
  }

  @Override
  public void run() {
    BufferStrategy bs = getBufferStrategy();
    graphics = (Graphics2D) bs.getDrawGraphics();

    long last = System.nanoTime();
    long frameTime = 1_000_000_000L / targetFPS;

    while (running) {
      do {
        do {
          graphics = (Graphics2D) bs.getDrawGraphics();
          graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          draw();
        } while (bs.contentsRestored());
        bs.show();
        Toolkit.getDefaultToolkit().sync(); // prevent tearing on some systems
      } while (bs.contentsLost());
      frameCount++;
      long elapsed = System.nanoTime() - last;
      while (elapsed < frameTime) {
        try {
          Thread.sleep(0, 10);
        } catch (InterruptedException ignored) {
        }
        elapsed = System.nanoTime() - last;
      }
      frameRate = (float) (1e9 / elapsed);
      try {
        Thread.sleep(1);
      } catch (InterruptedException ignored) {
      }
      last = System.nanoTime();
    }
    bs.dispose();
    frame.dispose();
  }

  private void draw() {
    graphics.setColor(getBackground());
    graphics.fillRect(0, 0, width, height);
    graphics.setColor(new Color(255, 0, 0));
    graphics.fillRect(0, frameCount / 10, 100, 100);
    graphics.drawString(Float.toString(frameRate), 200, 200);
  }
}
