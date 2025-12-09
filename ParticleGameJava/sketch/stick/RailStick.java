package sketch.stick;

import java.util.ArrayList;

import sketch.DrawManager;
import sketch.ParticleManager;
import sketch.particle.Particle;
import sketch.util.Vector;

public class RailStick extends Stick {
  public ArrayList<Particle> riderParticles = new ArrayList<>();

  public RailStick(Vector pos1, Vector pos2, ParticleManager manager) {
    super(new Particle(pos1.x, pos1.y, 1, manager), new Particle(pos2.x, pos2.y, 1, manager), pos1.dist(pos2), 1);
    this.p1 = manager.addParticle(pos1.x, pos1.y, 1);
    this.p2 = manager.addParticle(pos2.x, pos2.y, 1);
  }

  public RailStick(Particle p1, Particle p2) {
    super(p1, p2);
  }

  public RailStick(Particle p1, Particle next, float restLength, float stiffness, float breakLength) {
    super(p1, next, restLength, stiffness, breakLength);
  }

  public void addRider(Particle p) {
    riderParticles.add(p);
    p.addDeleteFunction(particle -> {
      riderParticles.remove(particle);
    });
    p.addIgnoredParticle(p1);
    p.addIgnoredParticle(p2);
    p1.addIgnoredParticle(p);
    p2.addIgnoredParticle(p);
  }

  private void constrain(Particle p) {
    Vector p1_p = Vector.sub(p.pos, p1.pos);
    Vector p1_p2 = Vector.sub(p2.pos, p1.pos);
    float dot = p1_p.dot(p1_p2);
    float t = dot / p1_p2.magSq();
    p.pos.set(Vector.lerp(p1.pos, p2.pos, (float) Math.clamp(t, 0.01, 0.99)));
    if (t < 0.01 && p.vel.dot(p1_p2) < 0 || t > 0.99 && p.vel.dot(p1_p2) > 1) {
      p.vel.mult(-0.01f);
    }
  }

  private void alignVelocity(Particle p) {
    Vector axis = Vector.sub(p2.pos, p1.pos).normalize();
    float dot = p.vel.dot(axis);
    p.vel.set(axis.mult(dot));
  }

  private void applyForceFromRider(Particle p) {
    Vector p1_p = Vector.sub(p.pos, p1.pos);
    Vector p1_p2 = Vector.sub(p2.pos, p1.pos);
    float dot = p1_p.dot(p1_p2);
    float t = dot / p1_p2.magSq();
    Vector normal = normal();
    float normalDot = p.lastAcc.dot(normal);
    p1.applyForce(Vector.mult(normal, normalDot * (1 - t)));
    p2.applyForce(Vector.mult(normal, normalDot * t));
  }

  @Override
  public void update() {
    super.update();
    riderParticles.forEach(this::applyForceFromRider);
    riderParticles.forEach(this::constrain);
    riderParticles.forEach(this::alignVelocity);
  }

  @Override
  public void propagateSignal() {
    super.propagateSignal();
    if (litUp) {
      for (Particle rider : riderParticles) {
        rider.nextLitUp = true;
      }
    }
  }

  @Override
  public void show(DrawManager dm) {
    dm.stroke(200);
    dm.strokeWeight(3);
    dm.line(p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y);
    if (litUp) {
      dm.stroke(255, 255, 0);
      dm.strokeWeight(2);
    } else {
      dm.stroke(50);
      dm.strokeWeight(1);
    }
    dm.line(p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y);
  }
}
