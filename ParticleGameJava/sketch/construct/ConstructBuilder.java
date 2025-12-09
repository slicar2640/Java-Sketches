package sketch.construct;

import sketch.ParticleManager;
import sketch.particle.Particle;
import sketch.stick.Stick;
import sketch.stick.WallStick;
import sketch.util.MathUtils;

public class ConstructBuilder {
  ParticleManager manager;

  public ConstructBuilder(ParticleManager manager) {
    this.manager = manager;
  }

  public void addLoop(float cx, float cy, float radius, int count, Particle particleTemplate, Stick stickTemplate) {
    try {
      Particle first = manager.copyParticle(cx + radius, cy, particleTemplate);
      Particle last = first;
      for (int i = 1; i < count; i++) {
        float x = cx + radius * (float) Math.cos((float) i / count * 2 * Math.PI);
        float y = cy + radius * (float) Math.sin((float) i / count * 2 * Math.PI);
        Particle p = manager.copyParticle(x, y, particleTemplate);
        manager.copyStick(last, p, stickTemplate);
        last = p;
      }
      manager.copyStick(last, first, stickTemplate);
    } catch (ReflectiveOperationException e) {
      System.out.println(e.toString());
    }
  }

  public void addLoop(float cx, float cy, float radius, int count) {
    addLoop(cx, cy, radius, count, manager.defaultParticle, manager.defaultStick);
  }

  public void addTriangle(float x, float y, int sideCount, float spacing, Particle particleTemplate,
      Stick stickTemplate) {
    try {
      float yOff = -spacing * (sideCount - 1) * MathUtils.SQRT3_2 * 2 / 3;
      Particle[][] parts = new Particle[sideCount][];
      parts[0] = new Particle[1];
      parts[0][0] = manager.addParticle(x, y + yOff, particleTemplate.mass, particleTemplate.getClass());
      for (int i = 1; i < sideCount; i++) {
        parts[i] = new Particle[i + 1];
        for (int j = 0; j <= i; j++) {
          float x0 = x + ((float) -i / 2 + j) * spacing;
          float y0 = y + yOff + i * MathUtils.SQRT3_2 * spacing;
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
    } catch (ReflectiveOperationException e) {
      System.out.println(e.toString());
    }
  }

  public void addTriangle(float x, float y, int sideCount, float spacing) {
    addTriangle(x, y, sideCount, spacing, manager.defaultParticle, manager.defaultStick);
  }

  public void addRectangle(float ox, float oy, int widthCount, int heightCount, float spacing, boolean centered,
      Particle particleTemplate, Stick stickTemplate) {
    try {
      float xOff = centered ? -(widthCount - 1) * spacing / 2 : 0;
      float yOff = centered ? -(heightCount - 1) * spacing * MathUtils.SQRT3_2 / 2 : 0;
      Particle[][] parts = new Particle[heightCount][];
      for (int i = 0; i < heightCount; i++) {
        parts[i] = new Particle[widthCount - ((i + 1) % 2)];
        for (int j = 0; j < widthCount - ((i + 1) % 2); j++) {
          float x = ox + xOff + (((i + 1) % 2) * 0.5f + j) * spacing;
          float y = oy + yOff + i * MathUtils.SQRT3_2 * spacing;
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
    } catch (ReflectiveOperationException e) {
      System.out.println(e.toString());
    }
  }

  public void addRectangle(float ox, float oy, int widthCount, int heightCount, float spacing, boolean centered) {
    addRectangle(ox, oy, widthCount, heightCount, spacing, centered, manager.defaultParticle, manager.defaultStick);
  }

  public void addClassicRectangle(float ox, float oy, int widthCount, int heightCount, float spacing, boolean centered,
      boolean LR, boolean RL, Particle particleTemplate, Stick stickTemplate) {
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
            if (j < widthCount - 1 && RL) {
              manager.copyStick(parts[i - 1][j + 1], parts[i][j], stickTemplate);
            }
          }
        }
      }
    } catch (ReflectiveOperationException e) {
      System.out.println(e.toString());
    }
  }

  public void addClassicRectangle(float ox, float oy, int widthCount, int heightCount, float spacing, boolean centered,
      boolean LR, boolean RL) {
    addClassicRectangle(ox, oy, widthCount, heightCount, spacing, centered, LR, RL, manager.defaultParticle,
        manager.defaultStick);
  }

  public void addWall(float x1, float y1, float x2, float y2) {
    Particle p1 = manager.addParticle(x1, y1);
    p1.isStatic = true;
    Particle p2 = manager.addParticle(x2, y2);
    p2.isStatic = true;
    Stick stick = new WallStick(p1, p2);
    manager.addStick(stick);
  }

  public static final ConstructDrawFunction drawLoop = (scale, count, sketch, dm) -> {
    dm.noFill();
    dm.stroke(255);
    dm.strokeWeight((float) Math.max(0.9 + Math.sin((float) sketch.frameCount / 20), 0));
    dm.circle(sketch.mouseX, sketch.mouseY, scale);
  };

  public static final ConstructDrawFunction drawTriangle = (scale, count, sketch, dm) -> {
    dm.noFill();
    dm.stroke(255);
    dm.strokeWeight((float) Math.max(0.9 + Math.sin((float) sketch.frameCount / 20), 0));
    float sideLength = scale * (count - 1);
    dm.triangle(sketch.mouseX, sketch.mouseY - sideLength * MathUtils.SQRT3_2 * 2 / 3, sketch.mouseX + sideLength / 2,
        sketch.mouseY + sideLength * MathUtils.SQRT3_2 / 3, sketch.mouseX - sideLength / 2,
        sketch.mouseY + sideLength * MathUtils.SQRT3_2 / 3);
  };

  public static final ConstructDrawFunction drawRectangle = (scale, count, sketch, dm) -> {
    dm.noFill();
    dm.stroke(255);
    dm.strokeWeight((float) Math.max(0.9 + Math.sin((float) sketch.frameCount / 20), 0));
    dm.rect(sketch.mouseX - (count - 1) * scale / 2, sketch.mouseY - (count - 1) * scale * MathUtils.SQRT3_2 / 2,
        (count - 1) * scale, (count - 1) * scale * MathUtils.SQRT3_2);
  };

  public static final ConstructDrawFunction drawClassicRectangle = (scale, count, sketch, dm) -> {
    dm.noFill();
    dm.stroke(255);
    dm.strokeWeight((float) Math.max(0.9 + Math.sin((float) sketch.frameCount / 20), 0));
    dm.rect(sketch.mouseX - (count - 1) * scale / 2, sketch.mouseY - (count - 1) * scale / 2, (count - 1) * scale,
        (count - 1) * scale);
  };
}
