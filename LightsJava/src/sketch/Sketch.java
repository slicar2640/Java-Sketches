package sketch;

import sketch.environment.Environment;

public class Sketch {
  public WindowManager windowManager;
  public InputManager inputManager;
  public Environment environment;
  public boolean showMaterials = false;

  public void init() {
    windowManager = new WindowManager(this, 600, 600);
    inputManager = new InputManager(this);
    windowManager.addKeyListener(inputManager);
    windowManager.addMouseListener(inputManager);
    windowManager.addMouseMotionListener(inputManager);
    windowManager.addMouseWheelListener(inputManager);

    environment = new Environment(this);
    for (int i = 0; i <= 10; i++) {
      environment.addRandomObject();
    }
    windowManager.start();
  }

  public static void main(String[] args) {
    Sketch sketch = new Sketch();
    sketch.init();
  }
}
