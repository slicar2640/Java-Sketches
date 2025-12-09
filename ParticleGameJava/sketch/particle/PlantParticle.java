package sketch.particle;

import sketch.DrawManager;
import sketch.ParticleManager;
import sketch.stick.PlantStick;
import sketch.util.MathUtils;
import sketch.util.Vector;

public class PlantParticle extends Particle {
  float stemGrowChance = 0.01f;
  int maxBranches = 4;
  float growAngleSpread = 10;

  boolean isLeaf = false;
  boolean isRoot = false;
  public int connected = 0;
  PlantParticle previous;

  public PlantParticle(float x, float y, float mass, PlantParticle previous, ParticleManager manager) {
    super(x, y, mass, manager);
    this.previous = previous;
    isLeaf = true;
  }

  public PlantParticle(float x, float y, float mass, ParticleManager manager) {
    super(x, y, mass, manager);
    isRoot = true;
  }

  @Override
  public void updateSignal() {
    if (!litUp && nextLitUp) {
      if (isLeaf) {
        float angle = MathUtils.radians(MathUtils.random(-growAngleSpread, growAngleSpread));
        float angleFromPrevious = Vector.sub(pos, previous.pos).heading();
        Vector newPos = Vector.fromAngle(angleFromPrevious + angle, manager.repelDist).add(pos);
        growTo(newPos.x, newPos.y);
        nextLitUp = false;
      } else if (isRoot && MathUtils.random(1) > (connected / maxBranches)) {
        float angle = MathUtils.random(2 * (float) Math.PI);
        Vector newPos = Vector.fromAngle(angle).mult(manager.repelDist).add(pos);
        growTo(newPos.x, newPos.y);
      } else if (connected < maxBranches && MathUtils.random(1) < stemGrowChance) {
        float angle = MathUtils.random(2 * (float) Math.PI);
        Vector newPos = Vector.fromAngle(angle, manager.repelDist).add(pos);
        growTo(newPos.x, newPos.y);
      }
    }
    super.updateSignal();
  }

  public PlantParticle growTo(float x, float y) {
    PlantParticle next = manager.addParticle(new PlantParticle(x, y, mass, this, manager));
    manager.addStick(new PlantStick(this, next));
    connected++;
    isLeaf = false;
    return next;
  }

  @Override
  public void show(DrawManager dm) {
    if (isRoot) {
      dm.strokeWeight(4);
      dm.stroke(140, 100, 20);
    } else if (isLeaf) {
      dm.strokeWeight(4);
      dm.stroke(255, 20, 20);
    } else {
      if (litUp) {
        dm.strokeWeight(6);
      } else {
        dm.strokeWeight(2);
      }
      dm.stroke(0, 185, 30);
    }
    dm.point(pos.x, pos.y);
  }
}
