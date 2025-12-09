package sketch.mouse;

import sketch.DrawManager;
import sketch.ParticleGameJava;

public class MouseInteractionManager {
  public MouseInteraction currentInteraction;
  ParticleGameJava sketch;

  public MouseInteractionManager(MouseInteraction first, ParticleGameJava sketch) {
    currentInteraction = first;
    this.sketch = sketch;
  }

  public void mouseClick() {
    currentInteraction.mouseClick();
  }

  public void mouseDown() {
    currentInteraction.mouseDown();
  }

  public void mouseUp() {
    currentInteraction.mouseUp();
  }

  public void updateScale(float delta) {
    currentInteraction.updateScale(delta);
  }

  public void updateCount(int delta) {
    currentInteraction.updateCount(delta);
  }

  public void show(DrawManager dm) {
    currentInteraction.show(dm);
  }
}
