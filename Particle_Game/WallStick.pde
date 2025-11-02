class WallStick extends Stick {
  boolean isStatic = false;
  ArrayList<HashSet<Particle>> intersectingBuckets = new ArrayList<>();
  public WallStick(Particle p1, Particle p2) {
    super(p1, p2);
    isStatic = p1.isStatic && p2.isStatic;
    setIntersectingBuckets();
  }
  
  public WallStick(Particle p1, Particle p2, float restLength, float stiffness, float breakLength) {
    super(p1, p2, restLength, stiffness, breakLength);
    isStatic = p1.isStatic && p2.isStatic;
    setIntersectingBuckets();
  }

  public void setIntersectingBuckets() {
    intersectingBuckets.clear();
    int minBucketX = max(0, min(p1.bucketIndex % manager.cols, p2.bucketIndex % manager.cols) - 1);
    int maxBucketX = min(manager.cols - 1, max(p1.bucketIndex % manager.cols, p2.bucketIndex % manager.cols) + 1);
    int minBucketY = max(0, min(p1.bucketIndex / manager.cols, p2.bucketIndex / manager.cols) - 1);
    int maxBucketY = min(manager.cols - 1, max(p1.bucketIndex / manager.cols, p2.bucketIndex / manager.cols) + 1);
    for (int j = minBucketY; j <= maxBucketY; j++) {
      for (int i = minBucketX; i <= maxBucketX; i++) {
        intersectingBuckets.add(manager.buckets.get(i + j * manager.cols));
      }
    }
  }

  @Override
    public void update() {
    if (!isStatic) {
      super.update();
      setIntersectingBuckets();
    }
    for (HashSet<Particle> bucket : intersectingBuckets) {
      for (Particle p : bucket) {
        PVector diff = shortestVectorToPoint(p.pos);
        if (diff != null && diff.mag() < manager.repelDist) {
          float dis = diff.mag();
          if (dis > 0.01 && dis < manager.repelDist) {
            PVector repulsion = diff.normalize()
              .mult((1 - dis / manager.repelDist) * manager.repelStrength);
            p.applyForce(repulsion);
          }
        }
      }
    }
  }

  public PVector shortestVectorToPoint(PVector p) {
    PVector p1_p = PVector.sub(p, p1.pos);
    PVector p1_p2 = PVector.sub(p2.pos, p1.pos);
    float dot = p1_p.dot(p1_p2);
    float t = dot /p1_p2.magSq();
    if (t > 0 && t < 1) {
      return PVector.sub(p, PVector.add(p1.pos, p1_p2.mult(t)));
    } else {
      return null;
    }
  }
}
