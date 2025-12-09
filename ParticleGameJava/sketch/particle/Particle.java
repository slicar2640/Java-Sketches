package sketch.particle;

import sketch.util.*;

import java.util.HashSet;
import java.util.function.Consumer;

import sketch.DrawManager;
import sketch.ParticleManager;
import sketch.stick.Stick;

public class Particle {
  public Vector pos;
  public Vector vel = new Vector(0, 0);
  public Vector acc = new Vector(0, 0);
  public Vector lastAcc = new Vector(0, 0);
  public float mass;
  public int bucketIndex;
  public float friction = 1e-3f;
  public ParticleManager manager;
  public boolean isStatic = false;
  public boolean litUp = false, nextLitUp = false;
  public boolean deleted = false;
  protected Consumer<Particle> onDelete;
  protected HashSet<Particle> ignored = new HashSet<>();

  public Particle(float x, float y, float mass, ParticleManager manager) {
    pos = new Vector(x, y);
    this.bucketIndex = manager.getBucketIndex(x, y);
    this.manager = manager;
    this.mass = mass;
    onDelete = particle -> {
    };
  }

  public boolean isOutsideBox(float x, float y, float size) {
    return pos.x < x || pos.y < y || pos.x > x + size || pos.y > y + size;
  }

  public void applyForce(Vector force) {
    if (!isStatic) {
      acc.add(force.div(mass));
    }
  }

  public void update(float dt) {
    if (isStatic)
      return;
    vel.add(acc.mult(dt));
    vel.mult(1 - friction * dt * mass);
    pos.add(Vector.mult(vel, dt));
    wallBounce();
    lastAcc.set(acc);
    acc.set(0, 0);
  }

  public void updateSignal() {
    litUp = nextLitUp;
    nextLitUp = false;
  }

  public void repelFrom(Particle other) {
    if (other == this || isStatic || ignored.contains(other))
      return;
    Vector diff = Vector.sub(pos, other.pos);
    float dis = diff.mag();
    if (dis < 0.01)
      return;
    if (dis < manager.repelDist) {
      Vector repulsion = diff.normalize().mult((1 - dis / manager.repelDist) * manager.repelStrength);
      applyForce(repulsion);
    }
  }

  public void wallBounce() {
    if (pos.x > manager.width) {
      pos.x = manager.width - 1;
      vel.x *= -1;
    }
    if (pos.x < 0) {
      pos.x = 1;
      vel.x *= -1;
    }
    if (pos.y > manager.height) {
      pos.y = manager.height - 1;
      vel.y *= -1;
    }
    if (pos.y < 0) {
      pos.y = 1;
      vel.y *= -1;
    }
  }

  public void addDeleteFunction(Consumer<Particle> fxn) {
    onDelete = onDelete.andThen(fxn);
  }

  public void addIgnoredParticle(Particle p) {
    ignored.add(p);
  }

  public void delete() {
    onDelete.accept(this);
  }

  public void show(DrawManager dm) {
    if (litUp) {
      dm.stroke(255, 255, 0);
      dm.strokeWeight(4);
      dm.point(pos.x, pos.y);
    }
    dm.stroke(255);
    dm.strokeWeight(2);
    dm.point(pos.x, pos.y);
  }

  public String basicDebug() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    return builder.toString();
  }

  public String detailedDebug() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    builder.append('\n');
    builder.append("""
        Velocity: %s
        Acceleration: %s
        Mass: %.1f
        """.formatted(vel.toString(), acc.toString(), mass));
    HashSet<Stick> connected = manager.sticksConnectedTo(this);
    builder.append("Connected Sticks [%d]: ".formatted(connected.size()));
    for (Stick s : connected) {
      builder.append(s.getClass().getSimpleName());
      builder.append(' ');
    }
    return builder.toString();
  }
}
