class MenuManager {
  boolean active = false;
  Menu currentMenu;
  HashMap<String, Menu> menuMap;
  boolean justExitedMenu = false;
  public MenuManager() {
    menuMap = new HashMap<>();
    menuMap.put("StartOfAdd", new Menu("StartOfAdd", this));
    menuMap.put("CustomConstruct", new Menu("CustomConstruct", this));
    initStartOfAdd(menuMap.get("StartOfAdd"));
    initCustomConstruct(menuMap.get("CustomConstruct"));
  }

  public void open(String menuName) {
    active = true;
    if (!menuMap.containsKey(menuName)) {
      throw new IllegalArgumentException("Menu " + menuName + " does not exist");
    }
    currentMenu = menuMap.get(menuName);
  }

  public void click() {
    currentMenu.click();
  }
  
  public void exit() {
    justExitedMenu = true;
    active = false;
  }

  public void show() {
    currentMenu.show();
  }
}
