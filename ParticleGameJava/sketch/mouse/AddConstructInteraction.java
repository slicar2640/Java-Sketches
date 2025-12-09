package sketch.mouse;

import java.util.function.BiConsumer;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.construct.ConstructDrawFunction;

public class AddConstructInteraction implements MouseInteraction {
  float scale;
  int count;
  BiConsumer<Float, Integer> constructFunction;
  ConstructDrawFunction showFunction;
  ParticleGameJava sketch;

  public AddConstructInteraction(BiConsumer<Float, Integer> constructFunction, float scale, int count,
      ConstructDrawFunction showFunction, ParticleGameJava sketch) {
    this.constructFunction = constructFunction;
    this.scale = scale;
    this.count = count;
    this.showFunction = showFunction;
    this.sketch = sketch;
  }

  public void mouseClick() {
    constructFunction.accept(scale, count);
  }

  public void mouseDown() {
  }

  public void mouseUp() {
  }

  public void updateScale(float delta) {
    scale += delta;
    if (scale < 0)
      scale = 0;
  }

  public void updateCount(int delta) {
    count += delta;
    if (count < 1)
      count = 1;
  }

  public void show(DrawManager dm) {
    showFunction.run(scale, count, sketch, dm);
  }
}
