package sketch.stick;

import sketch.DrawManager;
import sketch.ParticleManager;
import sketch.particle.Particle;
import sketch.util.Vector;

public class Stick {
  public Particle p1, p2;
  public float restLength;
  public float stiffness;
  public float breakLengthRatio = 0;
  public ParticleManager manager;
  public boolean litUp = false;
  public boolean blockIncomingP1 = false, blockIncomingP2 = false;
  public boolean blockOutgoingP1 = false, blockOutgoingP2 = false;
  public boolean nextP1 = false, nextP2 = false;
  public boolean disabled = false;

  public Stick(Particle p1, Particle p2, float restLength, float stiffness, float breakLength) {
    this.p1 = p1;
    this.p2 = p2;
    this.restLength = restLength < 0 ? p1 != null ? p1.pos.dist(p2.pos) : restLength : restLength;
    this.stiffness = stiffness;
    this.breakLengthRatio = breakLength;
    if (p1 != null) {
      manager = p1.manager;
    }
  }

  public Stick(Particle p1, Particle p2, float restLength, float stiffness) {
    this(p1, p2, restLength, stiffness, 0);
  }

  public Stick(Particle p1, Particle p2) {
    this(p1, p2, -1, 1, 0);
  }

  public void update() {
    if (disabled)
      return;
    Vector diff = Vector.sub(p1.pos, p2.pos);
    float dis = diff.mag();
    if (breakLengthRatio > 1 && dis > restLength * breakLengthRatio) {
      breakSelf();
      return;
    }
    Vector attraction = diff.normalize().mult((dis - restLength) * stiffness / 2);
    p2.applyForce(attraction);
    p1.applyForce(attraction.mult(-1));
  }

  public void propagateSignal() {
    if (litUp) {
      if (!blockOutgoingP1) {
        p1.nextLitUp = true;
        blockIncomingP1 = true;
      } else {
        blockIncomingP1 = false;
        blockOutgoingP1 = false;
      }
      if (!blockOutgoingP2) {
        p2.nextLitUp = true;
        blockIncomingP2 = true;
      } else {
        blockIncomingP2 = false;
        blockOutgoingP2 = false;
      }
      litUp = false;
    }
    if (p1.litUp) {
      if (!blockIncomingP1) {
        litUp = true;
        blockOutgoingP1 = true;
      } else {
        blockOutgoingP1 = false;
        blockIncomingP1 = false;
      }
    }
    if (p2.litUp) {
      if (!blockIncomingP2) {
        litUp = true;
        blockOutgoingP2 = true;
      } else {
        blockOutgoingP2 = false;
        blockIncomingP2 = false;
      }
    }
  }

  public void breakSelf() {
    manager.removeStick(this);
  }

  public boolean reconnect(Particle oldP, Particle newP) {
    boolean didReconnect = false;
    if (p1 == oldP) {
      p1 = newP;
      didReconnect = true;
    }
    if (p2 == oldP) {
      p2 = newP;
      didReconnect = true;
    }
    return didReconnect;
  }

  public Vector normal() {
    Vector p1_p2 = Vector.sub(p2.pos, p1.pos);
    return new Vector(p1_p2.y, p1_p2.x).normalize();
  }

  public Vector tangent() {
    return Vector.sub(p2.pos, p1.pos).normalize();
  }

  public Vector closestPoint(Vector p) {
    Vector p1_p = Vector.sub(p, p1.pos);
    Vector p1_p2 = Vector.sub(p2.pos, p1.pos);
    float dot = p1_p.dot(p1_p2);
    float t = dot / p1_p2.magSq();
    if (t < 0) {
      return p1.pos;
    } else if (t > 1) {
      return p2.pos;
    } else {
      return Vector.add(p1.pos, p1_p2.mult(t));
    }
  }

  public void show(DrawManager dm) {
    if (litUp) {
      dm.stroke(255, 255, 0);
      dm.strokeWeight(5);
      dm.line(p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y);
    }
    dm.stroke(255);
    dm.strokeWeight(1);
    dm.line(p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y);
  }

  public String basicDebug() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    return builder.toString();
  }

  public String detailedDebug() {
    StringBuilder builder = new StringBuilder(getClass().getSimpleName());
    builder.append('\n');
    builder.append("""
        p1: %s
        p2: %s
        Rest Length: %.1f
        Stiffness: %.1f
        """.formatted(p1.getClass().getSimpleName(), p2.getClass().getSimpleName(), restLength, stiffness));
    builder.append("Breakable? %s\n".formatted(breakLengthRatio > 1 ? "yes" : "no"));
    if (breakLengthRatio > 1) {
      builder.append("Break length ratio: %d%%".formatted((int) (breakLengthRatio * 100)));
    }

    return builder.toString();
  }
}
