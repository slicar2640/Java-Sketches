package sketch;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import sketch.edit.EditManager;
import sketch.environment.Environment;
import sketch.managers.InputManager;
import sketch.managers.StateManager;
import sketch.managers.WindowManager;
import sketch.managers.StateManager.State;

public class Sketch {
  public WindowManager windowManager;
  public InputManager inputManager;
  public StateManager stateManager;
  public EditManager editManager;
  public Environment environment;

  public HashMap<String, Cursor> cursorMap = new HashMap<>();

  private void setupCursorIcons() {
    try {
      setCursorIcon("open-hand", "/resources/open_hand.png", new Point(16, 16));
      setCursorIcon("closed-hand", "/resources/closed_hand.png", new Point(16, 16));
      setCursorIcon("add-hand", "/resources/add_hand.png", new Point(15, 8));
      setCursorIcon("delete-hand", "/resources/delete_hand.png", new Point(15, 8));
      cursorMap.put("blank", Toolkit.getDefaultToolkit()
          .createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setCursorIcon(String name, String address, Point hotSpot) throws IOException {
    InputStream is = Sketch.class.getResourceAsStream(address);
    BufferedImage icon = ImageIO.read(is);
    cursorMap.put(name, Toolkit.getDefaultToolkit().createCustomCursor(icon, hotSpot, name));
    is.close();
  }

  public void setupStateManager() {
    stateManager = new StateManager(this, State.DISPLAY);
    stateManager.addTransition(State.DISPLAY, State.DEBUG);
    stateManager.addTransition(State.DEBUG, State.DEBUG, State.DISPLAY);
    stateManager.addTransition(State.DEBUG, State.DISPLAY);
    stateManager.addTransition(State.DISPLAY, State.EDIT);
    stateManager.addTransition(State.EDIT, State.EDIT, State.DISPLAY, editManager::exit);
    stateManager.addTransition(State.EDIT, State.DISPLAY, editManager::exit);
  }

  public void saveToFile() {
    JFileChooser fileChooser = new JFileChooser();
    int decision = fileChooser.showSaveDialog(null);
    if (decision == JFileChooser.APPROVE_OPTION) {
      String saveString = environment.getSaveString();
      try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile() + ".txt")) {
        fw.write(saveString.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }
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
    for (int i = 0; i < 4; i++) {
      environment.addRandomObject();
    }
    saveToFile();
    editManager = new EditManager(this, environment);
    setupStateManager();
    setupCursorIcons();

    windowManager.start();
  }
  // TODO: extent tool for parabolas

  public static void main(String[] args) {
    Sketch sketch = new Sketch();
    sketch.init();
  }
}
