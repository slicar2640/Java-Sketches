class ClickAction {
  ActionType type;
  String nextMenuName;
  
  public ClickAction(ActionType type, String nextMenuName) {
    this.type = type;
    this.nextMenuName = nextMenuName;
  }
  
  public ClickAction(ActionType type) {
    this.type = type;
  }
}

enum ActionType {
  NOTHING,
  EXIT,
  LINK
}
