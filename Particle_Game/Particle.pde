class Particle {
  public PVector pos;
  public PVector vel = new PVector();
  public PVector acc = new PVector();
  public float mass;
  public int bucketIndex;
  public float friction = 1e-3;
  public ParticleManager manager;
  public boolean isStatic = false;
  public boolean litUp = false, nextLitUp = false;

  public Particle(float x, float y, float mass, int bucketIndex, ParticleManager manager) {
    pos = new PVector(x, y);
    this.bucketIndex = bucketIndex;
    this.manager = manager;
    this.mass = mass;
  }

  public boolean isOutsideBox(float x, float y, float size) {
    return pos.x < x || pos.y < y || pos.x > x + size || pos.y > y + size;
  }

  public void applyForce(PVector force) {
    if (!isStatic) {
      acc.add(force.div(mass));
    }
  }

  public void update(float dt) {
    if (isStatic) return;
      vel.add(acc.mult(dt));
      vel.mult(1 - friction * dt * mass);
      pos.add(PVector.mult(vel, dt));
      wallBounce();
      acc.set(0, 0);
  }
  
  public void updateSignal() {
    litUp = nextLitUp;
    nextLitUp = false;
  }

  public void repelFrom(Particle other) {
    if (other == this || isStatic) return;
    PVector diff = PVector.sub(pos, other.pos);
    float dis = diff.mag();
    if (dis < 0.01) return;
    if (dis < manager.repelDist) {
      PVector repulsion = diff.normalize()
        .mult((1 - dis / manager.repelDist) * manager.repelStrength);
      applyForce(repulsion);
    }
  }

  public void wallBounce() {
    if (pos.x > width) {
      pos.x = width - 1;
      vel.x *= -1;
    }
    if (pos.x < 0) {
      pos.x = 1;
      vel.x *= -1;
    }
    if (pos.y > height) {
      pos.y = height - 1;
      vel.y *= -1;
    }
    if (pos.y < 0) {
      pos.y = 1;
      vel.y *= -1;
    }
  }
  
  public void show() {
    if(litUp) {
      stroke(255, 255, 0);
      strokeWeight(4);
      point(pos.x, pos.y);
    }
    stroke(255);
    strokeWeight(2);
    point(pos.x, pos.y);
  }
}
