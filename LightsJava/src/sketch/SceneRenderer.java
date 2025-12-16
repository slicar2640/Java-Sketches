package sketch;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.snapshot.EnvironmentObjectSnapshot;
import sketch.environment.snapshot.EnvironmentSnapshot;
import sketch.util.DrawUtils;
import sketch.util.Vector;

public class SceneRenderer {
  public Sketch sketch;
  private int width, height;
  private volatile BufferedImage frontImage;
  private BufferedImage backImage;
  private int[] pixels;
  private EnvironmentSnapshot environment;
  private Phaser phaser;
  private ExecutorService controller = Executors.newSingleThreadExecutor();
  private int workerNum;
  private int radialSamples;
  public final float[] progress;

  public SceneRenderer(Sketch sketch, int width, int height, int workerNum, int samples) {
    this.sketch = sketch;
    this.width = width;
    this.height = height;
    frontImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    backImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt) backImage.getRaster().getDataBuffer()).getData();

    this.radialSamples = samples;
    this.workerNum = workerNum;
    progress = new float[workerNum];
  }

  public void start() {
    controller.submit(this::run);
  }

  private void run() {
    phaser = new Phaser(workerNum);
    ExecutorService pool = Executors.newFixedThreadPool(workerNum);
    for (int i = 0; i < workerNum; i++) {
      int workerId = i; // Variable passed into lambda must be effectively final
      pool.submit(() -> workerLoop(workerId));
    }
    while (!Thread.currentThread().isInterrupted()) {
      int phase = phaser.getPhase();
      phaser.awaitAdvance(phase); // wait for all workers

      swapImages();
    }
  }

  private void workerLoop(int id) {
    int x0 = (id % 2) * (width / 2);
    int y0 = (id / 2) * (height / 2);
    int x1 = x0 + width / 2;
    int y1 = y0 + height / 2;

    while (true) {
      renderTile(id, x0, y0, x1, y1);
      phaser.arriveAndAwaitAdvance();
    }
  }

  private void renderTile(int id, int x0, int y0, int x1, int y1) {
    for (int y = y0; y < y1; y++) {
      progress[id] = (y - y0) / (y1 - y0);
      for (int x = x0; x < x1; x++) {
        float[] sumColor = new float[] {0, 0, 0};
        for (int i = 0; i < radialSamples; i++) {
          float angle = (float) i / radialSamples * 2 * (float) Math.PI;
          Ray ray = new Ray(new Vector(x, y), Vector.fromAngle(angle));
          Color color = trace(ray);
          sumColor[0] += color.getRed();
          sumColor[1] += color.getGreen();
          sumColor[2] += color.getBlue();
        }
        sumColor[0] /= 255;
        sumColor[1] /= 255;
        sumColor[2] /= 255;

        int rgb = DrawUtils.rgbFromArray(sumColor).getRGB();
        // pixels[x + y * width] = rgb;
        backImage.setRGB(x, y, rgb);
      }
    }
  }

  private Color trace(Ray ray) {
    // if (depth > maxDepth) {
    // return Color.BLACK;
    // }
    Intersection intersection = intersect(ray);
    if (intersection == null) {
      return Color.BLACK;
    }
    return intersection.color;

    // MaterialSnapshot.MatType mat = intersection.objectSnapshot.material.matType;
    // if (mat == MaterialSnapshot.MatType.GLASS) {
    // Color through = trace(new Ray(intersection.position, ray.direction), depth +
    // 1, maxDepth);
    // return intersection.objectSnapshot.material.filter(through, intersection);
    // } else {
    // return intersection.color;
    // }
  }

  public Intersection intersect(Ray ray) {
    float minDist = Float.MAX_VALUE;
    Intersection intersection = null;
    for (EnvironmentObjectSnapshot obj : environment.objects) {
      return new Intersection(ray, null, null, minDist, new EnvironmentObjectSnapshot(null, null), Color.green);
      // Intersection objIntersection = obj.intersect(ray);
      // if (objIntersection == null)
      // continue;
      // float d = Vector.dist(ray.origin, objIntersection.position);
      // if (d < minDist) {
      // minDist = d;
      // intersection = objIntersection;
      // }
    }
    return intersection;
  }

  public void setSnapshot(EnvironmentSnapshot environment) {
    this.environment = environment;
  }

  public void swapImages() {
    BufferedImage tmp = frontImage;
    frontImage = backImage;
    backImage = tmp;
    pixels = ((DataBufferInt) backImage.getRaster().getDataBuffer()).getData();
  }

  public BufferedImage getImage() {
    return frontImage;
  }
}
