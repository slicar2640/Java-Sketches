package sketch.mouse;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.particle.Particle;
import sketch.particle.PlantParticle;
import sketch.util.Vector;

public class PlantChainInteraction implements MouseInteraction {
  float addDist;
  float addDistSq;
  PlantParticle lastParticle;
  int showAddDistTime = 0;
  ParticleGameJava sketch;

  public PlantChainInteraction(float addDist, ParticleGameJava sketch) {
    this.addDist = addDist;
    addDistSq = addDist * addDist;
    this.sketch = sketch;
  }

  public void mouseClick() {
    try {
      PlantParticle closest = sketch.particleManager.closestParticleToPoint(sketch.mouseX, sketch.mouseY,
          PlantParticle.class);
      if (sketch.shiftPressed && closest != null) {
        lastParticle = closest;
      } else {
        lastParticle = sketch.particleManager.addParticle(sketch.mouseX, sketch.mouseY, 1, PlantParticle.class);
      }
    } catch (ReflectiveOperationException e) {
      System.out.println(e.toString());
    }
  }

  public void mouseDown() {
    if (new Vector(sketch.mouseX, sketch.mouseY).sub(lastParticle.pos).magSq() > addDistSq) {
      Vector step = new Vector(sketch.mouseX, sketch.mouseY).sub(lastParticle.pos).normalize().mult(addDist);
      Vector pos = lastParticle.pos.copy();
      while (new Vector(sketch.mouseX, sketch.mouseY).sub(pos).magSq() > addDistSq) {
        pos.add(step);
        PlantParticle nextParticle = lastParticle.growTo(pos.x, pos.y);
        lastParticle = nextParticle;
      }
    }
  }

  public void mouseUp() {
    lastParticle = null;
  }

  public void updateScale(float delta) {
    addDist += delta;
    if (addDist < 1)
      addDist = 1;
    addDistSq = addDist * addDist;
    showAddDistTime = 30;
  }

  public void updateCount(int delta) {
  }

  public void show(DrawManager dm) {
    if (sketch.shiftPressed) {
      Particle closest = sketch.particleManager.closestParticleToPoint(sketch.mouseX, sketch.mouseY);
      if (closest != null) {
        dm.stroke(255, 30);
        dm.strokeWeight(6);
        dm.noFill();
        dm.circle(closest.pos.x, closest.pos.y, 10);
      }
    }
    if (showAddDistTime > 0) {
      dm.stroke(255);
      dm.strokeWeight(1);
      dm.noFill();
      dm.circle(sketch.mouseX, sketch.mouseY, addDist);
      showAddDistTime--;
    }
    if (lastParticle != null) {
      dm.stroke(255);
      dm.strokeWeight(1);
      dm.noFill();
      dm.circle(lastParticle.pos.x, lastParticle.pos.y, addDist);
    }
  }
}
