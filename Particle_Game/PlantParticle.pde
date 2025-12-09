class PlantParticle extends Particle {
  float stemGrowChance = 0.01;
  int maxBranches = 4;
  float growAngleSpread = 10;

  boolean isLeaf = false;
  boolean isRoot = false;
  int connected = 0;
  PlantParticle previous;
  public PlantParticle(float x, float y, float mass, PlantParticle previous, int bucketIndex, ParticleManager manager) {
    super(x, y, mass, bucketIndex, manager);
    this.previous = previous;
    isLeaf = true;
  }

  public PlantParticle(float x, float y, float mass, int bucketIndex, ParticleManager manager) {
    super(x, y, mass, bucketIndex, manager);
    isRoot = true;
  }

  @Override
    public void updateSignal() {
    if (!litUp && nextLitUp) {
      if (isLeaf) {
        float angle = radians(random(-growAngleSpread, growAngleSpread));
        float angleFromPrevious = PVector.sub(pos, previous.pos).heading();
        PVector newPos = PVector.fromAngle(angleFromPrevious + angle).mult(manager.repelDist).add(pos);
        growTo(newPos.x, newPos.y);
        nextLitUp = false;
      } else if (isRoot && random(1) > (connected / maxBranches)) {
        float angle = random(TWO_PI);
        PVector newPos = PVector.fromAngle(angle).mult(manager.repelDist).add(pos);
        growTo(newPos.x, newPos.y);
      } else if (connected < maxBranches && random(1) < stemGrowChance) {
        float angle = random(TWO_PI);
        PVector newPos = PVector.fromAngle(angle).mult(manager.repelDist).add(pos);
        growTo(newPos.x, newPos.y);
      }
    }
    litUp = nextLitUp;
    nextLitUp = false;
  }

  public PlantParticle growTo(float x, float y) {
    PlantParticle next = manager.addParticle(new PlantParticle(x, y, mass, this, manager.getBucketIndex(x, y), manager));
    manager.addStick(new PlantStick(this, next));
    connected++;
    isLeaf = false;
    return next;
  }

  @Override
    public void show() {
    if (isRoot) {
      strokeWeight(4);
      stroke(140, 100, 20);
    } else if (isLeaf) {
      strokeWeight(4);
      stroke(255, 20, 20);
    } else {
      if (litUp) {
        strokeWeight(6);
      } else {
        strokeWeight(2);
      }
      stroke(0, 185, 30);
    }
    point(pos.x, pos.y);
  }
}
