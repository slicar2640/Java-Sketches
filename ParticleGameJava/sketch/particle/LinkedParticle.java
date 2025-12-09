package sketch.particle;

import java.util.HashMap;
import java.util.HashSet;

import sketch.DrawManager;
import sketch.stick.Stick;
import sketch.util.Vector;

public class LinkedParticle extends Particle {
  HashMap<LinkableParticle, HashSet<Stick>> particles = new HashMap<>(2);
  int maxMergedParticles = 10;
  public float connectDistance = 4;

  public LinkedParticle(LinkableParticle a, LinkableParticle b) {
    super((a.pos.x + b.pos.x) / 2, (a.pos.y + b.pos.y) / 2, a.mass + b.mass, a.manager);
    vel = Vector.mult(a.vel, a.mass).add(Vector.mult(b.vel, b.mass)).div(a.mass + b.mass);
    particles.put(a, manager.reconnectSticks(a, this));
    particles.put(b, manager.reconnectSticks(b, this));
    manager.removeParticle(a);
    manager.removeParticle(b);
  }

  public void addParticle(LinkableParticle p) {
    particles.put(p, manager.reconnectSticks(p, this));
    manager.removeParticle(p);
  }

  public void merge(LinkedParticle other) {
    int numMergedParticles = other.particles.size() + particles.size();
    if (numMergedParticles > maxMergedParticles) {
      if (numMergedParticles <= other.maxMergedParticles) {
        other.merge(this);
      }
      return;
    } else {
      for (HashSet<Stick> stickList : other.particles.values()) {
        for (Stick s : stickList) {
          if (s.p1 == other)
            s.p1 = this;
          if (s.p2 == other)
            s.p2 = this;
        }
      }
      particles.putAll(other.particles);
    }
  }

  @Override
  public void repelFrom(Particle other) {
    if (other == this)
      return;
    if (other instanceof LinkedParticle linkedP) {
      if (!linkedP.deleted) {
        merge(linkedP);
      }
    }
  }

  @Override
  public void updateSignal() {
    if (!litUp && nextLitUp) {
      split();
    }
  }

  public HashSet<LinkableParticle> split() {
    HashSet<LinkableParticle> newParticles = new HashSet<>(particles.keySet());
    for (LinkableParticle part : newParticles) {
      manager.addParticle(part);
      part.pos.set(pos);
      part.pos.add(Vector.random2D().mult(connectDistance + 2));
      part.vel.set(vel);
      part.acc.set(acc);
      for (Stick stick : particles.get(part)) {
        stick.reconnect(this, part);
      }
    }
    manager.removeParticle(this);
    return newParticles;
  }

  @Override
  public void show(DrawManager dm) {
    if (litUp) {
      dm.stroke(255, 255, 0);
      dm.strokeWeight(6);
      dm.point(pos.x, pos.y);
    }
    dm.stroke(255, 0, 0);
    dm.strokeWeight(4);
    dm.point(pos.x, pos.y);
    dm.stroke(255);
    dm.strokeWeight(2);
    dm.point(pos.x, pos.y);
  }
}