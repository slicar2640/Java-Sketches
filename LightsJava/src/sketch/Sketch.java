package sketch;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import sketch.edit.EditManager;
import sketch.environment.Environment;
import sketch.environment.EnvironmentObject;
import sketch.environment.colortype.SplitColor;
import sketch.environment.material.LightMaterial;
import sketch.util.DrawUtils;

public class Sketch {
  public enum State {
    DISPLAY, DEBUG, EDIT
  }

  private State state;
  public WindowManager windowManager;
  public InputManager inputManager;
  public EditManager editManager;
  public Environment environment;

  public void stateChange(State changeState) {
    if (this.state == changeState) {
      if (this.state == State.EDIT) {
        editManager.exit();
      }
      this.state = State.DISPLAY;
    } else {
      this.state = changeState;
    }
  }

  public State getState() {
    return state;
  }

  private void setupCursorIcons() {
    try {
      DrawUtils drawUtils = windowManager.getDrawUtils();
      InputStream is;
      is = Sketch.class.getResourceAsStream("/resources/open_hand.png");
      drawUtils.setCursorIcon("open-hand", ImageIO.read(is));
      is.close();
      is = Sketch.class.getResourceAsStream("/resources/closed_hand.png");
      drawUtils.setCursorIcon("closed-hand", ImageIO.read(is));
      is.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void init() {
    windowManager = new WindowManager(this, 600, 600);
    inputManager = new InputManager(this);
    windowManager.addKeyListener(inputManager);
    windowManager.addMouseListener(inputManager);
    windowManager.addMouseMotionListener(inputManager);
    windowManager.addMouseWheelListener(inputManager);
    environment = new Environment(this, 10);
    // for (int i = 0; i <= 4; i++) {
    // environment.addRandomObject();
    // }
    environment.addObject(new EnvironmentObject(environment.randomShape(), new LightMaterial(SplitColor.random(), 2)));
    editManager = new EditManager(this, environment);
    setupCursorIcons();

    windowManager.start();
  }

  public static void main(String[] args) {
    Sketch sketch = new Sketch();
    sketch.init();
  }
}
