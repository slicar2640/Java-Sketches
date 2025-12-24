package sketch.edit;

import java.awt.Cursor;
import java.util.ArrayList;

import sketch.Sketch;
import sketch.edit.editpanel.EditPanel;
import sketch.environment.Environment;
import sketch.environment.EnvironmentObject;
import sketch.util.DrawUtils;

public class EditManager {
  public Sketch sketch;
  private Environment environment;
  private final Object selectionLock = new Object();
  private volatile EditTool selectedTool = null;
  private volatile EnvironmentObject selectedObject = null;
  private int objectClickDist = 10;
  public EditPanel editPanel = null;

  public EditManager(Sketch sketch, Environment environment) {
    this.sketch = sketch;
    this.environment = environment;
  }

  public void drag(float mx, float my) {
    if (selectedTool != null) {
      selectedTool.drag(mx, my);
    }
  }

  public void show(DrawUtils drawUtils) {
    boolean alreadyHoveredSomething = false;
    if (selectedTool != null) {
      alreadyHoveredSomething = true;
    }
    synchronized (selectionLock) {
      if (selectedObject != null) {
        for (EditTool tool : selectedObject.getTools()) {
          if (!alreadyHoveredSomething && tool.isHovered(sketch.inputManager.mouse.x, sketch.inputManager.mouse.y)) {
            tool.highlighted = true;
            alreadyHoveredSomething = true;
          } else {
            tool.highlighted = false;
          }
        }
        selectedObject.showMaterial(drawUtils);
        selectedObject.show(drawUtils);
        selectedObject.showEditTools(drawUtils);
      } else {
        ArrayList<EnvironmentObject> objects = environment.getObjects();
        for (EnvironmentObject object : objects) {
          for (EditTool tool : object.getTools()) {
            if (!alreadyHoveredSomething && tool.isHovered(sketch.inputManager.mouse.x, sketch.inputManager.mouse.y)) {
              tool.highlighted = true;
              alreadyHoveredSomething = true;
            } else {
              tool.highlighted = false;
            }
          }
        }
        for (EnvironmentObject object : objects) {
          object.showEditTools(drawUtils);
        }
      }
    }
    if (selectedTool != null) {
      selectedTool.highlighted = true;
      drawUtils.setCursor("closed-hand");
    } else if (alreadyHoveredSomething) {
      drawUtils.setCursor("open-hand");
    } else {
      drawUtils.setCursor(Cursor.DEFAULT_CURSOR);
    }
  }

  public void selectObjectAt(int mx, int my) {
    synchronized (selectionLock) {
      for (EnvironmentObject object : environment.getObjects()) {
        if (object.shape.distToPoint(mx, my) < objectClickDist) {
          selectedObject = object;
          deselectTool();
          return;
        }
      }
      deselectObject();
    }
  }

  public void selectToolAt(int mx, int my) {
    synchronized (selectionLock) {
      if (selectedObject != null) {
        for (EditTool tool : selectedObject.getTools()) {
          if (tool.isHovered(mx, my)) {
            selectedTool = tool;
            return;
          }
        }
      } else {
        for (EnvironmentObject object : environment.getObjects()) {
          for (EditTool tool : object.getTools()) {
            if (tool.isHovered(mx, my)) {
              selectedTool = tool;
              return;
            }
          }
        }
      }
    }
  }

  public void deselectTool() {
    selectedTool = null;
  }

  private void deselectObject() {
    selectedObject = null;
    if (editPanel != null) {
      closeEditPanel();
    }
  }

  public void tryToOpenEditPanel() {
    synchronized (selectionLock) {
      if (selectedObject != null && editPanel == null) {
        openEditPanel(selectedObject);
      }
    }
  }

  public void openEditPanel(EnvironmentObject obj) {
    editPanel = new EditPanel(sketch, this, 300, sketch.windowManager.height);
    selectedObject.setupEditPanel(editPanel);
    editPanel.updateVisuals();
  }

  public void closeEditPanel() {
    editPanel.dispose();
    editPanel = null;
  }

  public void exit() {
    deselectTool();
    deselectObject();
  }
}
