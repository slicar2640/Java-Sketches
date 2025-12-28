package sketch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import sketch.environment.HitColor;
import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.util.DrawUtils;
import sketch.util.Vector;
import sketch.util.DrawUtils.TextAlign;

public class WindowManager extends Canvas implements Runnable {
  public Sketch sketch;
  private JFrame frame;
  private boolean running = false;
  private Thread animationThread;
  private int targetFPS = 120;
  private Graphics2D graphics;
  private DrawUtils drawUtils;

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
    drawUtils = new DrawUtils(this);
    scene = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt) scene.getRaster().getDataBuffer()).getData();
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
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

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
          drawUtils.setGraphics(graphics);
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
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        elapsed = System.nanoTime() - last;
      }
      frameRate = (float) (1e9 / elapsed);
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      last = System.nanoTime();
    }
    bs.dispose();
    frame.dispose();
  }

  public JFrame getFrame() {
    return frame;
  }

  public DrawUtils getDrawUtils() {
    return drawUtils;
  }

  private int px = 0, py = 0;
  private BufferedImage scene;
  private int[] pixels;
  Ray ray = new Ray(new Vector(0, 0), new Vector(1, 0), 0);
  private int samples = 100;
  private int pixelsPerFrame = 1200;
  private int numFrames = 0;
  private boolean justEdited = false;

  private void draw() {
    if (sketch.getState() == Sketch.State.EDIT) {
      drawUtils.background(Color.BLACK);
    } else {
      drawUtils.getGraphics().drawImage(scene, 0, 0, null);
    }

    if (sketch.getState() == Sketch.State.DEBUG) {
      sketch.environment.showMaterials(drawUtils);
      drawUtils.noStroke();
      drawUtils.fill(Color.orange);
      drawUtils.rect(10, 10, 50, 20);
      drawUtils.fill(Color.WHITE);
      drawUtils.text(Integer.toString((int) frameRate), 10, 10, 20, TextAlign.LEFT_TOP);
    }

    sketch.environment.show(drawUtils);

    if (sketch.getState() == Sketch.State.EDIT) {
      sketch.editManager.show(drawUtils);
      justEdited = true;
    } else {
      if (justEdited) {
        justEdited = false;
        px = 0;
        py = 0;
        numFrames = 0;
        for (int i = 0; i < width * height; i++) {
          pixels[i] = 0xFF000000;
        }
      }
      drawUtils.stroke(Color.white);
      drawUtils.strokeWeight(2);
      drawUtils.line(0, py, 20, py);

      for (int pix = 0; pix < pixelsPerFrame; pix++) {
        ray.origin.set(px, py);
        HitColor colorSum = new HitColor(0, 0, 0, 1);
        float angleOffset = (float) (Math.random() * Math.PI * 2);
        for (int i = 0; i < samples; i++) {
          float angle = (float) i / samples * 2 * (float) Math.PI;
          ray.direction.set(Vector.fromAngle(angle + angleOffset));
          Intersection inter = sketch.environment.intersect(ray);
          if (inter != null) {
            colorSum.add(inter.color);
          }
        }
        colorSum.divide(samples);
        HitColor oldPixel = new HitColor(new Color(pixels[px + py * width]));
        colorSum.add(oldPixel.multiply(numFrames)).divide(numFrames + 1);
        pixels[px + py * width] = colorSum.toColor().getRGB();

        px++;
        if (px >= width) {
          px = 0;
          py++;
        }
        if (py >= height) {
          py = 0;
          numFrames++;
        }
      }
    }
  }

  public int getFrameX() {
    return frame.getX();
  }

  public int getFrameY() {
    return frame.getY();
  }
}
