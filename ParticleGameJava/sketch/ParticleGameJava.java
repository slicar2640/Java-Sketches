package sketch;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import sketch.DrawManager.TextAlign;
import sketch.construct.ConstructBuilder;
import sketch.menu.MenuManager;
import sketch.mouse.*;
import sketch.stick.Stick;

public class ParticleGameJava extends Canvas
    implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
  private JFrame frame;
  private boolean running = false;
  private Thread animationThread;
  private int targetFPS = 120;
  public Graphics2D graphics;

  private DrawManager dm;

  public int frameCount = 0;
  public float frameRate = targetFPS;

  public volatile int mouseX, mouseY;
  public volatile boolean mouseIsPressed = false;
  public volatile MouseButton mouseButton;

  public volatile boolean shiftPressed = false, ctrlPressed = false, altPressed = false;
  public volatile boolean keyPressed = false;
  public volatile char key;

  public int width = 600, height = 600;

  // sketch variables
  public ParticleManager particleManager;
  public MouseInteractionManager mouseManager;
  public ConstructBuilder constructBuilder;
  public MenuManager menuManager;
  PushInteraction pushInteraction;
  DeleteInteraction deleteInteraction;
  AddStickInteraction addStickInteraction;
  int fpsAverageNum = 10;
  float[] frameRates = new float[fpsAverageNum];

  public ParticleGameJava() {
    setPreferredSize(new Dimension(width, height));
    frame = new JFrame("Particle Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setResizable(false);
    setIgnoreRepaint(true);
    createBufferStrategy(3);
    dm = new DrawManager(this);
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    addMouseWheelListener(this);
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
    setup();

    while (running) {
      draw(bs);
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

  private void setup() {
    particleManager = new ParticleManager(width, height, 10, this);
    constructBuilder = new ConstructBuilder(particleManager);
    pushInteraction = new PushInteraction(5, 100, this);
    deleteInteraction = new DeleteInteraction(50, this);
    addStickInteraction = new AddStickInteraction(Stick.class, 100, 1, this);
    mouseManager = new MouseInteractionManager(pushInteraction, this);
    menuManager = new MenuManager(this);

    // for (int i = 0; i < 1000; i++) {
    // particleManager.addParticle(MathUtils.random(width), MathUtils.random(height), 1);
    // }

    // RailStick rail1 = particleManager
    // .addStick(new RailStick(new Vector(100, 100), new Vector(100, 200), particleManager));
    // RailStick rail2 = particleManager
    // .addStick(new RailStick(new Vector(200, 100), new Vector(200, 200), particleManager));
    // RailStick rail3 = particleManager
    // .addStick(new RailStick(new Vector(100, 150), new Vector(200, 150), particleManager));
    // rail1.addRider(rail3.p1);
    // rail2.addRider(rail3.p2);
    // rail3.addRider(particleManager.addParticle(150, 150, 1));

    // rail1.p1.isStatic = true;
    // rail1.p2.isStatic = true;
    // rail2.p1.isStatic = true;
    // rail2.p2.isStatic = true;
  }

  private synchronized void draw(BufferStrategy bs) {
    do {
      do {
        graphics = (Graphics2D) bs.getDrawGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        dm.setGraphics(graphics);

        dm.background(0);
        if (menuManager.active) {
          menuManager.show(dm);
        } else {
          if (!particleManager.paused) {
            particleManager.update(1, 30);
            particleManager.propagateSignals();
          }
          if (mouseIsPressed) {
            if (!menuManager.justExitedMenu) {
              mouseManager.mouseDown();
            }
          }
          particleManager.show(dm);

          frameRates[frameCount % fpsAverageNum] = frameRate;
          if (keyPressed) {
            if (key == 'd') {
              dm.fill(255, 150);
              dm.noStroke();
              dm.text(particleManager.numParticles + ", " + particleManager.numSticks(), 10, 10, 20,
                  TextAlign.LEFT_TOP);
              float avgFPS = 0;
              for (float f : frameRates) {
                avgFPS += f;
              }
              avgFPS /= fpsAverageNum;
              dm.text(Float.toString((int) (avgFPS * 100) / 100f), 10, 35, 20, TextAlign.LEFT_TOP);
              // mouseManager.showDebug();
            }
          }

          mouseManager.show(dm);
        }

        graphics.dispose();
      } while (bs.contentsRestored());
      bs.show();
      Toolkit.getDefaultToolkit().sync(); // prevent tearing on some systems
    } while (bs.contentsLost());
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    mouseManager.updateScale(-e.getWheelRotation());
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
    mouseIsPressed = true;
    mouseButton = MouseButton.fromEvent(e);
    if (menuManager.active) {
      menuManager.click();
    } else if (!menuManager.justExitedMenu) {
      mouseManager.mouseClick();
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mouseIsPressed = false;
    menuManager.justExitedMenu = false;
    mouseButton = MouseButton.fromEvent(e);
    if (!menuManager.active) {
      mouseManager.mouseUp();
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SHIFT)
      shiftPressed = true;
    if (e.getKeyCode() == KeyEvent.VK_CONTROL)
      ctrlPressed = true;
    if (e.getKeyCode() == KeyEvent.VK_ALT)
      altPressed = true;
    keyPressed = true;
    key = e.getKeyChar();
    if (!menuManager.active) {
      switch (e.getKeyChar()) {
      case '1':
        mouseManager.currentInteraction = pushInteraction;
        break;
      case '2':
        mouseManager.currentInteraction = deleteInteraction;
        break;
      case '3':
        menuManager.open("StartOfAdd");
        break;
      case '4':
        mouseManager.currentInteraction = addStickInteraction;
        addStickInteraction.p1 = null;
        break;
      case '$':
        menuManager.open("AddStick");
        break;
      case '5':
        mouseManager.currentInteraction = new GrabInteraction(0.03f, this);
        break;
      case '6':
        mouseManager.currentInteraction = new IgniteInteraction(this);
        break;
      case '7':
        mouseManager.currentInteraction = new InspectInteraction(this);
        break;
      case '-':
        mouseManager.updateCount(-1);
        break;
      case '=':
        mouseManager.updateCount(1);
        break;
      case ' ':
        particleManager.paused = !particleManager.paused;
        break;
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SHIFT)
      shiftPressed = false;
    if (e.getKeyCode() == KeyEvent.VK_CONTROL)
      ctrlPressed = false;
    if (e.getKeyCode() == KeyEvent.VK_ALT)
      altPressed = false;

    keyPressed = false;
    key = e.getKeyChar();
  }

  public static void main(String[] args) {
    ParticleGameJava sketch = new ParticleGameJava();
    sketch.start();
  }
}
