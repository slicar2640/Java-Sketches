package sketch.mouse;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.particle.Particle;

public class IgniteInteraction implements MouseInteraction {
  ParticleGameJava sketch;

  public IgniteInteraction(ParticleGameJava sketch) {
    this.sketch = sketch;
    this.sketch = sketch;
  }

  public void mouseClick() {
    Particle closest = sketch.particleManager.closestParticleToPoint(sketch.mouseX, sketch.mouseY);
    if (closest != null) {
      closest.nextLitUp = true;
    }
  }

  public void mouseDown() {
  }

  public void mouseUp() {
  }

  public void updateScale(float delta) {
  }

  public void updateCount(int delta) {
  }

  public void show(DrawManager dm) {
    Particle closest = sketch.particleManager.closestParticleToPoint(sketch.mouseX, sketch.mouseY);
    if (closest != null) {
      dm.stroke(255, 30);
      dm.strokeWeight(6);
      dm.noFill();
      dm.circle(closest.pos.x, closest.pos.y, 10);
    }

    dm.stroke(255, 180, 0);
    dm.strokeWeight(2);
    dm.noFill();
    dm.circle(sketch.mouseX, sketch.mouseY, 15);
  }
}
