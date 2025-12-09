package sketch.mouse;

import java.util.HashSet;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.particle.Particle;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class GrabInteraction implements MouseInteraction {
  float strength;
  HashSet<Particle> grabbedParticles = new HashSet<>();
  ParticleGameJava sketch;

  public GrabInteraction(float strength, ParticleGameJava sketch) {
    this.strength = strength;
    this.sketch = sketch;
  }

  private Particle closestParticle() {
    HashSet<Particle> neighbors = sketch.particleManager.neighborParticles(sketch.mouseX, sketch.mouseY);
    Particle closest = null;
    float closestDist = sketch.particleManager.repelDist;
    for (Particle p : neighbors) {
      if (Vector.dist(p.pos.x, p.pos.y, sketch.mouseX, sketch.mouseY) < closestDist) {
        closest = p;
        closestDist = Vector.dist(p.pos.x, p.pos.y, sketch.mouseX, sketch.mouseY);
      }
    }
    return closest;
  }

  public void mouseClick() {
    Particle closest = closestParticle();
    if (closest != null) {
      if (grabbedParticles.isEmpty() || sketch.shiftPressed) {
        grabbedParticles.add(closest);
      } else if (sketch.ctrlPressed) {
        if (grabbedParticles.contains(closest)) {
          grabbedParticles.remove(closest);
        } else {
          grabbedParticles.add(closest);
        }
      } else if (sketch.altPressed) {
        grabbedParticles.remove(closest);
      }
    }
  }

  public void mouseDown() {
    if (!sketch.shiftPressed) {
      Vector mousePos = new Vector(sketch.mouseX, sketch.mouseY);
      for (Particle p : grabbedParticles) {
        Vector diff = Vector.sub(mousePos, p.pos);
        p.applyForce(diff.mult(strength));
      }
      if (sketch.mouseButton == MouseButton.RIGHT) {
        for (Particle p : grabbedParticles) {
          p.vel.mult(0.8f);
        }
      }
    }
  }

  public void mouseUp() {
    if (sketch.mouseButton == MouseButton.MIDDLE) {
      grabbedParticles.clear();
    }
  }

  public void updateScale(float delta) {
    strength += delta / 150;
  }

  public void updateCount(int delta) {
  }

  public void show(DrawManager dm) {
    for (Particle p : grabbedParticles) {
      dm.stroke(255, 80);
      dm.strokeWeight(5 + (float) Math.sin(sketch.frameCount / 15));
      dm.noFill();
      dm.circle(p.pos.x, p.pos.y, 10);
    }

    Particle closest = closestParticle();
    if (closest != null) {
      dm.stroke(255, 30);
      dm.strokeWeight(6);
      dm.noFill();
      dm.circle(closest.pos.x, closest.pos.y, 10);
    }

    float strong = 0.1f;
    dm.stroke(MathUtils.map(strength, 0, -strong, 0, 255), MathUtils.map(strength, 0, strong, 0, 255), 0);
    dm.strokeWeight((float) Math.max(1.5, (Math.abs(strength) - strong) * 4));
    dm.noFill();
    dm.circle(sketch.mouseX, sketch.mouseY, 15);
  }
}
