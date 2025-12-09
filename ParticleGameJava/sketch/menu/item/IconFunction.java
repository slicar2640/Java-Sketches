package sketch.menu.item;

import sketch.DrawManager;

@FunctionalInterface
public interface IconFunction {
  void run(float x, float y, float w, DrawManager dm);
}
