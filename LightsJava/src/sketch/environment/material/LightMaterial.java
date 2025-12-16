package sketch.environment.material;

import java.awt.Color;

import sketch.environment.colortype.ColorType;
import sketch.environment.snapshot.material.MaterialSnapshot;

public class LightMaterial extends Material {
  public LightMaterial(ColorType colorType) {
    this.colorType = colorType;
    matColor = new Color(255, 255, 160);
  }

  public MaterialSnapshot getSnapshot() {
    return new MaterialSnapshot(colorType.copy(), MaterialSnapshot.MatType.LIGHT);
  }
}
