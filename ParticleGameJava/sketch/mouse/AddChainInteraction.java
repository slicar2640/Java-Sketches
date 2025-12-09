package sketch.mouse;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.particle.Particle;
import sketch.stick.Stick;
import sketch.util.Vector;

public class AddChainInteraction implements MouseInteraction {
  float addDist;
  float addDistSq;
  Particle particleTemplate;
  Stick stickTemplate;
  Particle lastParticle;
  int showAddDistTime = 0;
  ParticleGameJava sketch;

  public AddChainInteraction(float addDist, Particle particleTemplate, Stick stickTemplate, ParticleGameJava sketch) {
    this.addDist = addDist;
    addDistSq = addDist * addDist;
    this.particleTemplate = particleTemplate;
    this.stickTemplate = stickTemplate;
    this.sketch = sketch;
  }

  public void mouseClick() {
    try {
      Particle closest = sketch.particleManager.closestParticleToPoint(sketch.mouseX, sketch.mouseY);
      if (sketch.shiftPressed && closest != null) {
        lastParticle = closest;
      } else {
        lastParticle = sketch.particleManager.copyParticle(sketch.mouseX, sketch.mouseY, particleTemplate);
      }
    } catch (ReflectiveOperationException e) {
      System.out.println(e.toString());
    }
  }

  public void mouseDown() {
    if (lastParticle == null) { // Hasn't created first particle yet
      return;
    }
    if (new Vector(sketch.mouseX, sketch.mouseY).sub(lastParticle.pos).magSq() > addDistSq) {
      try {
        Vector step = new Vector(sketch.mouseX, sketch.mouseY).sub(lastParticle.pos).normalize().mult(addDist);
        Vector pos = lastParticle.pos.copy();
        while (new Vector(sketch.mouseX, sketch.mouseY).sub(pos).magSq() > addDistSq) {
          pos.add(step);
          Particle nextParticle = sketch.particleManager.copyParticle(pos.x, pos.y, particleTemplate);
          sketch.particleManager.copyStick(lastParticle, nextParticle, stickTemplate);
          lastParticle = nextParticle;
        }
      } catch (ReflectiveOperationException e) {
        System.out.println(e.toString());
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
