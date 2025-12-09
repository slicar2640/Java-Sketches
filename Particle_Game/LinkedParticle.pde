class LinkedParticle extends Particle {
  LinkableParticle p1;
  LinkableParticle p2;
  ArrayList<Stick> p1Sticks;
  ArrayList<Stick> p2Sticks;
  boolean isLinked = false;
  public LinkedParticle(LinkableParticle a, LinkableParticle b) {
    super((a.pos.x + b.pos.x) / 2, (a.pos.y + b.pos.y) / 2, a.mass + b.mass, a.bucketIndex, a.manager);
    vel = PVector.mult(a.vel, a.mass).add(PVector.mult(b.vel, b.mass)).div(a.mass + b.mass);
    p1 = a;
    p2 = b;
    p1Sticks = manager.reconnectSticks(a, this);
    p2Sticks = manager.reconnectSticks(b, this);
  }
  
  @Override
  public void show() {
    if(litUp) {
      stroke(255, 255, 0);
      strokeWeight(6);
      point(pos.x, pos.y);
    }
    stroke(255, 0, 0);
    strokeWeight(4);
    point(pos.x, pos.y);
    stroke(255);
    strokeWeight(2);
    point(pos.x, pos.y);
  }
}
