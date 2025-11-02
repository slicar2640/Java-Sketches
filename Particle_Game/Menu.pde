class Menu {
  HashMap<String, MenuItem> items = new HashMap<>();
  MenuManager manager;
  String name;
  public Menu(String name, MenuManager manager) {
    this.name = name;
    this.manager = manager;
  }
  
  public void click() {
    for (MenuItem item : items.values()) {
      ClickAction action = item.click(mouseX, mouseY);
      switch(action.type) {
        case EXIT:
        manager.exit();
        break;
        case LINK:
        manager.open(action.nextMenuName);
        break;
        case NOTHING:
        default:
        break;
      }
    }
  }
  
  public void addItem(MenuItem item) {
    items.put(item.id, item);
  }
  
  public MenuItem getItemById(String id) {
    return items.get(id);
  }
  
  public void show() {
    for (MenuItem item : items.values()) {
      item.show();
    }
  }
}
