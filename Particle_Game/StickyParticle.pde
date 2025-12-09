import java.util.HashSet;
class StickyParticle extends Particle {
  public HashSet<StickyParticle> stuck = new HashSet<>();
  public StickyParticle(float x, float y, float mass, int bucketIndex, ParticleManager manager) {
    super(x, y, mass, bucketIndex, manager);
  }
  
  @Override
  public void repelFrom(Particle other) {
    if(other instanceof StickyParticle && other != this && !stuck.contains((StickyParticle)other)) {
      if(pos.dist(other.pos) < manager.repelDist) {
        manager.addStick(new StickyStick(this, (StickyParticle)other, manager.repelDist, 1, 1.25)); //1.15-1.25 works best
      }
    }
    super.repelFrom(other);
  }
  
  @Override
  public void show() {
    if(litUp) {
      stroke(255, 255, 0);
      strokeWeight(4);
      point(pos.x, pos.y);
    }
    stroke(255, 255, 180);
    strokeWeight(2);
    point(pos.x, pos.y);
  }
}
