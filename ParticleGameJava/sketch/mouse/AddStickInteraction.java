package sketch.mouse;

import java.util.HashSet;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.particle.Particle;
import sketch.stick.Stick;
import sketch.util.Vector;

public class AddStickInteraction implements MouseInteraction {
  Class<? extends Stick> stickClass;
  public float length;
  public float stiffness;
  public Particle p1;
  public ParticleGameJava sketch;

  public AddStickInteraction(Class<? extends Stick> clazz, float length, float stiffness, ParticleGameJava sketch) {
    stickClass = clazz;
    this.length = length;
    this.stiffness = stiffness;
    this.sketch = sketch;
  }

  public AddStickInteraction(float length, float stiffness, ParticleGameJava sketch) {
    this.length = length;
    this.stiffness = stiffness;
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
    if (p1 != null) {
      Particle closest = closestParticle();
      if (closest != null && closest != p1) {
        connect(closest);
        p1 = null;
      }
    } else {
      Particle closest = closestParticle();
      if (closest != null) {
        p1 = closest;
      }
    }
  }

  public void mouseDown() {
  }

  public void mouseUp() {
  }

  public void connect(Particle next) {
    if (stickClass != null) {
      try {
        Stick stick = sketch.particleManager.addStick(p1, next, stickClass);
        stick.stiffness = stiffness;
        if (!sketch.shiftPressed) {
          stick.restLength = length;
        }
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
      }
    } else {
      sketch.particleManager.addStick(new Stick(p1, next, sketch.shiftPressed ? -1 : length, stiffness));
    }
  }

  public void updateScale(float delta) {
    length += delta;
    if (length < 0)
      length = 0;
  }

  public void updateCount(int delta) {
    stiffness += 0.01 * delta;
    if (stiffness < 0)
      stiffness = 0;
  }

  public void show(DrawManager dm) {
    if (p1 != null) {
      if (sketch.shiftPressed) {
        dm.stroke((2 - stiffness) * 255, stiffness * 255, stiffness * 255);
        dm.strokeWeight(2);
        dm.noFill();
        dm.line(p1.pos.x, p1.pos.y, sketch.mouseX, sketch.mouseY);
      } else {
        Vector p2 = Vector.add(p1.pos, new Vector(sketch.mouseX, sketch.mouseY).sub(p1.pos).normalize().mult(length));
        dm.stroke(((2 - stiffness) * 255), stiffness * 255, stiffness * 255);
        dm.strokeWeight(2);
        dm.noFill();
        dm.line(p1.pos.x, p1.pos.y, p2.x, p2.y);
      }

      dm.stroke(255, 80);
      dm.strokeWeight(5 + (float) Math.sin(sketch.frameCount / 15));
      dm.noFill();
      dm.circle(p1.pos.x, p1.pos.y, 10);
    }

    Particle closest = closestParticle();
    if (closest != null) {
      dm.stroke(255, 30);
      dm.strokeWeight(6);
      dm.noFill();
      dm.circle(closest.pos.x, closest.pos.y, 10);
    }
  }
}
