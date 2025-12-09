package sketch.stick;

import sketch.DrawManager;
import sketch.particle.Particle;
import sketch.particle.PlantParticle;

public class PlantStick extends Stick {
  PlantParticle p1, p2;

  public PlantStick(PlantParticle p1, PlantParticle p2) {
    super(p1, p2);
    this.p1 = p1;
    this.p2 = p2;
  }

  public PlantStick(Particle p1, Particle p2) {
    this((PlantParticle) p1, (PlantParticle) p2);
  }

  @Override
  public void breakSelf() {
    p1.connected--;
    p2.connected--;
  }

  @Override
  public void show(DrawManager dm) {
    if (litUp) {
      dm.strokeWeight(3);
    } else {
      dm.strokeWeight(1);
    }
    dm.stroke(0, 185, 30);
    dm.line(p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y);
  }
}
