package sketch.mouse;

import java.awt.event.MouseEvent;

public enum MouseButton {
  NOBUTTON, LEFT, MIDDLE, RIGHT;

  public static MouseButton fromEvent(MouseEvent e) {
    return switch (e.getButton()) {
    case MouseEvent.BUTTON1 -> LEFT;
    case MouseEvent.BUTTON2 -> MIDDLE;
    case MouseEvent.BUTTON3 -> RIGHT;
    default -> NOBUTTON;
    };
  }
}
