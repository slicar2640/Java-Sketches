package sketch.environment.snapshot;

import java.awt.Color;
import java.util.Arrays;

import sketch.environment.Intersection;
import sketch.environment.Ray;
import sketch.environment.snapshot.material.MaterialSnapshot;
import sketch.util.Vector;

public class EnvironmentSnapshot {

  public final EnvironmentObjectSnapshot[] objects;

  public EnvironmentSnapshot(EnvironmentObjectSnapshot[] objects) {
    this.objects = objects;
  }
}
