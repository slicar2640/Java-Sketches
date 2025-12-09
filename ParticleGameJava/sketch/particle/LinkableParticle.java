package sketch.particle;

import sketch.DrawManager;
import sketch.ParticleManager;

public class LinkableParticle extends Particle {
  public float connectDistance = 4;

  public LinkableParticle(float x, float y, float mass, ParticleManager manager) {
    super(x, y, mass, manager);
  }

  @Override
  public void repelFrom(Particle other) {
    if (other == this)
      return;
    if (other instanceof LinkableParticle linkableP) {
      if (!other.deleted && pos.dist(linkableP.pos) < connectDistance) {
        manager.addParticle(new LinkedParticle(this, linkableP));
      }
    } else if (other instanceof LinkedParticle linkedP) {
      if (pos.dist(linkedP.pos) < connectDistance && linkedP.particles.size() < linkedP.maxMergedParticles) {
        linkedP.addParticle(this);
      }
    } else {
      super.repelFrom(other);
    }
  }

  public void show(DrawManager dm) {
    if (litUp) {
      dm.stroke(255, 255, 0);
      dm.strokeWeight(4);
      dm.point(pos.x, pos.y);
    }
    dm.stroke(0, 255, 0);
    dm.strokeWeight(2);
    dm.point(pos.x, pos.y);
  }
}