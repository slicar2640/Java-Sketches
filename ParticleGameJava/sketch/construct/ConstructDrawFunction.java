package sketch.construct;

import sketch.DrawManager;
import sketch.ParticleGameJava;

@FunctionalInterface
public interface ConstructDrawFunction {
  void run(float scale, int count, ParticleGameJava sketch, DrawManager dm);
}
