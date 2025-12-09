package sketch.mouse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.DrawManager.TextAlign;
import sketch.particle.Particle;
import sketch.stick.Stick;
import sketch.util.Vector;

public class InspectInteraction implements MouseInteraction {
  ParticleGameJava sketch;

  public InspectInteraction(ParticleGameJava sketch) {
    this.sketch = sketch;
    this.sketch = sketch;
  }

  public void mouseClick() {
    if (sketch.shiftPressed) {
      Vector closestPoint = new Vector(0, 0);
      Stick closest = sketch.particleManager.closestStickToPoint(sketch.mouseX, sketch.mouseY, closestPoint);
      if (closest != null && closestPoint.dist(sketch.mouseX, sketch.mouseY) < 100) {
        System.out.println(closest.basicDebug());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sketch/inspect.txt"))) {
          writer.write(closest.detailedDebug());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      Particle closest = sketch.particleManager.closestParticleToPoint(sketch.mouseX, sketch.mouseY);
      if (closest != null) {
        System.out.println(closest.basicDebug());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sketch/inspect.txt"))) {
          writer.write(closest.detailedDebug());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
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
    if (sketch.shiftPressed) {
      Vector closestPoint = new Vector(0, 0);
      Stick closest = sketch.particleManager.closestStickToPoint(sketch.mouseX, sketch.mouseY, closestPoint);
      if (closest != null && closestPoint.dist(sketch.mouseX, sketch.mouseY) < 100) {
        dm.stroke(255, 30);
        dm.strokeWeight(6);
        dm.noFill();
        Vector normal = closest.normal();
        Vector tangent = closest.tangent();
        dm.polygon(closest.p1.pos.x - normal.x * 10 - tangent.x * 10, closest.p1.pos.y + normal.y * 10 - tangent.y * 10,
            closest.p2.pos.x - normal.x * 10 + tangent.x * 10, closest.p2.pos.y + normal.y * 10 + tangent.y * 10,
            closest.p2.pos.x + normal.x * 10 + tangent.x * 10, closest.p2.pos.y - normal.y * 10 + tangent.y * 10,
            closest.p1.pos.x + normal.x * 10 - tangent.x * 10, closest.p1.pos.y - normal.y * 10 - tangent.y * 10);
      }
    } else {
      Particle closest = sketch.particleManager.closestParticleToPoint(sketch.mouseX, sketch.mouseY);
      if (closest != null) {
        dm.stroke(255, 30);
        dm.strokeWeight(6);
        dm.noFill();
        dm.circle(closest.pos.x, closest.pos.y, 10);
      }
    }

    dm.fill(255);
    dm.text("?", sketch.mouseX + 8, sketch.mouseY - 8, 15, TextAlign.LEFT_BOTTOM);
  }
}
