package sketch;

public class Sketch {
    WindowManager windowManager;
    InputManager inputManager;

    public void init() {
        windowManager = new WindowManager(this, 600, 600);
        inputManager = new InputManager(this);
        windowManager.addKeyListener(inputManager);
        windowManager.addMouseListener(inputManager);
        windowManager.addMouseMotionListener(inputManager);
        windowManager.addMouseWheelListener(inputManager);
        windowManager.start();
    }

    public static void main(String[] args) {
        Sketch sketch = new Sketch();
        sketch.init();
    }
}
