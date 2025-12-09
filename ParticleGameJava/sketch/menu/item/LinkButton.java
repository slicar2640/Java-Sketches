package sketch.menu.item;

import sketch.menu.Menu;
import sketch.menu.clickAction.*;

public class LinkButton extends Button {
  String linkedMenu;
  ClickFunction<LinkButton> clickFunction;

  public LinkButton(String id, float x, float y, float w, float h, String label, String linkedMenu, Menu menu,
      ClickFunction<LinkButton> clickFunction, IconFunction show) {
    super(id, x, y, w, h, label, menu, null, show);
    this.clickFunction = clickFunction;
    this.linkedMenu = linkedMenu;
  }

  @Override
  public ClickAction click(float mx, float my) {
    if (!visible)
      return new NotClicked();
    if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
      clickFunction.run(this);
      if (cancelled) {
        cancelled = false;
        return new Nothing();
      } else {
        return new Link(linkedMenu);
      }
    }
    return new NotClicked();
  }
}