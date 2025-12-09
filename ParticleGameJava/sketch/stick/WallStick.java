package sketch.stick;

import java.util.ArrayList;
import java.util.HashSet;

import sketch.particle.Particle;
import sketch.util.Vector;

public class WallStick extends Stick {
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
    int minBucketX = Math.max(0, Math.min(p1.bucketIndex % manager.cols, p2.bucketIndex % manager.cols) - 1);
    int maxBucketX = Math.min(manager.cols - 1,
        Math.max(p1.bucketIndex % manager.cols, p2.bucketIndex % manager.cols) + 1);
    int minBucketY = Math.max(0, Math.min(p1.bucketIndex / manager.cols, p2.bucketIndex / manager.cols) - 1);
    int maxBucketY = Math.min(manager.cols - 1,
        Math.max(p1.bucketIndex / manager.cols, p2.bucketIndex / manager.cols) + 1);
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
        Vector diff = shortestVectorToPoint(p.pos);
        if (diff != null && diff.mag() < manager.repelDist) {
          float dis = diff.mag();
          if (dis > 0.01 && dis < manager.repelDist) {
            Vector repulsion = diff.normalize().mult((1 - dis / manager.repelDist) * manager.repelStrength);
            p.applyForce(repulsion);
          }
        }
      }
    }
  }

  public Vector shortestVectorToPoint(Vector p) {
    Vector p1_p = Vector.sub(p, p1.pos);
    Vector p1_p2 = Vector.sub(p2.pos, p1.pos);
    float dot = p1_p.dot(p1_p2);
    float t = dot / p1_p2.magSq();
    if (t > 0 && t < 1) {
      return Vector.sub(p, Vector.add(p1.pos, p1_p2.mult(t)));
    } else {
      return null;
    }
  }
}
