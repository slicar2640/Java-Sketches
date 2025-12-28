package sketch;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import sketch.edit.EditManager;
import sketch.environment.Environment;
import sketch.util.DrawUtils;

public class Sketch {
  public enum State {
    DISPLAY, DEBUG, EDIT
  }

  private State state = State.DISPLAY;
  public WindowManager windowManager;
  public InputManager inputManager;
  public EditManager editManager;
  public Environment environment;

  public void stateChange(State changeState) {
    switch (changeState) {
    case EDIT:
      switch (state) {
      case DISPLAY:
      case DEBUG:
        state = State.EDIT;
        break;
      case EDIT:
        editManager.exit();
        state = State.DISPLAY;
        break;
      default:
        break;
      }
      break;
    case DEBUG:
      switch (state) {
      case DISPLAY:
        state = State.DEBUG;
        break;
      case DEBUG:
        state = State.DISPLAY;
        break;
      case EDIT:
      default:
        break;
      }
      break;
    case DISPLAY:
      state = changeState;
      break;
    default:
      break;
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
    for (int i = 0; i <= 4; i++) {
      environment.addRandomObject();
    }
    setupCursorIcons();

    windowManager.start();
  } // TODO: EditPanel takes long time, sometimes doesn't open

  public static void main(String[] args) {
    Sketch sketch = new Sketch();
    sketch.init();
  }
}
