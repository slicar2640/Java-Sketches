package sketch.mouse;

import java.util.HashSet;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.particle.Particle;
import sketch.util.Vector;

public class DeleteInteraction implements MouseInteraction {
  float radius;
  ParticleGameJava sketch;

  public DeleteInteraction(float radius, ParticleGameJava sketch) {
    this.radius = radius;
    this.sketch = sketch;
  }

  public void mouseClick() {
    mouseDown();
  }

  public void mouseDown() {
    Vector mousePos = new Vector(sketch.mouseX, sketch.mouseY);
    HashSet<Particle> parts = sketch.particleManager.getParticlesInRange(sketch.mouseX - radius, sketch.mouseY - radius,
        sketch.mouseX + radius, sketch.mouseY + radius);
    for (Particle p : parts) {
      if (p.pos.dist(mousePos) < radius) {
        sketch.particleManager.removeParticle(p);
      }
    }
  }

  public void mouseUp() {
  }

  public void updateScale(float delta) {
    radius += delta;
    if (radius < 0)
      radius = 0;
  }

  public void updateCount(int delta) {
  }

  public void show(DrawManager dm) {
    dm.noFill();
    dm.stroke(255, 0, 0);
    dm.strokeWeight(1);
    dm.circle(sketch.mouseX, sketch.mouseY, radius);
  }
}