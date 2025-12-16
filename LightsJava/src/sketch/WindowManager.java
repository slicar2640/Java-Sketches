package sketch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

import sketch.util.DrawUtils;

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

  public SceneRenderer renderer;

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
    // scene = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  public synchronized void start() {
    if (running)
      return;
    running = true;

    renderer = new SceneRenderer(sketch, width, height, 1, 1);
    renderer.start();
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

  // private int py = 0;
  // private BufferedImage scene;
  // Ray ray = new Ray(new Vector(0, 0), new Vector(0, 0));
  // private int samples = 100;

  private void draw() {
    drawUtils.getGraphics().drawImage(renderer.getImage(), 0, 0, null);

    drawUtils.stroke(Color.white);
    drawUtils.strokeWeight(4);
    drawUtils.point(10, renderer.progress[0] * height / 2);
    // drawUtils.point(width - 10, renderer.progress[1] * height / 2);
    // drawUtils.point(10, (renderer.progress[2] + 1) * height / 2);
    // drawUtils.point(width - 10, (renderer.progress[3] + 1) * height / 2);

    if (sketch.showMaterials) {
      sketch.environment.showMaterials(drawUtils);
    }
    sketch.environment.show(drawUtils);

    drawUtils.stroke(Color.white);
    drawUtils.strokeWeight(2);
    // drawUtils.line(0, py, 20, py);

    // int[] row = new int[width * 3];
    // for (int px = 0; px < width; px++) {
    // ray.origin.set(px, py);
    // row[px * 3 + 0] = 0;
    // row[px * 3 + 1] = 0;
    // row[px * 3 + 2] = 0;
    // float angleOffset = (float) (Math.random() * Math.PI * 2);
    // for (int i = 0; i < samples; i++) {
    // float angle = (float) i / samples * 2 * (float) Math.PI;
    // ray.direction.set(Vector.fromAngle(angle + angleOffset));
    // Intersection inter = sketch.environment.intersect(ray);
    // if (inter != null) {
    // row[px * 3 + 0] += (int) (inter.color[0]);
    // row[px * 3 + 1] += (int) (inter.color[1]);
    // row[px * 3 + 2] += (int) (inter.color[2]);
    // }
    // }
    // row[px * 3 + 0] /= samples;
    // row[px * 3 + 1] /= samples;
    // row[px * 3 + 2] /= samples;
    // }
    // scene.getRaster().setPixels(0, py, width, 1, row);

    // py++;
    // if (py >= height) {
    // py = 0;
    // }
  }
}
