package sketch.mouse;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.particle.Particle;
import sketch.stick.RailStick;
import sketch.util.Vector;

public class AddParticleInteraction implements MouseInteraction {
  Class<? extends Particle> particleClass;
  float radius;
  int count;
  ParticleGameJava sketch;
  float maxRailDist = 100;

  public AddParticleInteraction(Class<? extends Particle> clazz, float radius, int count, ParticleGameJava sketch) {
    particleClass = clazz;
    this.radius = radius;
    this.count = count;
    this.sketch = sketch;
  }

  public void mouseClick() {
    if (sketch.mouseButton == MouseButton.RIGHT) {
      if (sketch.shiftPressed) {
        addParticleToRail();
      } else {
        addParticle();
      }
    }
  }

  public void mouseDown() {
    if (sketch.mouseButton == MouseButton.LEFT) {
      addParticle();
    }
  }

  public void mouseUp() {
  }

  private void addParticle() {
    try {
      Vector pos = new Vector(0, 0);
      for (int i = 0; i < count; i++) {
        pos.set(0, 0);
        Vector.random2D(pos);
        pos.mult((float) Math.sqrt(Math.random()) * radius);
        pos.add(sketch.mouseX, sketch.mouseY);
        sketch.particleManager.addParticle(pos.x, pos.y, 1, particleClass);
      }
    } catch (ReflectiveOperationException e) {
      e.printStackTrace();
    }
  }

  private void addParticleToRail() {
    Vector closestPoint = new Vector(0, 0);
    RailStick closestStick = closestRail(closestPoint);
    if (closestStick != null) {
      try {
        closestStick.addRider(sketch.particleManager.addParticle(closestPoint.x, closestPoint.y, 1, particleClass));
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
      }
    }
  }

  private RailStick closestRail(Vector closestPoint) {
    RailStick closest = sketch.particleManager.closestStickToPoint(sketch.mouseX, sketch.mouseY, closestPoint,
        RailStick.class);
    if (closestPoint.dist(sketch.mouseX, sketch.mouseY) < 100) {
      return closest;
    } else {
      return null;
    }
  }

  public void updateScale(float delta) {
    radius += delta;
    if (radius < 0)
      radius = 0;
  }

  public void updateCount(int delta) {
    count += delta;
    if (count < 1)
      count = 1;
  }

  public void show(DrawManager dm) {
    if (sketch.shiftPressed) {
      Vector closestPoint = new Vector(0, 0);
      if (closestRail(closestPoint) != null) {
        dm.noFill();
        dm.stroke(255, 0, 0, 120);
        dm.strokeWeight(2);
        dm.circle(closestPoint.x, closestPoint.y, 5);
      }
    } else {
      dm.noFill();
      dm.stroke(255);
      dm.strokeWeight(1);
      dm.circle(sketch.mouseX, sketch.mouseY, radius);
    }
  }
}
