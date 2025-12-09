package sketch.stick;

import sketch.DrawManager;
import sketch.particle.StickyParticle;

public class StickyStick extends Stick {
  StickyParticle sp1, sp2;

  public StickyStick(StickyParticle p1, StickyParticle p2, float restLength, float stiffness, float breakLength) {
    super(p1, p2, restLength, stiffness, breakLength);
    p1.stuck.add(p2);
    p2.stuck.add(p1);
    sp1 = p1;
    sp2 = p2;
  }

  @Override
  public void propagateSignal() {
    boolean wasLitUp = litUp;
    super.propagateSignal();
    if (wasLitUp) {
      breakSelf();
    }
  }

  @Override
  public void breakSelf() {
    manager.removeStick(this);
    sp1.stuck.remove(sp2);
    sp2.stuck.remove(sp1);
  }

  @Override
  public void show(DrawManager dm) {
    if (litUp) {
      dm.stroke(255, 255, 0);
      dm.strokeWeight(3);
      dm.line(p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y);
    }
    dm.stroke(255, 255, 180);
    dm.strokeWeight(1);
    dm.line(p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y);
  }
}
