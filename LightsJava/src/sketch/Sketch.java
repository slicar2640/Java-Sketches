package sketch;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import sketch.edit.EditManager;
import sketch.environment.Environment;
import sketch.environment.EnvironmentObject;
import sketch.environment.colortype.SolidColor;
import sketch.environment.material.LightMaterial;
import sketch.environment.shape.Parabola;
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

  public void saveImage(boolean waitForFinish) {
    JFileChooser fileChooser = new JFileChooser() {
      @Override
      public void approveSelection() {
        File f = getSelectedFile();
        if ((f.exists() || new File(f.toString() + ".png").exists()) && getDialogType() == SAVE_DIALOG) {
          int result = JOptionPane.showConfirmDialog(this, "The file %s exists, overwrite?".formatted(f.toString()),
              "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
          switch (result) {
          case JOptionPane.YES_OPTION:
            super.approveSelection();
            return;
          case JOptionPane.NO_OPTION:
          case JOptionPane.CLOSED_OPTION:
            return;
          case JOptionPane.CANCEL_OPTION:
            cancelSelection();
            return;
          }
        }
        super.approveSelection();
      }
    };
    fileChooser.setFileFilter(new FileNameExtensionFilter(".png", "png"));
    int result = fileChooser.showSaveDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      String selectedFileName = fileChooser.getSelectedFile().toString();
      if (!selectedFileName.endsWith(".png")) {
        selectedFileName += ".png";
      }
      BufferedImage scene;
      if (waitForFinish) {
        scene = windowManager.getSceneOnceFinished();
      } else {
        scene = windowManager.getScene();
      }
      try {
        ImageIO.write(scene, "png", new File(selectedFileName));
        System.out.println("scene saved to " + new File(selectedFileName).getName());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void saveEnvironment() {
    JFileChooser fileChooser = new JFileChooser() {
      @Override
      public void approveSelection() {
        File f = getSelectedFile();
        if ((f.exists() || new File(f.toString() + ".txt").exists()) && getDialogType() == SAVE_DIALOG) {
          int result = JOptionPane.showConfirmDialog(this, "The file %s exists, overwrite?".formatted(f.toString()),
              "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
          switch (result) {
          case JOptionPane.YES_OPTION:
            super.approveSelection();
            return;
          case JOptionPane.NO_OPTION:
          case JOptionPane.CLOSED_OPTION:
            return;
          case JOptionPane.CANCEL_OPTION:
            cancelSelection();
            return;
          }
        }
        super.approveSelection();
      }
    };
    FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(".txt", "txt");
    fileChooser.setFileFilter(txtFilter);
    int result = fileChooser.showSaveDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      String saveString = environment.getSaveString();
      String selectedFileName = fileChooser.getSelectedFile().toString();
      if (!selectedFileName.endsWith(".txt")) {
        selectedFileName += ".txt";
      }
      try (FileWriter fw = new FileWriter(selectedFileName, false)) {
        fw.write(saveString);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void loadEnvironment() {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(".txt", "txt");
    fileChooser.setFileFilter(txtFilter);
    int result = fileChooser.showOpenDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      String selectedFileName = fileChooser.getSelectedFile().toString();
      if (!selectedFileName.endsWith(".txt")) {
        selectedFileName += ".txt";
      }
      try (BufferedReader br = new BufferedReader(new FileReader(selectedFileName))) {
        Iterator<String> iterator = br.lines().iterator();
        environment.load(iterator);
        windowManager.resetImage();
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
    editManager = new EditManager(this, environment);
    setupStateManager();
    setupCursorIcons();

    windowManager.start();
  }

  public static void main(String[] args) {
    Sketch sketch = new Sketch();
    sketch.init();
  }
}
