package sketch.mouse;

import java.util.HashSet;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.particle.Particle;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class PushInteraction implements MouseInteraction {
  float strength, radius;
  ParticleGameJava sketch;

  public PushInteraction(float strength, float radius, ParticleGameJava sketch) {
    this.strength = strength;
    this.radius = radius;
    this.sketch = sketch;
  }

  public void mouseClick() {
  }

  public void mouseDown() {
    if (sketch.particleManager.paused)
      return;
    Vector mousePos = new Vector(sketch.mouseX, sketch.mouseY);
    HashSet<Particle> parts = sketch.particleManager.getParticlesInRange(sketch.mouseX - radius, sketch.mouseY - radius,
        sketch.mouseX + radius, sketch.mouseY + radius);
    for (Particle p : parts) {
      Vector diff = Vector.sub(p.pos, mousePos);
      float dis = diff.mag();
      if (dis < 0.01)
        return;
      if (dis < radius) {
        if (sketch.mouseButton == MouseButton.RIGHT) {
          p.vel.mult(0.95f);
        } else {
          Vector repulsion = diff.normalize().mult((1 - dis / radius) * strength);
          p.applyForce(repulsion);
        }
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
    strength += delta;
  }

  public void show(DrawManager dm) {
    float strong = 10;
    dm.stroke(MathUtils.map(strength, 0, -strong, 0, 255), MathUtils.map(strength, 0, strong, 0, 255), 0, 200);
    dm.strokeWeight(Math.max(2, (Math.abs(strength) - strong) / 3));
    dm.noFill();
    dm.circle(sketch.mouseX, sketch.mouseY, radius);
  }
}
