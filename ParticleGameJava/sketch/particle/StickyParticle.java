package sketch.particle;

import java.util.HashSet;

import sketch.DrawManager;
import sketch.ParticleManager;
import sketch.stick.StickyStick;

public class StickyParticle extends Particle {
  public HashSet<StickyParticle> stuck = new HashSet<>();

  public StickyParticle(float x, float y, float mass, ParticleManager manager) {
    super(x, y, mass, manager);
  }

  @Override
  public void repelFrom(Particle other) {
    if (other instanceof StickyParticle && other != this && !stuck.contains((StickyParticle) other)) {
      if (pos.dist(other.pos) < manager.repelDist) {
        manager.addStick(new StickyStick(this, (StickyParticle) other, manager.repelDist, 1, 1.25f)); // 1.15-1.25 best
      }
    }
    super.repelFrom(other);
  }

  @Override
  public void show(DrawManager dm) {
    if (litUp) {
      dm.stroke(255, 255, 0);
      dm.strokeWeight(4);
      dm.point(pos.x, pos.y);
    }
    dm.stroke(255, 255, 180);
    dm.strokeWeight(2);
    dm.point(pos.x, pos.y);
  }
}
