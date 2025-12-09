package sketch.menu;

import java.util.HashMap;

import sketch.DrawManager;
import sketch.menu.clickAction.*;
import sketch.menu.item.MenuItem;

public class Menu {
  private HashMap<String, MenuItem> items = new HashMap<>();
  public MenuManager manager;
  public String name;

  public Menu(String name, MenuManager manager) {
    this.name = name;
    this.manager = manager;
  }

  public void click() {
    for (MenuItem item : items.values()) {
      ClickAction action = item.click(manager.sketch.mouseX, manager.sketch.mouseY);
      if (action instanceof Exit) {
        manager.exit();
      } else if (action instanceof Link link) {
        manager.open(link.linkedMenuName());
      }
    }
  }

  public void addItem(MenuItem item) {
    items.put(item.id, item);
  }

  public MenuItem getItemById(String id) {
    return items.get(id);
  }

  public void show(DrawManager dm) {
    for (MenuItem item : items.values()) {
      item.show(dm);
    }
  }
}
