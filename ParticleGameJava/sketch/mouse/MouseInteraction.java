package sketch.mouse;

import sketch.DrawManager;

interface MouseInteraction {
  public void mouseClick();

  public void mouseDown();

  public void mouseUp();

  public void updateScale(float delta);

  public void updateCount(int delta);

  public void show(DrawManager dm);
}