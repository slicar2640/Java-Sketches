class ConstructBuilder {
  ParticleManager manager;
  public Particle defaultParticle;
  public Stick defaultStick;
  public ConstructBuilder(ParticleManager manager) {
    this.manager = manager;
    defaultParticle = new Particle(0, 0, 1, 0, manager);
    defaultStick = new Stick(defaultParticle, defaultParticle, -1, 1);
  }

  public void addLoop(float cx, float cy, float radius, int count, Particle particleTemplate, Stick stickTemplate) {
    try {
      Particle first = manager.copyParticle(cx + radius, cy, particleTemplate);
      Particle last = first;
      for (int i = 1; i < count; i++) {
        float x = cx + radius * cos((float)i / count * TWO_PI);
        float y = cy + radius * sin((float)i / count * TWO_PI);
        Particle p = manager.copyParticle(x, y, particleTemplate);
        manager.copyStick(last, p, stickTemplate);
        last = p;
      }
      manager.copyStick(last, first, stickTemplate);
    }
    catch (ReflectiveOperationException e) {
      println(e.toString());
    }
  }

  public void addLoop(float cx, float cy, float radius, int count) {
    addLoop(cx, cy, radius, count, defaultParticle, defaultStick);
  }

  public void addTriangle(float x, float y, int sideCount, float spacing, Particle particleTemplate, Stick stickTemplate) {
    try {
      float yOff = -spacing * (sideCount - 1) * SQRT3_2 * 2 / 3;
      Particle[][] parts = new Particle[sideCount][];
      parts[0] = new Particle[1];
      parts[0][0] = manager.addParticle(x, y + yOff, particleTemplate.mass, particleTemplate.getClass());
      for (int i = 1; i < sideCount; i++) {
        parts[i] = new Particle[i + 1];
        for (int j = 0; j <= i; j++) {
          float x0 = x + ((float) -i / 2 + j) * spacing;
          float y0 = y + yOff + i * SQRT3_2 * spacing;
          parts[i][j] = manager.addParticle(x0, y0, particleTemplate.mass, particleTemplate.getClass());
        }
        for (int k = 0; k < i; k++) {
          Particle top = parts[i - 1][k];
          Particle left = parts[i][k];
          Particle right = parts[i][k + 1];
          manager.copyStick(top, left, stickTemplate);
          manager.copyStick(top, right, stickTemplate);
          manager.copyStick(left, right, stickTemplate);
        }
      }
    }
    catch (ReflectiveOperationException e) {
      println(e.toString());
    }
  }

  public void addTriangle(float x, float y, int sideCount, float spacing) {
    addTriangle(x, y, sideCount, spacing, defaultParticle, defaultStick);
  }

  public void addRectangle(float ox, float oy, int widthCount, int heightCount, float spacing, boolean centered, Particle particleTemplate, Stick stickTemplate) {
    try {
      float xOff = centered ? -(widthCount - 1) * spacing / 2 : 0;
      float yOff = centered ? -(heightCount - 1) * spacing * SQRT3_2 / 2 : 0;
      Particle[][] parts = new Particle[heightCount][];
      for (int i = 0; i < heightCount; i++) {
        parts[i] = new Particle[widthCount - ((i + 1) % 2)];
        for (int j = 0; j < widthCount - ((i + 1) % 2); j++) {
          float x = ox + xOff + (((i + 1) % 2) * 0.5 + j) * spacing;
          float y = oy + yOff + i * SQRT3_2 * spacing;
          parts[i][j] = manager.addParticle(x, y, particleTemplate.mass, particleTemplate.getClass());
          if (j > 0) {
            manager.copyStick(parts[i][j - 1], parts[i][j], stickTemplate);
          }
          if (i > 0) {
            if (i % 2 == 0) {
              manager.copyStick(parts[i - 1][j], parts[i][j], stickTemplate);
              manager.copyStick(parts[i - 1][j + 1], parts[i][j], stickTemplate);
            } else {
              if (j > 0) {
                manager.copyStick(parts[i - 1][j - 1], parts[i][j], stickTemplate);
              }
              if (j < widthCount - 1) {
                manager.copyStick(parts[i - 1][j], parts[i][j], stickTemplate);
              }
            }
          }
        }
      }
    }
    catch (ReflectiveOperationException e) {
      println(e.toString());
    }
  }

  public void addRectangle(float ox, float oy, int widthCount, int heightCount, float spacing, boolean centered) {
    addRectangle(ox, oy, widthCount, heightCount, spacing, centered, defaultParticle, defaultStick);
  }

  public void addClassicRectangle(float ox, float oy, int widthCount, int heightCount, float spacing, boolean centered, boolean LR, boolean RL, Particle particleTemplate, Stick stickTemplate) {
    try {
      float xOff = centered ? -(widthCount - 1) * spacing / 2 : 0;
      float yOff = centered ? -(heightCount - 1) * spacing / 2 : 0;
      Particle[][] parts = new Particle[heightCount][widthCount];
      for (int i = 0; i < heightCount; i++) {
        for (int j = 0; j < widthCount; j++) {
          float x = ox + xOff + j * spacing;
          float y = oy + yOff + i * spacing;
          parts[i][j] = manager.copyParticle(x, y, particleTemplate);
          if (j > 0) {
            manager.copyStick(parts[i][j - 1], parts[i][j], stickTemplate);
          }
          if (i > 0) {
            manager.copyStick(parts[i - 1][j], parts[i][j], stickTemplate);
            if (j > 0 && LR) {
              manager.copyStick(parts[i - 1][j - 1], parts[i][j], stickTemplate);
            }
            if(j < widthCount - 1 && RL) {
              manager.copyStick(parts[i - 1][j + 1], parts[i][j], stickTemplate);
            }
          }
        }
      }
    }
    catch (ReflectiveOperationException e) {
      println(e.toString());
    }
  }

  public void addClassicRectangle(float ox, float oy, int widthCount, int heightCount, float spacing, boolean centered, boolean LR, boolean RL) {
    addClassicRectangle(ox, oy, widthCount, heightCount, spacing, centered, LR, RL, defaultParticle, defaultStick);
  }

  public void addWall(float x1, float y1, float x2, float y2) {
    Particle p1 = manager.addParticle(x1, y1);
    p1.isStatic = true;
    Particle p2 = manager.addParticle(x2, y2);
    p2.isStatic = true;
    Stick stick = new WallStick(p1, p2);
    manager.addStick(stick);
  }
}
